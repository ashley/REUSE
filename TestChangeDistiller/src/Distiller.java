import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;

public class Distiller {
	Distiller(int i,File before, File after) throws IOException{
		File left = before;
		File right = after;
		
		FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
		try {
		    distiller.extractClassifiedSourceCodeChanges(left, right);
		} catch(Exception e) {
		    /* An exception most likely indicates a bug in ChangeDistiller. Please file a
		       bug report at https://bitbucket.org/sealuzh/tools-changedistiller/issues and
		       attach the full stack trace along with the two files that you tried to distill. */
		    System.err.println("Warning: error while change distilling. " + e.getMessage());
		}
		List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
					
		if(changes.isEmpty()){
			System.out.println("Empty");
		}
		else if(changes != null) {
			int current = 0;
			ArrayList<String> changesInString = new ArrayList<String>();
		    for(SourceCodeChange change : changes){
		    	System.out.println(Integer.toString(current) + ": " + change); 
		    	changesInString.add(Integer.toString(current) + ": " + change);
		    List<String> lines = changesInString;
		    Path textFile = Paths.get(before.toString().split("BEFORE.txt")[0]+"CHANGES.txt");
		    Files.write(textFile, lines, Charset.forName("UTF-8"));
		    
		    	current++;
		    }
		    System.out.println();
		}
	}
}
