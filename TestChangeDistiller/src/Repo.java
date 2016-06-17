import java.io.File;

public class Repo {
	Repo(){
		String repoPath = "/Users/ashleychen/Desktop/REUSE/REUSE/Repos/MagicBattle/";
		//aPull.getChanges();
		
		File [] pulls = new File(repoPath).listFiles();
		for (File i: pulls){
			System.out.println(i);
			new Pull(i.toString()).getChanges();
			System.out.println();
		}
	}
}
