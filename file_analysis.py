from GitHub_API_Collect import searchRepos, storePull, openPickledData, pickledData
import os

if os.path.lexists("/Users/ashleychen/Desktop/REUSE/REUSE/repoID.p"):
	print "great"
else:
	listofRepoID = searchRepos("game+language:java",20)
	pickedData('repoID.p',listofRepoID)

for id in listofRepoID:
	storePull(id)
