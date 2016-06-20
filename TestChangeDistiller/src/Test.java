import java.io.File;
import java.io.IOException;

public class Test {
	
	public static void main(String[] args) throws IOException {
		//Repo aRepo = new Repo();
		String repoPath = "/Users/ashleychen/Desktop/REUSE/REUSE/Repos/";
		File [] pulls = new File(repoPath).listFiles();
		for (File i: pulls){
			if(!i.toString().equals("/Users/ashleychen/Desktop/REUSE/REUSE/Repos/.DS_Store")){
				new Repo(i);
			}
		}
	}

}
