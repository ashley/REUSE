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


listofRepoID = [19148949]
for repo in listofRepoID:
	storePull(repo)


