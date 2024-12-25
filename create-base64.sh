#!/bin/sh

touch tmp-compare.txt
search_file=$(ls ./app/build/outputs/roborazzi/*_compare.png)
for compare_file in $search_file
do
  base64 -w 0 "$compare_file" > tmp-compare.txt
done
touch compare.json
jq -R '.' tmp-compare.txt | jq --slurp >> compare.json
