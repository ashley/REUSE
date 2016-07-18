# REUSE
Summer REU Project for predicting the likelihood of pull requests getting accepted, rejected, and reverted.

## Collect Github Pull Requests
1. Make sure there is a "Repos" folder in this repo.
2. run file_analysis.py to start cloning pull requests to the "Repos" directory. It will prompt you to enter the Repo ID which you can find by going to https://api.github.com/repos/USERNAME/REPONAME. To mine through multiple repos, you can modify file_analysis's listofRepoID array with as many RepoIDs as you want.
3. Once file_analysis.py is running, there should be a loading bar that shows the script's progress. 