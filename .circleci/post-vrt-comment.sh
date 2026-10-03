#!/bin/bash
# VRT の結果を PR にコメントする。
#
# 使い方: post-vrt-comment.sh <result-dir>
#   <result-dir>/images/*_compare.png : 差分があった画面の比較画像(Roborazzi の _compare.png)
#   <result-dir>/artifact.json        : vrt ジョブの CircleCI artifact 一覧(API のレスポンス)
#
# 画像の埋め込み方法は 3 段構え。上から順に試して、失敗したら次へ落ちる。
#   1. VRT_COMMENT_TOKEN(ユーザーの PAT)があれば gh の --attach で GitHub にアップロードする
#      (gh 2.99.0 以降)。画像は GitHub 側に残る。gh の --attach は OAuth / PAT のトークンしか
#      受け付けず、GitHub App のトークンはクライアント側の allowlist で拒否される。
#      App トークンで gh が使うアップロードエンドポイントを直接叩く方法も試したが、
#      サーバー側でも拒否されたため、ボット(GITHUB_APP_TOKEN)ではアップロードできない。
#   2. PAT が無ければ、vrt ジョブが store_artifacts した CircleCI artifact の URL を
#      そのまま ![](url) で本文に埋め込む。公開プロジェクトの artifact は認証無しで取れるので
#      GitHub(camo)が画像を表示できる。artifact は 30 日で消えるため古いコメントの画像は後で壊れる。
#   3. artifact の一覧が取れていなければ、名前だけのリンク表を貼る。
# コメントの投稿自体は常に GITHUB_APP_TOKEN(sumire-apps)または PAT で行う。
#
# 必要な環境変数: CIRCLE_PULL_REQUEST(PR の URL)
set -u

RESULT_DIR="${1:?usage: $0 <result-dir>}"
PR_URL="${CIRCLE_PULL_REQUEST:?CIRCLE_PULL_REQUEST is not set}"
GH="${GH_BIN:-gh}"
MAX_ATTACHMENTS=50
COMMENT_FILE="${RESULT_DIR}/comment.txt"

# PR の URL から owner/repo と番号を取る(https://github.com/<owner>/<repo>/pull/<n>)
OWNER_REPO=$(printf '%s' "${PR_URL}" | sed -E 's#^https?://[^/]+/([^/]+/[^/]+)/pull/.*$#\1#')
PR_NUMBER=$(printf '%s' "${PR_URL}" | sed -E 's#^.*/pull/([0-9]+).*$#\1#')
if [ "${OWNER_REPO}" = "${PR_URL}" ] || [ -z "${OWNER_REPO}" ] || [ "${PR_NUMBER}" = "${PR_URL}" ]; then
  echo "cannot parse owner/repo/number from ${PR_URL}" >&2
  exit 1
fi

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
  MODE="bot"
  AUTHOR_LOGIN="${VRT_COMMENT_BOT_LOGIN:-sumire-apps}"
fi
echo "mode=${MODE} author=${AUTHOR_LOGIN} repo=${OWNER_REPO}"

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

# 画像ごとの埋め込み先。attach モードではローカルパス(gh が書き換える)、
# bot モードでは CircleCI artifact の URL が入る
declare -A IMAGE_URL=()

display_name() {
  basename "$1" _compare.png
}

# alt text として安全な形にする(gh の escapeAlt と同じ文字を潰す)
escape_alt() {
  printf '%s' "$1" | sed -e 's/\\/\\\\/g' -e 's/\[/\\[/g' -e 's/\]/\\]/g' | tr '\n\r' '  '
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

# 埋め込み形式: IMAGE_URL にある画像は本文に埋め込み、無いものは artifact へのリンクにする
build_image_body() {
  echo "## VRT Result"
  echo
  printf '%d screen(s) changed.\n' "${#IMAGES[@]}"
  local img name
  for img in "${IMAGES[@]}"; do
    name=$(display_name "${img}")
    echo
    printf '### %s\n' "${name}"
    echo
    if [ -n "${IMAGE_URL[${img}]:-}" ]; then
      printf '![%s](%s)\n' "$(escape_alt "${name}")" "${IMAGE_URL[${img}]}"
    else
      # --attach の 50 件上限を超えた分や、artifact の URL が取れなかった分
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
# 自分(投稿者)の最後のコメントの ID を REST で探す。
# gh pr view --json comments / --edit-last は App トークンだと自分のコメントを
# 見つけられず新規コメントが増えてしまうため、REST の user.login で判定する。
# Bot の login は REST では "<name>[bot]" になるので両方を見る
find_own_comment_id() {
  "${GH}" api "repos/${OWNER_REPO}/issues/${PR_NUMBER}/comments?per_page=100" \
    | jq -r --arg login "${AUTHOR_LOGIN}" \
        '[.[] | select(.user.login == $login or .user.login == ($login + "[bot]"))] | last | .id // empty'
}

# REST で投稿する(添付なし)。既に自分のコメントがあれば上書き、無ければ新規
post_comment_rest() {
  local id
  id=$(find_own_comment_id)
  if [ -n "${id}" ]; then
    "${GH}" api -X PATCH "repos/${OWNER_REPO}/issues/comments/${id}" -F "body=@${COMMENT_FILE}" --jq .html_url
  else
    "${GH}" api -X POST "repos/${OWNER_REPO}/issues/${PR_NUMBER}/comments" -F "body=@${COMMENT_FILE}" --jq .html_url
  fi
}

# gh pr comment で投稿する(--attach 用)。既に自分のコメントがあれば --edit-last
post_comment_gh() {
  local edit_flag=()
  if [ -n "$(find_own_comment_id)" ]; then
    edit_flag=(--edit-last)
  fi
  "${GH}" pr comment "${PR_URL}" -F "${COMMENT_FILE}" ${edit_flag[@]+"${edit_flag[@]}"} "$@"
}

if [ "${#IMAGES[@]}" -gt 0 ]; then
  case "${MODE}" in
    attach)
      # gh に本文中の参照(ローカルパス)をアップロード先 URL へ書き換えさせる
      attach_args=()
      i=0
      for img in "${IMAGES[@]}"; do
        i=$((i + 1))
        [ "${i}" -le "${MAX_ATTACHMENTS}" ] || break
        IMAGE_URL["${img}"]="${img}"
        attach_args+=(--attach "${img}#$(display_name "${img}")")
      done
      build_image_body > "${COMMENT_FILE}"
      if post_comment_gh "${attach_args[@]}"; then
        echo "posted VRT result with $(( ${#attach_args[@]} / 2 )) inline image(s) via gh --attach"
        exit 0
      fi
      echo "warning: posting with --attach failed, falling back to artifact links" >&2
      ;;
    bot)
      # artifact の URL を画像として埋め込む
      for img in "${IMAGES[@]}"; do
        url="${ARTIFACT_URL[$(basename "${img}")]:-}"
        [ -n "${url}" ] && IMAGE_URL["${img}"]="${url}"
      done
      if [ "${#IMAGE_URL[@]}" -gt 0 ]; then
        build_image_body > "${COMMENT_FILE}"
        if post_comment_rest; then
          echo "posted VRT result with ${#IMAGE_URL[@]} inline image(s) from CircleCI artifacts"
          exit 0
        fi
        echo "warning: posting the comment with artifact images failed, falling back to the link table" >&2
      else
        echo "warning: no artifact urls available, falling back to the link table" >&2
      fi
      ;;
  esac
fi

build_link_body > "${COMMENT_FILE}"
post_comment_rest
echo "posted VRT result with artifact links"
