from GitHub_API_Collect import *
import os

def picklingRepos():
	pas = 0

	if os.path.lexists("/Users/ashleychen/Desktop/REUSE/REUSE/repoID.p") and pas == 1:
		print "already pickled files"
		listofRepoID = openPickledData('repoID.p')
	else:
		listofRepoID = searchRepos("a+language:java",200,500)
		pickledData('repoID.p',listofRepoID)

def firstARFFTest():
	repoCollection = []
	for id in listofRepoID:
		repoCollection.append(createPullClass(id))
	pickledData("repoCollection",repoCollection)


listofRepoID = [] #Repo Example: 19148949 or add multiple repoIDs in this array
userID = int(input("Enter repo ID (example: 19148949): "))
listofRepoID.append(userID)
for repo in listofRepoID:
	storePull(repo)


