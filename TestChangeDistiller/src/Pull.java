import java.io.File;
import java.util.List;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;

public class Pull {
	private static File pull;
	private static File[] files;
	private String pullPath;
	private static String before = "_BEFORE.txt";
	private static String after = "_AFTER.txt";
	
	
	Pull(String repoPath){
		pull = new File(repoPath);
		files = pull.listFiles();
		pullPath = pull.toString();
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
	
	public static void getChanges(){
		for (int i = 0; i<getFilesCount();i++){
			new Distiller(i,getFileVersion(i,"before"),getFileVersion(i,"after"));
		}//for
	}//getChanges
	
}
