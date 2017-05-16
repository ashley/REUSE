#!/bin/bash
X=21
while [ $X -le 50 ]
do
	defects4j checkout -p Lang -v $X"f" -w $X"f"
	defects4j checkout -p Lang -v $X"b" -w $X"b"		
	X=$((X+1))
done
