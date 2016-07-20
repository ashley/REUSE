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

tokens = ['e830cb5c349fcd370134161668a3ba74144ff7cb','e5813425ce00f24ff96ef1c8b6c874fe94faaee9','ed0e0c8f5afb170c475c8fb8703e0972b10c3a21','2378badc29e41939863978ef886fd9cf01c91bc0','bdbe6402f605805b304bc03324f2e0423bbe4efd','070d7c44cb97af94fbf872b5419bebe6726d1b37','8414b778c60aca40d83aeee9c6bcd6e4d91cf284','f908391378fbc047bcf432d1d16e8c9f8681cd59','600f1fa50aae4ea65dade960a823e3a642da89ce','f1765571eb36aaa7ec49fb964285a6f5918478fe','edd4096cb3b13a40c8d1414cf809cd26f9d2305f','d49141037bacd411e1f99b8949b558509e0e4572','b8a408437b156f93d9db4f6b2044d618b5daefaf','efcfdaa8c361b54f04e3bd060bcbfa7bda89f721','8755d755a4a6f710a0dbb9839106144d35d00f6c']
listofRepoID = [] #Repo Example: 19148949 or add multiple repoIDs in this array
userID = input("Enter repo ID (example: '19148949' or 'd' for default): ")
if userID == 'd':
	listofRepoID.append(19148949)
else:
	listofRepoID.append(int(userID))
for repo in range(len(listofRepoID)):
	g = GithubOAuth(tokens[repo])
	print g.rate_limiting
	#g = useGitAuth(None)
	storePull(listofRepoID[repo],g)

	repoCollection = []
	for id in listofRepoID:
		repoCollection.append(createPullClass(id))
	pickledData("repoCollection",repoCollection)


