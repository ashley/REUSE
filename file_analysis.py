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

listOfClients = ['801bea680af08669472f','eafdebbd5742fba5871e','d59f9312dfce5a0799fe']
listOfSecrets = ['97fc00b28ce6962619732c9bc4d36165c25a6f96','29b21bd5de57dc2102ee4359157f60c87b69a7a2','3229cd46525904c3fd6842e117adfaa267c43bd2']
listofRepoID = [] #Repo Example: 19148949 or add multiple repoIDs in this array
userID = input("Enter repo ID (example: '19148949' or 'd' for default): ")
if userID == 'd':
	listofRepoID.append(19148949)
else:
	listofRepoID.append(int(userID))
for repo in range(len(listofRepoID)):
	g = GithubOAuth(listOfClients[repo],listOfSecrets[repo])
	#g = useGitAuth(None)
	storePull(listofRepoID[repo],g)


