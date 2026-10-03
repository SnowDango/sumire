#!/bin/bash
# Compose Preview Screenshot Testing の update タスク(./gradlew updateDebugScreenshotTest)が
# 各モジュールに書き出した参照画像({module}/src/screenshotTestDebug/reference)を 1 か所にまとめる。
#
# 使い方: collect-screenshots.sh <output-dir>
#   <output-dir>/<module のパス>/ 以下に、reference 以下の階層のままコピーする
#   (例: presenter/history/src/screenshotTestDebug/reference/foo.png -> <output-dir>/presenter/history/foo.png)
#
# 1 枚も見つからなければ 1 を返す。update タスクが成功して何も撮れていないのは異常なので、
# 空の比較元・比較先で VRT が「変更なし」になるのを防ぐ。
set -euo pipefail

OUT_DIR="${1:?usage: $0 <output-dir>}"
REFERENCE_SUFFIX="src/screenshotTestDebug/reference"

mkdir -p "${OUT_DIR}"
count=0
while IFS= read -r ref_dir; do
  module="${ref_dir#./}"
  module="${module%/${REFERENCE_SUFFIX}}"
  n=$(find "${ref_dir}" -type f -name '*.png' | wc -l)
  if [ "${n}" -eq 0 ]; then
    continue
  fi
  mkdir -p "${OUT_DIR}/${module}"
  cp -R "${ref_dir}/." "${OUT_DIR}/${module}/"
  echo "${module}: ${n} image(s)"
  count=$((count + n))
done < <(find . -path ./.git -prune -o -type d -path "*/${REFERENCE_SUFFIX}" -print | sort)

if [ "${count}" -eq 0 ]; then
  echo "no reference images found in */${REFERENCE_SUFFIX}" >&2
  exit 1
fi
echo "collected ${count} image(s) into ${OUT_DIR}"
