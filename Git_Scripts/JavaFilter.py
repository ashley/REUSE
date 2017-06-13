import os
import re

def traverseRepo(repoName): 
    repoPath = os.path.abspath("Repos" + "//"+ repoName)
    if os.path.lexists(repoPath):
        print repoPath
        listt = os.listdir(repoPath)
        listt = [commit for commit in listt if commit != '.DS_Store']
        for commit in listt:
            badCommits = []
            commitPath = repoPath + "//" + commit
            beforePath = commitPath + "//b"
            afterPath = commitPath + "//f"
            fileNames = os.listdir(beforePath)
            regex = re.compile(r'\.java$')
            #Check if there are an equal number of files in both b and f directories
            if os.listdir(beforePath) != os.listdir(afterPath):
                badCommits.append(commit)
            elif len(os.listdir(beforePath)) == 0:
                badCommits.append(commit)
            elif len(filter(regex.search, fileNames)) != len(os.listdir(beforePath)):
                badCommits.append(commit)
        print badCommits

        

traverseRepo("testing")
