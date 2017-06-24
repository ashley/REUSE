#!/bin/bash
cd Repos
cd derby_buggy
n=`ls -l | grep -v ^l | wc -l`-1
m=1
count=$((n-m))
rm -r -f $count
cd ..
cd ..
echo $count
