import java.io.IOException;

import codemining.lm.tsg.tui.TsgEntropy;
import codemining.lm.tsg.tui.java.SampleTSG;
import codemining.util.serialization.ISerializationStrategy.SerializationException;

public class StoreEntropy {
	public static String [] entropyLevel(String pullDirectory) throws IOException, SerializationException{
		String [] arg = {"/Users/ashleychen/Desktop/REUSE/REUSE/TestChangeDistiller/tsg.ser",pullDirectory};
		
		//String [] arguments = {"/Users/ashleychen/Desktop/CS113","normal","1"};
		//SampleTSG.main(arguments);
		
		
		TsgEntropy te = new TsgEntropy();
		te.main(arg);
		String [] entropies = te.getEntropy(); //entropy, cross-entropy
		return entropies;
	}//main
}//class
