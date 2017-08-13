# REUSE
Summer REU Project for predicting the likelihood of pull requests getting accepted, rejected, and reverted.

## Mining Fixed and Buggy Changes
- Check http://odd-code.github.io/Data.html for SHAs
- Run `Git_Bug_Collect.py` with arguments of the project name, project path, and number of changes to mine
- Execute the .jar file with the corresponding K-fold iteration. The file should be labeled like `TrainBuggyCode_atmosphere_234.jar` where `atmosphere` is the project name and `234` are the iterations
- Once a model is made, execute `TestEntropy.jar` with arguments stating the range of changes to generate entropy. This executable file should generate a text file relative to the location of the executable file
- Import the text file to R, and run `Entropy_Aggregation.R` on the file
