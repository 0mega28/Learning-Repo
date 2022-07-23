#!/usr/bin/env bash

[[ $# -eq 1 ]] || {
    echo "Usage: $0 <input-file>"
    exit 1
}

input_file=$1
output_file=${input_file%.*}.js

cp "$input_file" "$output_file" || exit

# Delete unnecessary start-end strings
sed -ir '/hi bhai/d; /bye bhai/d' "$output_file"

# bol bhai to console.log
sed -ir 's/bol bhai \(.*\);/console.log\(\1\);/' "$output_file"

# bhai ye hai to let
sed -ir 's/bhai ye hai/let/' "$output_file"

# agar bhai to if
sed -ir 's/agar bhai/if/' "$output_file"

# nahi to bhai to else if
sed -ir 's/nahi to bhai/else if/' "$output_file"

# warna bhai to else
sed -ir 's/warna bhai/else/' "$output_file"

# jab tak bhai to while
sed -ir 's/jab tak bhai/while/' "$output_file"

# bas kar bhai to break
sed -ir 's/bas kar bhai/break/' "$output_file"

# agla dekh bhai to continue
sed -ir 's/agla dekh bhai/continue/' "$output_file"

# Remove start two spaces
sed -ir 's/^\s\s//' "$output_file"

# run the file
node "$output_file"
rm "$output_file"
rm "${output_file}r"

