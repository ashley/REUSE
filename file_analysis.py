from GitHub_API_Collect import *
import os

if os.path.lexists("/Users/ashleychen/Desktop/REUSE/REUSE/repoID.p"):
	print "already pickled files"
	listofRepoID = openPickledData('repoID.p')
else:
	listofRepoID = searchRepos("game+language:java",20)
	pickledData('repoID.p',listofRepoID)

for id in listofRepoID:
	storePull(id)



