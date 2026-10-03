#!/bin/bash
# VRT の比較元(target branch)のスクリーンショットを、develop/master への push 時に走る
# save-screenshot ジョブの artifact から取得する。
#
# 使い方: fetch-base-screenshots.sh <target-branch> <output-dir>
#
# target branch の HEAD と同じコミットで成功した save-screenshot ジョブが見つかれば、
# その artifact(collect-screenshots.sh でまとめた Compose Preview Screenshot Testing の
# 参照画像。artifact の path は screenshots/<module>/...)を output-dir にダウンロードして 0 を返す。
# 見つからない・取得に失敗したとき、screenshots/ 以下の artifact が 1 つも無いとき
# (Roborazzi 時代の artifact しか無いときなど)は 1 を返し、呼び出し側は撮影にフォールバックする。
#
# 必要な環境変数: CIRCLE_TOKEN, CIRCLE_PROJECT_USERNAME, CIRCLE_PROJECT_REPONAME
set -u

TARGET_BRANCH="${1:?usage: $0 <target-branch> <output-dir>}"
OUT_DIR="${2:?usage: $0 <target-branch> <output-dir>}"
API="${CIRCLE_API_BASE:-https://circleci.com/api/v2}"
PROJECT_SLUG="gh/${CIRCLE_PROJECT_USERNAME}/${CIRCLE_PROJECT_REPONAME}"
WORKFLOW_NAME="save-screenshot"
JOB_NAME="save-screenshot"

if [ -z "${CIRCLE_TOKEN:-}" ]; then
  echo "CIRCLE_TOKEN is not set" >&2
  exit 1
fi

api() {
  # -L: artifact の URL はリダイレクトされることがあるので追従する
  curl -sSfL --retry 2 -H "Circle-Token: ${CIRCLE_TOKEN}" "$@"
}

# ダウンロードした画像が本物か確かめる。空ファイルやエラーページを比較元にすると
# CompareScreenshots.java が読めずに落ちるので、怪しければ呼び出し側で撮影にフォールバックさせる
check_downloaded_file() {
  local file="$1"
  if [ ! -s "${file}" ]; then
    echo "downloaded file is empty: ${file}" >&2
    return 1
  fi
  case "${file}" in
    *.png)
      if [ "$(head -c 8 "${file}" | od -An -tx1 | tr -d ' \n')" != "89504e470d0a1a0a" ]; then
        echo "downloaded file is not a PNG: ${file}" >&2
        head -c 200 "${file}" >&2; echo >&2
        return 1
      fi
      ;;
  esac
  return 0
}

# 1. target branch の HEAD を解決する
git fetch -q origin "${TARGET_BRANCH}" \
  || echo "warning: git fetch failed, using origin/${TARGET_BRANCH} as cloned" >&2
if ! TARGET_SHA=$(git rev-parse --verify -q "origin/${TARGET_BRANCH}"); then
  echo "cannot resolve origin/${TARGET_BRANCH}" >&2
  exit 1
fi
echo "target: ${TARGET_BRANCH} @ ${TARGET_SHA}"

# 2. そのコミットの pipeline を探す(新しい順、最大 3 ページ)
branch_query=$(jq -rn --arg b "${TARGET_BRANCH}" '$b | @uri')
pipeline_ids=""
page_token=""
for _ in 1 2 3; do
  url="${API}/project/${PROJECT_SLUG}/pipeline?branch=${branch_query}"
  if [ -n "${page_token}" ]; then url="${url}&page-token=${page_token}"; fi
  if ! json=$(api "${url}"); then
    echo "failed to list pipelines of ${TARGET_BRANCH}" >&2
    exit 1
  fi
  pipeline_ids=$(printf '%s' "${json}" \
    | jq -r --arg sha "${TARGET_SHA}" '.items[] | select(.vcs.revision == $sha) | .id')
  if [ -n "${pipeline_ids}" ]; then break; fi
  page_token=$(printf '%s' "${json}" | jq -r '.next_page_token // empty')
  if [ -z "${page_token}" ]; then break; fi
done
if [ -z "${pipeline_ids}" ]; then
  echo "no pipeline found for ${TARGET_SHA} on ${TARGET_BRANCH}" >&2
  exit 1
fi

# 3. 成功した save-screenshot ジョブの job_number を探す
job_number=""
for pipeline_id in ${pipeline_ids}; do
  workflow_ids=$(api "${API}/pipeline/${pipeline_id}/workflow" \
    | jq -r --arg name "${WORKFLOW_NAME}" \
        '.items[] | select(.name == $name and .status == "success") | .id')
  for workflow_id in ${workflow_ids}; do
    job_number=$(api "${API}/workflow/${workflow_id}/job" \
      | jq -r --arg name "${JOB_NAME}" \
          '[.items[] | select(.name == $name and .status == "success" and .job_number != null)][0].job_number // empty')
    if [ -n "${job_number}" ]; then break 2; fi
  done
done
if [ -z "${job_number}" ]; then
  echo "no successful ${JOB_NAME} job for ${TARGET_SHA} (still running or failed?)" >&2
  exit 1
fi
echo "reusing artifacts of ${JOB_NAME} job #${job_number}"

# 4. artifact をダウンロードする
mkdir -p "${OUT_DIR}"
count=0
page_token=""
while :; do
  url="${API}/project/${PROJECT_SLUG}/${job_number}/artifacts"
  if [ -n "${page_token}" ]; then url="${url}?page-token=${page_token}"; fi
  if ! json=$(api "${url}"); then
    echo "failed to list artifacts of job #${job_number}" >&2
    exit 1
  fi
  while IFS=$'\t' read -r path download_url; do
    [ -n "${path}" ] || continue
    # save-screenshot は store_artifacts の destination を screenshots にしている。
    # それ以外(Roborazzi 時代の app/build/outputs/roborazzi/... など)は比較元に使えないので無視する
    rel="${path#screenshots/}"
    if [ "${rel}" = "${path}" ]; then continue; fi
    mkdir -p "$(dirname "${OUT_DIR}/${rel}")"
    if ! api -o "${OUT_DIR}/${rel}" "${download_url}"; then
      echo "failed to download ${path}" >&2
      exit 1
    fi
    if ! check_downloaded_file "${OUT_DIR}/${rel}"; then
      exit 1
    fi
    count=$((count + 1))
  done < <(printf '%s' "${json}" | jq -r '.items[] | [.path, .url] | @tsv')
  page_token=$(printf '%s' "${json}" | jq -r '.next_page_token // empty')
  if [ -z "${page_token}" ]; then break; fi
done
if [ "${count}" -eq 0 ]; then
  echo "no screenshots/ artifacts found in job #${job_number}" >&2
  exit 1
fi
echo "downloaded ${count} file(s) into ${OUT_DIR}"
find "${OUT_DIR}" -type f | sort
