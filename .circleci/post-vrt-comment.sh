#!/bin/bash
# VRT の結果を PR にコメントする。
#
# 使い方: post-vrt-comment.sh <result-dir>
#   <result-dir>/images/*_compare.png : 差分があった画面の比較画像(Roborazzi の _compare.png)
#   <result-dir>/artifact.json        : vrt ジョブの CircleCI artifact 一覧(API のレスポンス)
#
# トークン(環境変数):
#   VRT_COMMENT_TOKEN : ユーザーの PAT(classic / fine-grained)。あれば gh の --attach で
#                       画像を GitHub に直接アップロードしてコメントに埋め込む(gh 2.99.0 以降)。
#                       --attach は OAuth / PAT のトークンでしか使えず、GitHub App の
#                       インストールトークンでは拒否されるため、埋め込みには PAT が必要。
#   GITHUB_APP_TOKEN  : sumire-apps(GitHub App)のインストールトークン。PAT が無いときに使い、
#                       従来どおり CircleCI artifact へのリンク表を貼る。
# アップロードに失敗したときもリンク表にフォールバックするので、コメントは必ず付く。
#
# 必要な環境変数: CIRCLE_PULL_REQUEST(PR の URL)
set -u

RESULT_DIR="${1:?usage: $0 <result-dir>}"
PR_URL="${CIRCLE_PULL_REQUEST:?CIRCLE_PULL_REQUEST is not set}"
GH="${GH_BIN:-gh}"
MAX_ATTACHMENTS=50
COMMENT_FILE="${RESULT_DIR}/comment.txt"

# ---- トークンと投稿者を決める ------------------------------------------------
if [ -n "${VRT_COMMENT_TOKEN:-}" ]; then
  export GH_TOKEN="${VRT_COMMENT_TOKEN}"
  MODE="attach"
  if ! AUTHOR_LOGIN=$("${GH}" api user --jq .login); then
    echo "failed to resolve the user of VRT_COMMENT_TOKEN" >&2
    exit 1
  fi
else
  export GH_TOKEN="${GITHUB_APP_TOKEN:?neither VRT_COMMENT_TOKEN nor GITHUB_APP_TOKEN is set}"
  MODE="link"
  AUTHOR_LOGIN="${VRT_COMMENT_BOT_LOGIN:-sumire-apps}"
  echo "VRT_COMMENT_TOKEN is not set: posting artifact links instead of inline images"
fi
echo "mode=${MODE} author=${AUTHOR_LOGIN}"

# ---- 比較画像と artifact の URL を集める ------------------------------------
IMAGES=()
while IFS= read -r f; do
  [ -n "${f}" ] && IMAGES+=("${f}")
done < <(find "${RESULT_DIR}/images" -maxdepth 1 -name '*_compare.png' 2>/dev/null | sort)

declare -A ARTIFACT_URL=()
if [ -s "${RESULT_DIR}/artifact.json" ]; then
  while IFS=$'\t' read -r name url; do
    [ -n "${name}" ] && ARTIFACT_URL["${name}"]="${url}"
  done < <(jq -r '.items[]? | [(.path | split("/") | last), .url] | @tsv' "${RESULT_DIR}/artifact.json")
fi

display_name() {
  basename "$1" _compare.png
}

artifact_link() {
  local file name url
  file=$(basename "$1")
  name=$(display_name "$1")
  url="${ARTIFACT_URL[${file}]:-}"
  if [ -n "${url}" ]; then
    printf '[%s](%s)' "${name}" "${url}"
  else
    printf '%s' "${name}"
  fi
}

# ---- 本文を作る ---------------------------------------------------------------
# 従来形式: CircleCI artifact へのリンク表
build_link_body() {
  echo "## VRT Result"
  echo
  if [ "${#IMAGES[@]}" -eq 0 ]; then
    echo "not changed screen"
    return
  fi
  echo "| changed |"
  echo "|-------|"
  local img
  for img in "${IMAGES[@]}"; do
    printf '| %s |\n' "$(artifact_link "${img}")"
  done
}

# 埋め込み形式: 本文で画像を参照し、同じパスを --attach で渡す。
# gh は本文中の参照を(絶対パスで照合して)アップロード先 URL に書き換える。
build_attach_body() {
  echo "## VRT Result"
  echo
  printf '%d screen(s) changed.\n' "${#IMAGES[@]}"
  local img name i=0
  for img in "${IMAGES[@]}"; do
    i=$((i + 1))
    name=$(display_name "${img}")
    echo
    printf '### %s\n' "${name}"
    echo
    if [ "${i}" -le "${MAX_ATTACHMENTS}" ]; then
      printf '![%s](%s)\n' "${name}" "${img}"
    else
      # --attach は 1 コマンド 50 件まで。超えた分は artifact へのリンクにする
      printf '%s (artifact)\n' "$(artifact_link "${img}")"
    fi
  done
  if [ "${#ARTIFACT_URL[@]}" -gt 0 ]; then
    echo
    echo "<details><summary>CircleCI artifacts</summary>"
    echo
    for img in "${IMAGES[@]}"; do
      printf -- '- %s\n' "$(artifact_link "${img}")"
    done
    echo
    echo "</details>"
  fi
}

# ---- 投稿する -----------------------------------------------------------------
has_own_comment() {
  "${GH}" pr view "${PR_URL}" --comments --json comments \
    | jq -r --arg login "${AUTHOR_LOGIN}" '[.comments[].author.login] | any(. == $login)'
}

# 既に自分のコメントがあれば --edit-last で上書きし、無ければ新規に付ける
post_comment() {
  local edit_flag=()
  if [ "$(has_own_comment)" = "true" ]; then
    edit_flag=(--edit-last)
  fi
  "${GH}" pr comment "${PR_URL}" -F "${COMMENT_FILE}" ${edit_flag[@]+"${edit_flag[@]}"} "$@"
}

if [ "${MODE}" = "attach" ] && [ "${#IMAGES[@]}" -gt 0 ]; then
  build_attach_body > "${COMMENT_FILE}"
  attach_args=()
  i=0
  for img in "${IMAGES[@]}"; do
    i=$((i + 1))
    [ "${i}" -le "${MAX_ATTACHMENTS}" ] || break
    attach_args+=(--attach "${img}#$(display_name "${img}")")
  done
  if post_comment "${attach_args[@]}"; then
    echo "posted VRT result with $(( ${#attach_args[@]} / 2 )) inline image(s)"
    exit 0
  fi
  echo "warning: posting with attachments failed, falling back to artifact links" >&2
fi

build_link_body > "${COMMENT_FILE}"
post_comment
echo "posted VRT result with artifact links"
