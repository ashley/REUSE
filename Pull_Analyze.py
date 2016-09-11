import pickle
from Pull import *
from collections import OrderedDict

def frequentWord(arr):
	diction = {}
	for word in arr:
		if word in diction:
			diction[word] += 1
		else:
			diction[word] = 1
	return findCommonWords(diction,5)

def findCommonWords(diction, freq):
	comm = []
	for key in sorted(diction.iterkeys()):
		comm.append(key)
	ordered = OrderedDict(sorted(diction.items(), key=lambda (k, v): v))
	print "Most frequent strings: "
	for i in range(len(ordered)-1,len(ordered)-freq-1,-1):
		print ordered.keys()[i]

def analyzeTitles(pulls):
	titlesComb = ""
	for pull in pulls.values():
		titlesComb += pull.title + " "
	frequentWord(titlesComb.split(" "))

def main():
	output = open('pulls.pkl','rb')
	pulls = pickle.load(output)
	analyzeTitles(pulls)

main()