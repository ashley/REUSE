import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;

public class TestCD {
	public static void main(String [] args) throws IOException{

		File left = new File("testfiles/Circle_BEFORE.java");
		File right = new File("testfiles/Circle_AFTER.java");
		
		if (args.length == 2){
			System.err.println("First Arg should be BEFORE file path. Second Arg should be AFTEr file path");
		}
		FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
		try {
		    distiller.extractClassifiedSourceCodeChanges(left, right);
		} catch(Exception e) {
		    System.err.println("Warning: error while change distilling. " + e.getMessage());
		}
		List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
		
		for(SourceCodeChange change: changes){
			//System.out.println(change);
		}
		
	}
}
