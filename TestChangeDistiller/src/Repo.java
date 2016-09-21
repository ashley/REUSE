import java.io.File;
import java.io.IOException;

import codemining.util.serialization.ISerializationStrategy.SerializationException;

public class Repo {
	Repo(File repoFile) throws IOException, SerializationException{
		String repoPath = repoFile.toString();
		
		File [] pulls = new File(repoPath).listFiles();
		for (File i: pulls){
			System.out.println(repoPath);
			new Pull(i.toString());
			Pull.getChanges(false, "weex", null);
			System.out.println();
		}
	}
}
