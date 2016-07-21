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
import java.util.TreeMap;

import com.google.common.base.Joiner;

import codemining.lm.tsg.tui.TsgEntropy;
import codemining.util.serialization.ISerializationStrategy.SerializationException;

public class Pull {
	private static File pull;
	private static File[] files;
	private static String before = "_BEFORE.txt";
	private static String after = "_AFTER.txt";
	private static boolean worked;
	private static Map <String,Double> compatible = new HashMap<>();
	private static Map <String,ArrayList<Integer>> pullSign = new HashMap<>();
	private static String pullPath;
	
	Pull(String pullPathh){
		pullPath = pullPathh;
		pull = new File(pullPath);
		files = pull.listFiles();
		pull.toString();
	}
	
	public static File [] getFiles(){
		return files;
	}
	
	public static File getFileVersion(int num, String version){
		if (version.equals("before")) {
			File fi = new File(files[num].toString() + "/" + getSha(num) + before);
			return (fi);
		} else {
			return (new File(files[num].toString() + "/" + getSha(num) + after));
		}

	}
	
	public static String getSha(int num){
		return (files[num].toString().split("/")[files[num].toString().split("/").length-1]);
	}
	public static int getFilesCount(){
		return (files.length);
	}
	
	public static void getChanges(boolean storeChanges,String repoName, TsgEntropy te) throws IOException, SerializationException{
		//compatible.clear();
		pullSign.clear();
		Distiller aDistiller = new Distiller();
		Map<String,Double> defaultMap = aDistiller.createChangeMap();
		Map<String,Double> defaultEntropyMap = aDistiller.createEntropyMap();

		double [] entropy = new double [2];
		//Map<String,Integer> changeMap;
		for (int i = 0; i<getFilesCount();i++){
			boolean beforeFile = false;
			boolean afterFile = false;
			if(!files[i].isFile()){
					System.out.println(files[i]);
					worked = false;
					if(files[i].listFiles().length >= 2){
						aDistiller.executeDistiller(getFileVersion(i,"before"),getFileVersion(i,"after"));
						beforeFile = true;
						afterFile = true;
					}
					else if((getFileVersion(i,"after")).exists()){
						afterFile = true;
						aDistiller.setFileVersions(getFileVersion(i,"after"), "after");
					}
					else if((getFileVersion(i,"before")).exists()){
						beforeFile = true;
						aDistiller.setFileVersions(getFileVersion(i,"before"), "before");
					}
					aDistiller.clearArrayList();
					aDistiller.getChanges(beforeFile, afterFile, repoName, te);
					Map<String,Double> changeMap = aDistiller.getChangeMap();
					Map<String,Double> entropyMap = aDistiller.getEntropyMap();
					changeMap.forEach((k, v) -> defaultMap.merge(k, v, Double::sum));
					entropyMap.forEach((k, v) -> defaultEntropyMap.merge(k, v, Double::sum));
					pullSign.put(Integer.toString(i),aDistiller.getSigList());
					entropy[0] = 1.1;//StoreEntropy.entropyLevel(pull.toString());
					aDistiller.getArrayList().add("Entropy: " + entropy[0]);
					aDistiller.getArrayList().add("Cross-Entropy: " + entropy[1]);
					System.err.println(aDistiller.getArrayList());
			}
		}//for
		System.out.println(defaultEntropyMap);
		if (storeChanges){
			storeInfo(convertEntitiesToString(defaultEntropyMap)+",");
			storeInfo(sumSig()+",");
			storeInfo(convertEntitiesToString(defaultMap));
		}
	}//getChanges
	
	public static Map<String, Double> checkChanges(){
		return compatible;
	}
	
	public static String convertEntitiesToString(Map<String, Double> map){
		String text;
		List<Object> listt = new ArrayList<Object>(map.values());
		text = Joiner.on(",").join(listt);
		return text;
	}
	
	public static void storeChanges(ArrayList<String> changesInString) throws IOException{
		List<String> lines = changesInString;
		Path infoFile = Paths.get(pullPath+"/"+pullPath.split("/")[pullPath.split("/").length-1]+"_INFO.txt");
	    Files.write(infoFile, lines, Charset.forName("UTF-8"),StandardOpenOption.APPEND);
	}
	
	public static String sumSig() throws IOException{
		int low=0;
		int med=0;
		int high=0;
		for(Map.Entry<String, ArrayList<Integer>> mp: pullSign.entrySet()){
			low = low + mp.getValue().get(0);
			med = med + mp.getValue().get(1);
			high = high + mp.getValue().get(2);
		}
		String text = Integer.toString(low) +","+ Integer.toString(med)+","+Integer.toString(high);
		return text;
	}
	public static void storeInfo(String text) throws IOException{
		System.out.println("STORING INFO: " + text);
		Path infoFile = Paths.get(pullPath+"/"+pullPath.split("/")[pullPath.split("/").length-1]+"_INFO.txt");
		Files.write(infoFile, text.getBytes(), StandardOpenOption.APPEND);
		System.err.println("Stored");
	}
	
	public static void deleteLastLine() throws IOException{
		RandomAccessFile f = new RandomAccessFile(pullPath+"/"+pullPath.split("/")[pullPath.split("/").length-1]+"_INFO.txt", "rw");
		long length = f.length() - 1;
		do {                     
		  length -= 1;
		  f.seek(length);
		} while(f.readByte() != 10 && length>0);
			f.setLength(length+1);
			f.close();
	}
	
}
