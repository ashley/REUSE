import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import codemining.lm.tsg.TSGNode;
import codemining.lm.tsg.TSGrammar;
import codemining.lm.tsg.tui.TsgEntropy;
import codemining.util.serialization.ISerializationStrategy.SerializationException;

public class AnalyzeWork {
	private static Map <String, Map> pullStat = new HashMap<>();
	public static void main(String [] args) throws IOException, SerializationException{
		if (args.length != 4) {
			System.err.println("arguments: Repo's path, storeChanges(true/false)");
			return;
		}
		String reusePath = args[3];
		String reposPath = args[2];
		String repoName = args[0].split("/")[args[0].split("/").length-1];
		String repoPath = reposPath + "/" + repoName;
		TsgEntropy te = new TsgEntropy(repoName,reusePath);
		boolean storeChanges = Boolean.parseBoolean(args[1]);
		File [] pulls = new File(repoPath).listFiles();
		for (File pull: pulls){
			if(!pull.toString().equals(repoPath + "/.DS_Store") && pull.isDirectory()){
				new Pull(pull.toString());
				Pull.getChanges(storeChanges,repoName, te);
			System.out.println();
			
			}
		}
	}//main
}//class
