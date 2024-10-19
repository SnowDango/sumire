#!/bin/sh

touch comment.txt
length=$(cat artifact.json | jq -c '.items | length')
echo $length
if [ $length -gt 0 ]; then
  echo "VRT Result" > comment.txt
  echo "| changed |" >> comment.txt
  echo "|-------|" >> comment.txt
  cat artifact.json | \
  jq -c '.items[]' | while read artifact; do
    echo "| [$(echo $artifact | jq -r '.path')]($(echo $artifact | jq -r '.url')) |" >> comment.txt
  done
else
  echo "VRT Result" > comment.txt
  echo "not changed screen" >> comment.txt
fi