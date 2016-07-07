import cPickle 
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

	def formatAttributes():
		pass

	def formatData(self):
		for pull in self.pulls:
			lineFormatted = str(pull.numOfFiles) + "," + pull.additions + "," + pull.subtractions + "," + str(pull.added) + "," + str(pull.removed) + "," + str(pull.renamed) + "," + str(pull.modified) + "," + pull.result
			self.lines.append(lineFormatted)
file = FormatFile(repoCollection)
file.formatData()
