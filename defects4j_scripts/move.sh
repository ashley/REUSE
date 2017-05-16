#!/bin/bash
while IFS='' read -r line || [[ -n "$line" ]]; do
    NAME=${line##*/}
    ID=${line:0:2}
    VERSION=${line:2:1}
    echo $ID$NAME$VERSION
    mv "/Users/ashleychen/desktop/testing/"$line "/Users/ashleychen/desktop/testing/"$VERSION"/"$ID$NAME    
done < "$1"
