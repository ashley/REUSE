import java.io.File;
import java.io.IOException;

public class unn_3_f {
	public static void main(String [] args) throws IOException{
		File directory = new File("");
			if(!directory.exists()){
				if (!directory.mkdir()){
					throw new IOException();
				}
			}
	}
}

