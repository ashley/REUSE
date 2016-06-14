from github import Github
import csv
import cPickle
from tqdm import tqdm
from openpyxl import Workbook
import requests
import sys
import os
reload(sys)  
sys.setdefaultencoding('utf8')
g = Github('msashleychen', 'freemason1')

class Pull:
	def __init__(self, pullObj,repoID):
		self.idNum = str(pullObj.id)
		self.number = str(pullObj.number)
		self.state = str(pullObj.state)
		self.merge = str(pullObj.merged)
		self.label = str(pullObj.base.label)
		self.result = ""
		self.title = pullObj.title.encode('utf-8')
		self.repo = repoID


	def classify(self):
		if self.title[:6] == "Revert" and self.state == "Closed" and self.merge == True:
			print pull.title
			self.result = "Revert"
		elif self.state == "Closed" and self.merge == False:
			self.result = "Rejected"
		elif self.state == "Open" and self.merge == False:
			self.result = "Pending"
		else:
			self.result = "Accepted"

def searchRepo(description, language):
	repos = g.search_repositories(description)

	for i in repos:
		print("repo name: " + str(i.name) + 
			"repo owner: " + str(i.owner.name)
			+ "repo id: " + str(i.id))
		print i.html_url

def sumOfPulls(pullObject):
	count = 0
	
	for i in pullObject:
		count += 1
	return count

def pullInfo(repo):
	pulls = repo.get_pulls("closed")
	for i in pulls:
		print "//NEW PULL//"
		print "id: " , i.id
		print "#: ", i.number
		print "state: " , i.title
		print "body: ", i.body
		print "Merged: ", i.merged
		print "label: ", i.base.label
		print ""
		print "/COMMENTS/"
		commentsInfo(i)
		print "/END COMMENTS/"		
		print "//END OF PULL//"
		print 

def issueInfo(repo):
	issues = repo.get_issues(milestone="none",state="closed")
	for i in issues:
		print "Title: " + i.title
		print "LABELS: "
		for a in i.labels:
			print a.name
		print

def commentsInfo(pull):
	pullComments = pull.get_issue_comments()
	for i in pullComments:
		print "id: " + str(i.id)
		print "user: ", i.user.login
		print "body: ", i.body
		print 

def main():
	#searchRepo("katello","")
	#repoID = 60700215
	repoID = 21836148
	repo = g.get_repo(repoID)
	cloneFiles(repo)

	
def cloneFiles(repo):
	os.makedirs(repo.name)
	for pull in repo.get_pulls(state="all"):
	#pull = repo.get_pull(262)
		os.makedirs(repo.name+"//"+str(pull.number))
		ogSHA = pull.head.sha
		pullFiles = pull.get_files()
		for i in tqdm(range(sumOfPulls(pullFiles))):	
			filePathName = pullFiles[i].filename

			switch = 1

			if switch == 1:
				os.makedirs(repo.name+"//"+str(pull.number)+"//"+str(pullFiles[i].sha[:8]))
				r = requests.get(pullFiles[i].raw_url)
				if pullFiles[i].status == "removed":
					suffix = "_BEFORE.txt"
				else:
					suffix = "_AFTER.txt"
				fo = open(repo.name+"//"+str(pull.number)+"//"+str(pullFiles[i].sha[:8])+"//"+str(pullFiles[i].sha[:8])+suffix,"wb")
				fo.write(str(pullFiles[i].sha)+ str(pullFiles[i].filename)+r.text.encode('utf-8').strip())

			if switch == 1 and pullFiles[i].patch != None and sumOfPulls(repo.get_commits(sha=ogSHA,path=filePathName)) > 1:
				beforeSHA = repo.get_commits(sha=ogSHA,path=filePathName)[1].sha
				for file in repo.get_commit(beforeSHA).files:
					if file.filename == filePathName:
						beforeURL = file.raw_url
				rB = requests.get(beforeURL)
				shaID = str(beforeSHA)+"_BEFORE" + "\n" 
				fileName = str(pullFiles[i].filename)+"_BEFORE" + "\n" 
				fo = open(repo.name+"//"+str(pull.number)+"//"+str(pullFiles[i].sha[:8])+"//"+str(pullFiles[i].sha[:8])+"_BEFORE.txt","wb")
				fo.write(beforeSHA + fileName + rB.text.encode('utf-8').strip())
				fo.close()


def storePulls(repo):
	pulls = repo.get_pulls("all")
	listt = []
	for pull in tqdm(range(sumOfPulls(pulls))):
		listt.append(Pull(pulls[pull],repoID))
		listt[pull].classify()
	f = open('data.p', 'wb')
	cPickle.dump(listt,f)      
	f.close() 	
	print "code pickled"

def end():
	f = open('data.p', 'rb')
	myData = cPickle.load(f)
	f.close()
	return myData

def colnum_string(n):
    div=n
    string=""
    temp=0
    while div>0:
        module=(div-1)%26
        string=chr(65+module)+string
        div=int((div-module)/26)
    return string

def submitToExcel(cells):
	wb = Workbook()
	ws = wb.active
	attributes = ["idNum","number","state","merge","label", "result", "title","repo"]
	for r in range(1,len(cells)):
		for c in range(1,8):
			ws[colnum_string(c)+str(r)] = getattr(cells[r],attributes[c])
	wb.save("sample.xlsx")
	print "Submited to Excel"

main()
#submitToExcel(end())


	

