import cPickle 
import io
from GitHub_API_Collect import openPickledData
from os import listdir
from os.path import isdir, join

def getDatafromTxt():
	dataLines = []
	lineFormatted = ""
	path = "/Users/ashleychen/Desktop/REUSE/REUSE/Repos"
	repos = [f for f in listdir(path) if isdir(join(path, f))]
	print len(repos)
	for repo in repos:
		pulls = listdir(path+"/"+repo)
		for pull in pulls:
			f = open(path+"/"+repo+"/"+pull+"/"+"INFO.txt",'r')
			info = f.read().split("\n")
			dataLines.append(info[7][11:] + "," + info[15][10:] + "," + info[2][13:] + "," + info[1][6:] + "," + info[16][8:] + "," + info[12][8:] + "," + info[14][9:] + "," + info[13][7:])
	print dataLines
	return dataLines
