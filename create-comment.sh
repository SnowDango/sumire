#!/bin/sh

comment_file=comment.md
touch $comment_file
length=$(cat compare.json | jq -c '.[] | length')
echo $length
if [ $length -gt 0 ]; then
  echo "VRT Result" > $comment_file
  echo "| changed |" >> $comment_file
  echo "|-------|" >> $comment_file
  cat compare.json | \
  jq -c '.[]' | while read artifact; do
    echo "| ![compare.png](data:image/jpeg;base64,$artifact) |" >> $comment_file
    # echo "| [$(echo $artifact | jq -r '.path')]($(echo $artifact | jq -r '.url')) |" >> $comment_file
  done
else
  echo "VRT Result" > $comment_file
  echo "not changed screen" >> $comment_file
fi