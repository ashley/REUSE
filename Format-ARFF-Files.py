import cPickle 
import io
from GitHub_API_Collect import openPickledData

repoCollection = openPickledData("repoCollection")

class FormatFile:
	def __init__(self,rawData):
		self.attributes = ["numOfFiles", "additions", "subtractions", "filesAdded", "filesRemoved", "filesRenamed", "filesModified", "class"]
		self.pulls = []
		self.lines = []
		for pulls in repoCollection:
			for pull in pulls:
				self.pulls.append(pull)

	def formatRelations(self):
		self.lines.append("@relation 'decision'")

	def formatAttributes(self):
		for i in self.attributes:
			self.lines.append("@attribute " + i + " numeric")

	def formatData(self):
		self.lines.append("@data")
		for pull in self.pulls:
			lineFormatted = str(pull.numOfFiles) + "," + pull.additions + "," + pull.subtractions + "," + str(pull.added) + "," + str(pull.removed) + "," + str(pull.renamed) + "," + str(pull.modified) + "," + pull.result
			self.lines.append(lineFormatted)
file = FormatFile(repoCollection)
file.formatRelations()
file.formatAttributes()
file.formatData()
with io.open('/Users/ashleychen/Desktop/test.txt', 'w', encoding='unicode-escape') as f:
    f.writelines(line + u'\n' for line in file.lines)
print "done"