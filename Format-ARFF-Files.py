import cPickle 
import io
from GitHub_API_Collect import openPickledData
from os import listdir
from os.path import isdir, join

repoCollection = openPickledData("repoCollection")

class FormatFile:
	def __init__(self,rawData):
		self.attributes = ["numOfFiles: numeric", "additions: numeric", "subtractions: numeric", "filesAdded: numeric", "filesRemoved: numeric", "filesRenamed: numeric", "filesModified: numeric", "lowSig: numeric","medSig: numeric","highSig: numeric","class: {Accepted, Rejected, Reverted}"]
		self.pulls = []
		self.lines = []
		for pulls in repoCollection:
			for pull in pulls:
				self.pulls.append(pull)

	def formatRelations(self):
		self.lines.append("@relation 'decision'")

	def formatAttributes(self):
		for i in self.attributes:
			self.lines.append("@attribute " + i)

	def formatData(self):
		self.lines.append("@data")
		for pull in self.pulls:
			lineFormatted = str(pull.numOfFiles) + "," + pull.additions + "," + pull.subtractions + "," + str(pull.added) + "," + str(pull.removed) + "," + str(pull.renamed) + "," + str(pull.modified) + "," + pull.result
			#self.lines.append(lineFormatted)

	def getDatafromTxt(self):
		self.lines.append("@data")
		path = "/Users/ashleychen/Desktop/REUSE/REUSE/Repos"
		repos = [f for f in listdir(path) if isdir(join(path, f))]
		for repo in repos:
			pulls = listdir(path+"/"+repo)
			for pull in pulls:
				if pull != ".DS_Store":
					f = open(path+"/"+repo+"/"+pull+"/"+"INFO.txt",'r')
					info = f.read().split("\n")
					print info[6]
					lineFormatted = info[7][11:] + "," + info[15][10:] + "," + info[2][13:] + "," + info[1][6:] + "," + info[16][8:] + "," + info[12][8:] + "," + info[14][9:] + "," + info[18] + "," +info[13][7:]
					#print lineFormatted
					self.lines.append(lineFormatted)

file = FormatFile(repoCollection)
file.formatRelations()
file.formatAttributes()
file.getDatafromTxt()
with io.open('Weka/Data-Complete.txt', 'w') as f:
    f.writelines(line + u'\n' for line in file.lines)
print "done"