import java.io.File;
import java.io.IOException;

public class Repo {
	Repo(File repoFile) throws IOException{
		String repoPath = repoFile.toString();
		
		File [] pulls = new File(repoPath).listFiles();
		for (File i: pulls){
			System.out.println(repoPath);
			new Pull(i.toString());
			Pull.getChanges();
			System.out.println();
		}
	}
}
