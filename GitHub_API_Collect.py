from github import Github #Github API
import csv #Convert to csv is needed
import cPickle #Module to store data anytime
from tqdm import tqdm #Loading bar API
from openpyxl import Workbook #Excel API
import requests #To request things off the web
import sys #For encoding/decoding special keys
import os #For encoding/decoding special keys
reload(sys) #Fixes bug for encoding special keys
sys.setdefaultencoding('utf8') #Fixes bug for encoding special keys
g = Github('yuannc', 'notmypassword1') #Access to Github API

class Pull: #Classifies pull reuqests as "Accepted", "Rejected", "Open", and "Reverted"
	def __init__(self, pullObj,repoID): #Inolves information from the pull
		self.idNum = str(pullObj.id)
		self.number = str(pullObj.number)
		self.state = str(pullObj.state).lower()
		self.merge = pullObj.merged
		self.label = str(pullObj.base.label)
		self.result = ""
		self.title = pullObj.title.encode('utf-8')
		self.repo = repoID


	def classify(self): #If else statement to classify pull
		if self.title[:6] == "revert" and self.state == "closed" and self.merge == True:
			print pull.title
			self.result = "Revert"
		elif self.state == "closed" and self.merge == False:
			self.result = "Rejected"
		elif self.state == "open" and self.merge == False:
			self.result = "Pending"
		else:
			self.result = "Accepted"

def searchRepos(description, numOfPulls): #Searches for repos under description. May be overwhelming data (depending on the description)
	repos = g.search_repositories(description)

	for i in repos:
		if sumOfPulls(i.get_pulls(state="closed")) <= numOfPulls:
			print("repo name: " + str(i.name) + 
				"repo owner: " + str(i.owner.name)
				+ "repo id: " + str(i.id))
			print i.html_url

def sumOfPulls(pullObject): #Used to find the sum of any Paginated List, not just pulls
	count = 0
	
	for i in pullObject:
		count += 1
	return count

def commentsInfo(pull): #Use attributes to get comments of a pull
	pullComments = pull.get_issue_comments()
	for i in pullComments:
		print "id: " + str(i.id)
		print "user: ", i.user.login
		print "body: ", i.body
		print 

def hardwireClones():
	repoIDs = [7266492,2287594, 15694901, 13131265, 14568504, 3739369, 1840419] #IDs of repos to clone

	for repoID in repoIDs:
		repo = g.get_repo(repoID)
		cloneFiles(repo)

def storePull(repoID):
	repo = g.get_repo(repoID)
	cloneFiles(repo)
	
	
def cloneFiles(repo):
	if not os.path.lexists("Repos"+"//"+repo.name): #Checks if folder exist. Also makes sure not to duplicate files
		os.makedirs("Repos"+"//"+repo.name) #Makes folder
	for pull in repo.get_pulls(state="closed"): #Default is all pulls
		pullInfo = Pull(pull, repo)
		pullInfo.classify() #accepted, rejected, reverted
		ogSHA = pull.head.sha
		pullFiles = pull.get_files()
		for i in tqdm(range(sumOfPulls(pullFiles))):	#tqdm is the loading bar
			filePathName = pullFiles[i].filename

			switch = 1 #safety pin

			if switch == 1: 
				if not os.path.lexists("Repos"+"//"+repo.name+"//"+str(pull.number)+"_"+pullInfo.result+"//"+str(pullFiles[i].sha[:8])):
					os.makedirs("Repos"+"//"+repo.name+"//"+str(pull.number)+"_"+pullInfo.result+"//"+str(pullFiles[i].sha[:8]))
				if pullFiles[i].status == "removed":
					#Checks whether or not the file was added, removed, or modified
					suffix = "_BEFORE.txt"
				else:
					suffix = "_AFTER.txt"
				if not os.path.lexists("Repos"+"//"+repo.name+"//"+str(pull.number)+"_"+pullInfo.result+"//"+str(pullFiles[i].sha[:8])+"//"+str(pullFiles[i].sha[:8])+suffix):
					#Starts to create the file here
					fo = open("Repos"+"//"+repo.name+"//"+str(pull.number)+"_"+pullInfo.result+"//"+str(pullFiles[i].sha[:8])+"//"+str(pullFiles[i].sha[:8])+suffix,"wb")
					r = requests.get(pullFiles[i].raw_url)
					#Only using request raw text for now. Figure our how to download img later.
					fo.write("//SHA: " + str(pullFiles[i].sha)+ "\n" + "//Path: " + str(pullFiles[i].filename)+"\n//Version: " + suffix+"\n"+r.text.encode('utf-8').strip())
					fo.close()

			#Statement checks for whether or not the file was added or removed
			if switch == 1 and pullFiles[i].patch != None and sumOfPulls(repo.get_commits(sha=ogSHA,path=filePathName)) > 1:
				beforeSHA = repo.get_commits(sha=ogSHA,path=filePathName)[1].sha #Gets the SHA that's before the current commit in its history
				for file in repo.get_commit(beforeSHA).files: #Checks that commit for the file. 
					if file.filename == filePathName: 
						beforeURL = file.raw_url #BEFORE version of the file URL
				shaID = str(beforeSHA)
				fileName = str(pullFiles[i].filename)
				if not os.path.lexists("Repos"+"//"+repo.name+"//"+str(pull.number)+"_"+pullInfo.result+"//"+str(pullFiles[i].sha[:8])+"//"+str(pullFiles[i].sha[:8])+"_BEFORE.txt"):
					#Same for the AFTER file
					fo = open("Repos"+"//"+repo.name+"//"+str(pull.number)+"_"+pullInfo.result+"//"+str(pullFiles[i].sha[:8])+"//"+str(pullFiles[i].sha[:8])+"_BEFORE.txt","wb")
					rB = requests.get(beforeURL)
					fo.write("//SHA: " + beforeSHA + "\n//Path: " + fileName + "\n//Version: _BEFORE.txt \n" + rB.text.encode('utf-8').strip())
					fo.close()


def storePulls(repo): #Pickles pull objects, use for rough coding
	pulls = repo.get_pulls("all")
	listt = []
	for pull in tqdm(range(sumOfPulls(pulls))):
		listt.append(Pull(pulls[pull],repoID))
		listt[pull].classify()
	f = open('data.p', 'wb')
	cPickle.dump(listt,f)      
	f.close() 	
	print "code pickled"

def openPickledData():
	f = open('data.p', 'rb')
	myData = cPickle.load(f)
	f.close()
	return myData

def colnum_string(n): #Used to number infinite columns in Excel
    div=n
    string=""
    temp=0
    while div>0:
        module=(div-1)%26
        string=chr(65+module)+string
        div=int((div-module)/26)
    return string

def submitToExcel(cells): #Excel test
	wb = Workbook()
	ws = wb.active
	attributes = ["idNum","number","state","merge","label", "result", "title","repo"]
	for r in range(1,len(cells)):
		for c in range(1,8):
			ws[colnum_string(c)+str(r)] = getattr(cells[r],attributes[c])
	wb.save("sample.xlsx")
	print "Submited to Excel"



	

