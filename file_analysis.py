from GitHub_API_Collect import *
import os, os.path
import datetime

def picklingRepos():
	pas = 0

	if os.path.lexists("/Users/ashleychen/Desktop/REUSE/REUSE/repoID.p") and pas == 1: #safety pin
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

g = GithubOAuth('93e8006bc59db0802284441fb9cfe4b278003005')

unformatTime = int(g.rate_limiting_resettime)#.strftime('%H:%M:%S')
currentTime = int(datetime.datetime.now().strftime("%s")) #datetime.datetime.fromtimestamp
userInputPath = input("Paste the path here: ") #/Users/ashleychen/Desktop/REUSE/REUSE/Repos/elasticsearch/
path, dirs, files = os.walk(str(userInputPath)).next()
file_count = len(dirs) - 1
#print file_count

tokens = ['93e8006bc59db0802284441fb9cfe4b278003005']
listofRepoID = [] #Repo Example: 19148949 or add multiple repoIDs in this array
userID = input("Enter repo ID (example: '19148949' or 'd' for default): ")
start = file_count
stop = int(input("Enter stop: "))
if userID == 'd':
	listofRepoID.append(19148949)
elif userID=='token':
	token = input("Enter your token: ")
	tokens = [token]
	listofRepoID.append(userID)
else:
	listofRepoID.append(int(userID))
for repo in range(len(listofRepoID)):
	g = GithubOAuth(tokens[repo])
	print g.rate_limiting
	storePull(listofRepoID[repo],g,start,stop)

	repoCollection = []

	#'e830cb5c349fcd370134161668a3ba74144ff7cb','e5813425ce00f24ff96ef1c8b6c874fe94faaee9','ed0e0c8f5afb170c475c8fb8703e0972b10c3a21',
	#'2378badc29e41939863978ef886fd9cf01c91bc0','bdbe6402f605805b304bc03324f2e0423bbe4efd','070d7c44cb97af94fbf872b5419bebe6726d1b37','8414b778c60aca40d83aeee9c6bcd6e4d91cf284',
	#'d49141037bacd411e1f99b8949b558509e0e4572'