import pickle
from Pull import *
from collections import OrderedDict
import indicoio
from indicoio.custom import Collection

indicoio.config.api_key = '5ede96b493001db135d333c31d4b2e00'

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

def formatData(pulls):
	for pull in pulls:
		print pull

def main():
	output = open('pulls.pkl','rb')
	pulls = pickle.load(output)
	formatData(pulls)
	#analyzeTitles(pulls)

def createModel():
	collection = Collection("Bug Indicators")
	# Add Data
	collection.add_data([["text1", "label1"], ["text2", "label2"]])
	collection.train()
	collection.wait()
	# Done! Start analyzing text
	print collection.predict("indico is so easy to use!")

main()

#createModel()