import java.io.File;
import java.io.IOException;

public class Pull {
	private static File pull;
	private static File[] files;
	private static String before = "_BEFORE.txt";
	private static String after = "_AFTER.txt";
	
	
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
	
	public static void getChanges() throws IOException{
		for (int i = 0; i<getFilesCount();i++){
			if(files[i].listFiles().length == 2){
				new Distiller(i,getFileVersion(i,"before"),getFileVersion(i,"after"));
			}
		}//for 
	}//getChanges
	
}
