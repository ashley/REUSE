import cPickle 
import io
from GitHub_API_Collect import openPickledData
from os import listdir
from os.path import isdir, join

def defaultAttributes():
	listOfAttributes =  ["numOfFiles: numeric", "additions: numeric", "subtractions: numeric", "filesAdded: numeric", "filesRemoved: numeric", "filesRenamed: numeric", "filesModified: numeric", "B-Entropy: numeric","B-Cross-Entropy: numeric","A-Entropy: numeric","A-Cross-entropy: numeric","lowSig: numeric","medSig: numeric","highSig: numeric","ARGUMENTS: numeric", "ARRAY_ACCESS: numeric", "ARRAY_CREATION: numeric", "ARRAY_INITIALIZER: numeric", "ARRAY_TYPE: numeric", "ASSERT_STATEMENT: numeric", "ASSIGNMENT: numeric", "FIELD: numeric", "BLOCK: numeric", "BLOCK_COMMENT: numeric", "BODY: numeric", "BOOLEAN_LITERAL: numeric", "BREAK_STATEMENT: numeric", "CAST_EXPRESSION: numeric", "CATCH_CLAUSE: numeric", "CATCH_CLAUSES: numeric", "CHARACTER_LITERAL: numeric", "CLASS: numeric", "CLASS_INSTANCE_CREATION: numeric", "COMPILATION_UNIT: numeric", "CONDITIONAL_EXPRESSION: numeric", "CONSTRUCTOR_INVOCATION: numeric", "CONTINUE_STATEMENT: numeric", "DO_STATEMENT: numeric", "ELSE_STATEMENT: numeric", "EMPTY_STATEMENT: numeric", "FOREACH_STATEMENT: numeric", "FIELD_ACCESS: numeric", "FIELD_DECLARATION: numeric", "FINALLY: numeric", "FOR_STATEMENT: numeric", "IF_STATEMENT: numeric", "INFIX_EXPRESSION: numeric", "INSTANCEOF_EXPRESSION: numeric", "JAVADOC: numeric", "LABELED_STATEMENT: numeric", "LINE_COMMENT: numeric", "METHOD: numeric", "METHOD_DECLARATION: numeric", "METHOD_INVOCATION: numeric", "MODIFIER: numeric", "MODIFIERS: numeric", "NULL_LITERAL: numeric", "NUMBER_LITERAL: numeric", "PARAMETERIZED_TYPE: numeric", "PARAMETERS: numeric", "PARAMETER: numeric", "POSTFIX_EXPRESSION: numeric", "PREFIX_EXPRESSION: numeric", "PRIMITIVE_TYPE: numeric", "QUALIFIED_NAME: numeric", "QUALIFIED_TYPE: numeric", "RETURN_STATEMENT: numeric", "ROOT_NODE: numeric", "SIMPLE_NAME: numeric", "SINGLE_TYPE: numeric", "STRING_LITERAL: numeric", "SUPER_INTERFACE_TYPES: numeric", "SWITCH_CASE: numeric", "SWITCH_STATEMENT: numeric", "SYNCHRONIZED_STATEMENT: numeric", "THEN_STATEMENT: numeric", "THROW: numeric", "THROW_STATEMENT: numeric", "TRY_STATEMENT: numeric", "TYPE_PARAMETERS: numeric", "TYPE_DECLARATION: numeric", "TYPE_LITERAL: numeric", "TYPE_PARAMETER: numeric","VARIABLE_DECLARATION_STATEMENT: numeric","WHILE_STATEMENT: numeric","WILDCARD_TYPE: numeric","FOR_INIT: numeric","FOR_INCR: numeric","class: {Accepted, Rejected, Reverted}"]
	return listOfAttributes

def allData(f):
	info = f.read().split("\n")
	print f
	specialized = info[19].split(",")
	listOfData = [info[7][11:],info[15][10:],info[2][13:],info[1][6:],info[16][8:],info[12][8:],info[14][9:],specialized[0],specialized[1],specialized[2],specialized[3],specialized[4],specialized[5],specialized[6],specialized[7],specialized[8],specialized[9],specialized[10],specialized[11],specialized[12],specialized[13],specialized[14],specialized[15],specialized[16],specialized[17],specialized[18],specialized[19],specialized[20],specialized[21],specialized[22],specialized[23],specialized[24],specialized[25],specialized[26],specialized[27],specialized[28],specialized[29],specialized[30],specialized[31],specialized[32],specialized[33],specialized[34],specialized[35],specialized[36],specialized[37],specialized[38],specialized[39],specialized[40],specialized[41],specialized[42],specialized[43],specialized[44],specialized[45],specialized[46],specialized[47],specialized[48],specialized[49],specialized[50],specialized[51],specialized[52],specialized[53],specialized[54],specialized[55],specialized[56],specialized[57],specialized[58],specialized[59],specialized[60],specialized[61],specialized[62],specialized[63],specialized[64],specialized[65],specialized[66],specialized[67],specialized[68],specialized[69],specialized[70],specialized[71],specialized[72],specialized[73],specialized[74],specialized[75],specialized[76],specialized[77],specialized[78],specialized[79],specialized[80],info[13][7:]]
	return listOfData

def getDatafromTxt():
	path = "/Users/ashleychen/Desktop/REUSE/REUSE/Repos"
	repos = [f for f in listdir(path) if isdir(join(path, f))]
	pullsString = []
	for repo in repos:
		pulls = listdir(path+"/"+repo)
		for pull in pulls:
			if pull != ".DS_Store":
				f = open(path+"/"+repo+"/"+pull+"/"+pull+"_INFO.txt",'r')
				listt = allData(f)
				string = ""
				for i in listt:
					string += i + ","
				pullsString.append(string)

				listt = []
				for i in pullsString:
					listt.append(i.split(','))
	return listt

class FormatFile:
	def __init__(self):
		self.attributes = defaultAttributes()
		self.lines = []

	def formatRelations(self):
		self.lines.append("@relation Decision")

	def formatAttributes(self):
		for i in self.attributes:
			self.lines.append("@attribute " + i)

	def formatAttributesShort(self,listt):
		for i in listt:
			self.lines.append("@attribute " + i)

	def formatData(self,data):
		self.lines.append("@data")
		for i in data:
			string = ','.join(i)
			self.lines.append(string)
