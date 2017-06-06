import java.io.File;
import java.io.IOException;
import java.util.List;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureNode;

public class CD {
	public static void main(String [] args) throws IOException{
		if (args.length != 2) {
			System.err.println("Usage <beforeFilePath> <afterFilePath>");
			return;
		}
		FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
		File file1 = new File(args[0]);
		File file2 = new File(args[1]);

		StructureNode outcome = distiller.extractClassifiedSourceCodeChanges(file1, file2); 
		List<SourceCodeChange> changes = distiller.getSourceCodeChanges(); // To print changes for debugging
		for (SourceCodeChange change: changes){
			System.out.println(change);
		}
					
	}//initialize
}
