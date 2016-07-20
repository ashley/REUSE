# REUSE
Summer REU Project for predicting the likelihood of pull requests getting accepted, rejected, and reverted.

## Collect Github Pull Requests
1. Make sure there is a "Repos" folder in this repo.
2. run file_analysis.py to start cloning pull requests to the "Repos" directory. It will prompt you to enter the Repo ID which you can find by going to https://api.github.com/repos/USERNAME/REPONAME. To mine through multiple repos, you can modify file_analysis's listofRepoID array with as many RepoIDs as you want:
```python
listofRepoID = [] #Repo Example: 19148949
```
3. Once file_analysis.py is running, there should be a loading bar that shows the script's progress. 
4. Once the script is finished, each pull should have either before, after, or both file verisons. There should also be a pull info.txt file for each pull.


## Process ChangeDistiller and Entropy to Repos
1. Be sure that TestChangeDistiller is has [codemining-treelm](msashleychen/codemining-treelm) and [ChangeDistiller](https://bitbucket.org/sealuzh/tools-changedistiller/wiki/Home) as maven dependencies. Also make sure there is a .ser file in [Entropy-model folder] (msashleychen/REUSE/tree/master/Entropy-model) named after the repo.
2. Run AnalyzeWork's main from TestChangeDistiller. It will ask for two arguments (the repo path and whether you want to store changes). The repo path should be a directory of the repo. To store changes, enter true to add ChangeDistiller's output and entropy to each pull's info file.
3. While the program runs, it should print change distiller information.
4. To check if the program succeed, check any pull info.txt file for additional info.

## Generate ARFF File
1. Make sure all repos in the Repos folder have been processed with AnalyzeWork. 
2. Run Format-ARFF-Files.py. This script will go into all info.txt files and format the info into a arff file.
3. When the script it done, a file called Data-Complete.txt should appear in the Weka folder.
4. Run Driver main from Weka-SimpleModel project to convert the txt file into a an ARFF file. If a problem occurs, it will print: "Problem found when reading.."
