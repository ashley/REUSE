from Github_API_Setup import *
from Pull import *
import pickle

def main():
	g = GithubOAuth(token)
	print "RATE LIMIT: ", g.rate_limiting
	repo = g.get_repo(repoID)
	print repo.html_url
	apiPulls = repo.get_pulls(state="closed")
	output = open('pulls.pkl','rb')
	pulls = pickle.load(output)
	print apiPulls[1000]
	#pulls = {}

	i = len(pulls)
	while apiPulls[i] != apiPulls[-1] and g.rate_limiting[0] > 20:
		pullNumber = apiPulls[i].number
		if pullNumber in pulls.keys():
			pass
		else:
			pulls[pullNumber] = Pull(repo,repo.get_pull(pullNumber))
			print "Obtain Pull " + str(i)			
		i+=1
	if g.rate_limiting < 20:
		print "Rate Limit Stop. Pickling " + str(i) + " pulls"
	output = open('pulls.pkl','wb')
	pickle.dump(pulls,output)
	output.close()
	

token = '93e8006bc59db0802284441fb9cfe4b278003005'
#pullNumber = 4525
repoID = 7508411
main()
