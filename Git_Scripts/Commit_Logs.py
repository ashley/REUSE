import os
import sys

def traverseFiles(path):
    listt = []
    if not os.path.lexists(path):
        print "File path does not exist"
        return
    elif os.path.isdir(path):
        for commit in os.listdir(path):
            for ffile in os.listdir(path+"/"+commit+"/"+"b"):
                listt.append(ffile + " " + commit)
    return listt

def writeIn(listt, filePath):
    fi = open(filePath, 'w')
    for line in listt:
        fi.write("%s\n" % line)
    fi.close()

writeIn(traverseFiles("/Users/ashleychen/Desktop/Repos/derby"),"derby_fileNames.txt")
writeIn(traverseFiles("/Users/ashleychen/Desktop/Repos/atmosphere"),"atmosphere_fileNames.txt")
writeIn(traverseFiles("/Users/ashleychen/Desktop/Repos/elasticsearch"),"elasticsearch_fileNames.txt")
writeIn(traverseFiles("/Users/ashleychen/Desktop/Repos/netty"),"netty_fileNames.txt")
writeIn(traverseFiles("/Users/ashleychen/Desktop/Repos/openjpa"),"openjpa_fileNames.txt")

