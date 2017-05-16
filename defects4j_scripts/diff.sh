#!/bin/bash
X=1
while [ $X -le 20 ]
do
	diff --brief -Nr $X"f" $X"b" 
	X=$((X+1))
	echo  
done
