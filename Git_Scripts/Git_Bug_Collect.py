from github import Github
import requests
import sys #For encoding/decoding special keys
import os #For encoding/decoding special keys
import re
from tqdm import tqdm #Loading bar API
reload(sys) #Fixes bug for encoding special keys
sys.setdefaultencoding('utf8') #Fixes bug for encoding special keys

"""
Create an instance of git to authorize the API
@return {Github} g
"""
def gitLogin():
    g = Github(client_id='801bea680af08669472f',client_secret='97fc00b28ce6962619732c9bc4d36165c25a6f96')
    return g

"""
Prints out API rate limit info
@param {Github} g
"""
def getRateInfo(g):
    print "RATE INFO: " + str(g.rate_limiting[0])
    print "Unix timestamp indicating time till reset: " + str(g.rate_limiting_resettime)
    print "---------------------------------------------------------------"

"""
Takes two commit SHAs and returns the union filenames
@param {Repository} repo
@param {String} sha_bi : SHA of when bug is introduced
@param {String} sha_bf: SHA wof when bug is fixed
@return {List} returned_files : dictionary of files of bi and bf from common_files
"""
def compareFiles(repo, sha_bi, sha_bf):
    bi_files = repo.get_commit(sha_bi).files
    bf_files = repo.get_commit(sha_bf).files
    
    bf_files_fileNames = [fileName.filename for fileName in bf_files]
    bi_files_fileNames = [fileName.filename for fileName in bi_files]    
    common_files = list(set(bf_files_fileNames) & set(bi_files_fileNames))
    
    returned_files = {}

    returned_files["bf"] = [fileName for fileName in bf_files if fileName.filename in common_files]
    returned_files["bi"] = [fileName for fileName in bi_files if fileName.filename in common_files]

    return returned_files

"""
Helper funtion to print a elements in a list on its own line
@param {List} listt
"""
def pprint(listt):
    for element in listt:
        print element

"""
Makes a request and writes the utf-8 encoding on the filePath
@param {File} fileObj
@param {String} filePath : where the file should be placed on the computer
"""
def downloadFile(fileObj,filePath):
    fo = open(filePath,"wb")
    rB = requests.get(fileObj.raw_url)
    fo.write(rB.text.encode('utf-8').strip())

"""
Creates a directory of buggy and fixed files
@param {Github} g
@param {String} repo_name
@param {String} repoID : owner/repo_name
"""
def aggregateFiles(g, repo_name, repoID):

    SHA_pairs = importSHAs(os.getcwd()+"//"+repo_name+"_pairs.txt")
    repo = g.get_repo(repoID) 
    getRateInfo(g)

    if not os.path.lexists("Repos"+"//"+repo_name):
        os.makedirs("Repos"+"//"+repo_name)  
    
    for i in tqdm(range(1,len(SHA_pairs))):
        changed_files = compareFiles(repo,SHA_pairs[i][0],SHA_pairs[i][1])
    
        buggyFolders = i
    
        buggyFolder_filePath = "Repos"+"//"+repo_name+"//"+str(buggyFolders)
        if not os.path.lexists(buggyFolder_filePath):
            os.makedirs(buggyFolder_filePath)  
        if not os.path.lexists(buggyFolder_filePath+"//"+"b"):
            os.makedirs(buggyFolder_filePath+"//"+"b")   
        if not os.path.lexists(buggyFolder_filePath+"//"+"f"):
            os.makedirs(buggyFolder_filePath+"//"+"f")  
    
        for files in changed_files["bf"]:
            #print(files.raw_url)
            fileName = re.sub('.*\/', '',files.filename)
            downloadFile(files,buggyFolder_filePath+"//"+"b"+"//"+fileName)

        for files in changed_files["bi"]:
            #print(files.raw_url)
            fileName = re.sub('.*\/', '',files.filename)
            downloadFile(files,buggyFolder_filePath+"//"+"f"+"//"+fileName)

        getRateInfo(g)

"""
Parses a text file of SHA pairs
@param {String} filePath
@return {List} list of list of SHA pairs
"""
def importSHAs(filePath):
    fi = open(filePath, "rb")
    content = [x.strip() for x in fi.readlines()]
    for line in range(len(content)):    
        content[line] = content[line].split(',')
    return content   


def main():
    g = gitLogin()
    aggregateFiles(g, "atmosphere", "atmosphere/atmosphere")

main()
