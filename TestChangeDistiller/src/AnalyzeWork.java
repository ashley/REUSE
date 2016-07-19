import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import codemining.util.serialization.ISerializationStrategy.SerializationException;

public class AnalyzeWork {
	private static Map <String, Map> pullStat = new HashMap<>();
	
	public static void main(String [] args) throws IOException, SerializationException{
		if (args.length != 2) {
			System.err.println("arguments: Repo's path, storeChanges(true/false)");
			return;
		}
		String repoPath = args[0]; // Hardwire: "/Users/ashleychen/Desktop/REUSE/REUSE/Repos/weex";
		boolean storeChanges = Boolean.parseBoolean(args[1]);
		File [] pulls = new File(repoPath).listFiles();
		for (File pull: pulls){
			if(!pull.toString().equals(repoPath + "/.DS_Store")){
				new Pull(pull.toString());
				Pull.getChanges(storeChanges);
			
			//pullStat.put(pull.toString().split("/")[8], Pull.checkChanges());
			//System.out.println(pullStat);
			//System.out.println("Hashmap: "+ Pull.checkChanges());
			System.out.println();
			
			}
		}
		
		int working = 0;
		int notWorking = 0;
		for(Map.Entry<String, Map> pullMap: pullStat.entrySet()){
			working = working + (int) pullMap.getValue().get("Works");
			notWorking = notWorking + (int) pullMap.getValue().get("No");
		}
		System.out.println("Working Files: " + working + " Failed Files: " + notWorking);
		
	}//main
}//class
