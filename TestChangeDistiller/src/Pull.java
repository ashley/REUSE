import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import codemining.util.serialization.ISerializationStrategy.SerializationException;

public class Pull {
	private static File pull;
	private static File[] files;
	private static String before = "_BEFORE.txt";
	private static String after = "_AFTER.txt";
	private static boolean worked;
	private static Map <String,Integer> compatible = new HashMap<>();
	private static Map <String,ArrayList<Integer>> pullSign = new HashMap<>();
	
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
		compatible.clear();
		pullSign.clear();
		int work = 0;
		int doesntWork = 0;
		Distiller aDistiller = new Distiller();
		for (int i = 0; i<getFilesCount();i++){
			if(!files[i].isFile()){
				if(files[i].listFiles().length >= 2){
					System.out.println(files[i]);
					worked = false;
					aDistiller.executeDistiller(getFileVersion(i,"before"),getFileVersion(i,"after"));
					aDistiller.clearArrayList();
					aDistiller.getChanges();
					pullSign.put(Integer.toString(i),aDistiller.getSigList());
					//String [] entropy = StoreEntropy.entropyLevel(pull.toString());
					//aDistiller.getArrayList().add("Entropy: " + entropy[0]);
					//aDistiller.getArrayList().add("Cross-Entropy: " + entropy[1]);
					if (!aDistiller.getArrayList().isEmpty()){
						work++;
						System.out.println("ARRAYLIST:" + aDistiller.getArrayList());
						//storeChanges(aDistiller.getArrayList(),i);
					}
					else{
						doesntWork++;
					}
				}
			}
		}//for
		compatible.put("Works", work);
		compatible.put("No", doesntWork);
		//System.out.println(pullSign);
		sumSig();
	}//getChanges
	
	public static Map checkChanges(){
		return compatible;
	}
	
	public static void storeChanges(ArrayList<String> changesInString, int fileNum) throws IOException{
		List<String> lines = changesInString;
	    Path textFile = Paths.get(files[fileNum].toString() + "/" + getSha(fileNum)+"CHANGES.txt");
	    Files.write(textFile, lines, Charset.forName("UTF-8"));
	}
	
	public static void sumSig() throws IOException{
		int low=0;
		int med=0;
		int high=0;
		//System.out.println(pullSign);
		for(Map.Entry<String, ArrayList<Integer>> mp: pullSign.entrySet()){
			low = low + mp.getValue().get(0);
			med = med + mp.getValue().get(1);
			high = high + mp.getValue().get(2);
		}
		String text = Integer.toString(low) +","+ Integer.toString(med)+","+Integer.toString(high);
		//System.out.println(text);

		deleteLastLine();
		Path infoFile = Paths.get(pull.toString()+"/INFO.txt");
		Files.write(infoFile, text.getBytes(), StandardOpenOption.APPEND);
	}
	
	public static void deleteLastLine() throws IOException{
		RandomAccessFile f = new RandomAccessFile(pull.toString()+"/INFO.txt", "rw");
		long length = f.length() - 1;
		do {                     
		  length -= 1;
		  f.seek(length);
		} while(f.readByte() != 10 && length>0);
			f.setLength(length+1);
			f.close();
	}
	
}
