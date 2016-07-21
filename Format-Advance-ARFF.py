import cPickle 
import io
from GitHub_API_Collect import openPickledData
from os import listdir
from os.path import isdir, join

repoCollection = openPickledData("repoCollection") #used pickle

class FormatFile:
	def __init__(self,rawData):
		self.attributes = ["numOfFiles: numeric", "additions: numeric", "subtractions: numeric", "filesAdded: numeric", "filesRemoved: numeric", "filesRenamed: numeric", "filesModified: numeric", "B-Entropy: numeric","B-Cross-Entropy: numeric","A-Entropy: numeric","A-Cross-entropy: numeric","lowSig: numeric","medSig: numeric","highSig: numeric","ARGUMENTS: numeric", "ARRAY_ACCESS: numeric", "ARRAY_CREATION: numeric", "ARRAY_INITIALIZER: numeric", "ARRAY_TYPE: numeric", "ASSERT_STATEMENT: numeric", "ASSIGNMENT: numeric", "FIELD: numeric", "BLOCK: numeric", "BLOCK_COMMENT: numeric", "BODY: numeric", "BOOLEAN_LITERAL: numeric", "BREAK_STATEMENT: numeric", "CAST_EXPRESSION: numeric", "CATCH_CLAUSE: numeric", "CATCH_CLAUSES: numeric", "CHARACTER_LITERAL: numeric", "CLASS: numeric", "CLASS_INSTANCE_CREATION: numeric", "COMPILATION_UNIT: numeric", "CONDITIONAL_EXPRESSION: numeric", "CONSTRUCTOR_INVOCATION: numeric", "CONTINUE_STATEMENT: numeric", "DO_STATEMENT: numeric", "ELSE_STATEMENT: numeric", "EMPTY_STATEMENT: numeric", "FOREACH_STATEMENT: numeric", "FIELD_ACCESS: numeric", "FIELD_DECLARATION: numeric", "FINALLY: numeric", "FOR_STATEMENT: numeric", "IF_STATEMENT: numeric", "INFIX_EXPRESSION: numeric", "INSTANCEOF_EXPRESSION: numeric", "JAVADOC: numeric", "LABELED_STATEMENT: numeric", "LINE_COMMENT: numeric", "METHOD: numeric", "METHOD_DECLARATION: numeric", "METHOD_INVOCATION: numeric", "MODIFIER: numeric", "MODIFIERS: numeric", "NULL_LITERAL: numeric", "NUMBER_LITERAL: numeric", "PARAMETERIZED_TYPE: numeric", "PARAMETERS: numeric", "PARAMETER: numeric", "POSTFIX_EXPRESSION: numeric", "PREFIX_EXPRESSION: numeric", "PRIMITIVE_TYPE: numeric", "QUALIFIED_NAME: numeric", "QUALIFIED_TYPE: numeric", "RETURN_STATEMENT: numeric", "ROOT_NODE: numeric", "SIMPLE_NAME: numeric", "SINGLE_TYPE: numeric", "STRING_LITERAL: numeric", "SUPER_INTERFACE_TYPES: numeric", "SWITCH_CASE: numeric", "SWITCH_STATEMENT: numeric", "SYNCHRONIZED_STATEMENT: numeric", "THEN_STATEMENT: numeric", "THROW: numeric", "THROW_STATEMENT: numeric", "TRY_STATEMENT: numeric", "TYPE_PARAMETERS: numeric", "TYPE_DECLARATION: numeric", "TYPE_LITERAL: numeric", "TYPE_PARAMETER: numeric","VARIABLE_DECLARATION_STATEMENT: numeric","WHILE_STATEMENT: numeric","WILDCARD_TYPE: numeric","FOR_INIT: numeric","FOR_INCR: numeric","class: {Accepted, Rejected, Reverted}"]
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
					f = open(path+"/"+repo+"/"+pull+"/"+pull+"_INFO.txt",'r')
					info = f.read().split("\n")
					print info[6]
					lineFormatted = info[7][11:] + "," + info[15][10:] + "," + info[2][13:] + "," + info[1][6:] + "," + info[16][8:] + "," + info[12][8:] + "," + info[14][9:] + "," + info[19] + "," +info[13][7:]
					#print info[14]
					#print info[14][9:]
					print lineFormatted
					self.lines.append(lineFormatted)

file = FormatFile(repoCollection)
file.formatRelations()
file.formatAttributes()
file.getDatafromTxt()
with io.open('Weka/Data-Complete.txt', 'w') as f:
    f.writelines(line + u'\n' for line in file.lines)
print "done"