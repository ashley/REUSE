import java.io.File;
import java.io.IOException;

public class unn_3_b {
	public static void main(String [] args){
		File directory = new File("");
			if(!directory.exists()){
				directory.mkdir();
			}
	}
}

