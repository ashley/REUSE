import java.io.File;
import java.io.IOException;
import java.util.List;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;

public class CD {
	public static void main(String [] args) throws IOException{
		File left = new File(args[0]);
		File right = new File(args[1]);
		
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
		System.out.println();
		if (changes != null){
			for(SourceCodeChange change: changes){
				System.out.println(change);
				System.out.println(change.getChangedEntity());
		        System.out.println();
			}
		}
		System.out.println();
		System.out.println("Comment changes are : " + distiller.getCommentChanges());
					
	}//initialize
}
