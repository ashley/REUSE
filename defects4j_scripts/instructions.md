### How to Obtain buggy and fix Java files from Defects4j

1. Install defects4j and make sure it is on your environment path.
2. Edit ```checkout.sh``` and change the project name and version range to your likings. After ```checkout.sh``` has been modified, run ```bash checkout.sh```
3. Edit ```diff.sh``` to the same version range and run it in a clear command. Copy and paste the output into a text editor. Filter out all the java files and list each file on its own line.Save the result in a file called ```files.txt``` and save it to the save directory as everything else.
4. Create two folders; one called b and one called f. Run ```move.sh```.
