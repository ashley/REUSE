from GitHub_API_Collect import *

g = Rate(0)

print "initial: " + str(g.rate)
g.git.get_repo(19148949).id
print "attribute: ", g.rate
print "git: ", g.git.rate_limiting
print "ID: ", g.git
print id(g)

a = Rate(1)
#a = swtichRate(g)
print "attribute: ", a.rate
print "git: ", a.git.rate_limiting
print "ID: ", a.git
print id(a)


