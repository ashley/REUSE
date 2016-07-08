from GitHub_API_Collect import *
import os

pas = 0

if os.path.lexists("/Users/ashleychen/Desktop/REUSE/REUSE/repoID.p") and pas == 1:
	print "already pickled files"
	listofRepoID = openPickledData('repoID.p')
else:
	listofRepoID = searchRepos("game+language:java",200)
	pickledData('repoID.p',listofRepoID)

repoCollection = []
for id in listofRepoID:
	repoCollection.append(createPullClass(id))
pickledData("repoCollection",repoCollection)



