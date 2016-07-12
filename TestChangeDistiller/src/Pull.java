import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import codemining.util.serialization.ISerializationStrategy.SerializationException;

public class Pull {
	private static File pull;
	private static File[] files;
	private static String before = "_BEFORE.txt";
	private static String after = "_AFTER.txt";
	
	
	Pull(String repoPath){
		pull = new File(repoPath);
		files = pull.listFiles();
		pull.toString();
	}
	
	public static File [] getFiles(){
		return files;
	}
	
	public static File getFileVersion(int num, String version){
		if (version.equals("before")) {
			return (new File(files[num].toString() + "/" + getSha(num) + before));
		} else {
			return (new File(files[num].toString() + "/" + getSha(num) + after));
		}

	}
	
	public static String getSha(int num){
		return (files[num].toString().split("/")[9]);
	}
	public static int getFilesCount(){
		return (files.length);
	}
	
	public static void getChanges() throws IOException, SerializationException{
		for (int i = 0; i<getFilesCount();i++){
			if(!files[i].isFile()){
				if(files[i].listFiles().length >= 2){
					System.out.println(files[i]);
					Distiller aDistiller = new Distiller(getFileVersion(i,"before"),getFileVersion(i,"after"));
					aDistiller.clearArrayList();
					aDistiller.getChanges();
					//String [] entropy = StoreEntropy.entropyLevel(pull.toString());
					//aDistiller.getArrayList().add("Entropy: " + entropy[0]);
					//aDistiller.getArrayList().add("Cross-Entropy: " + entropy[1]);
					if (!aDistiller.getArrayList().isEmpty()){
						//storeChanges(aDistiller.getArrayList(),i);
					}
				}
			}
		}//for 
	}//getChanges
	
	public static void storeChanges(ArrayList<String> changesInString, int fileNum) throws IOException{
		List<String> lines = changesInString;
	    Path textFile = Paths.get(files[fileNum].toString() + "/" + getSha(fileNum)+"CHANGES.txt");
	    Files.write(textFile, lines, Charset.forName("UTF-8"));
	}
	
}
