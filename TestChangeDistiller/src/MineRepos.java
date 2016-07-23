import java.io.File;
import java.io.IOException;

import codemining.util.serialization.ISerializationStrategy.SerializationException;

public class MineRepos {
	public static void main(String[] args) throws IOException, SerializationException{
		String reusePath = "/Users/ashleychen/Desktop/REUSE/REUSE";
		String repoPath = reusePath + "/Repos";
		String iterations = "400";
		File [] repos = new File(repoPath).listFiles();
		for (File repo: repos){
			if(repo.isDirectory()){
				String [] arguments = {repo.toString(),"true",repoPath,reusePath,iterations};
				AnalyzeWork.main(arguments);
			}
		}
		System.err.println("Done");
	}
		
}
