import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;

public class Distiller {
	private static ArrayList<String> changesInString = new ArrayList<String>();
	private static List<SourceCodeChange> changes;
	private static ArrayList<Integer> sigLevels = new ArrayList<Integer>();
	
	Distiller(){
		resetSigLevel();
	}
	
	public void executeDistiller(File before, File after) throws IOException{
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
		changes = distiller.getSourceCodeChanges();
					
	}//initialize
	
	public void getChanges(){
		if(changes.isEmpty()){
			//changesInString.add("DISTILLER " + "EMPTY");
		}
		else if(changes != null) {
			int current = 0;
			changesInString.clear();
		    for(SourceCodeChange change : changes){
		    	if (change.getSignificanceLevel().toString().equals("LOW")){
		    		sigLevels.set(0, sigLevels.get(0)+1);
		    	}
		    	else if (change.getSignificanceLevel().toString().equals("MEDIUM")) {
		    		sigLevels.set(1,sigLevels.get(1)+1);
		    	}
		    	else{
		    		sigLevels.set(2,sigLevels.get(2)+1);
		    	}
		    	System.out.println("DISTILLER " + Integer.toString(current) + ": " + change.getLabel()); 
		    	System.out.println("DISTILLER " + Integer.toString(current) + ": " + change.getSignificanceLevel()); 
		    	changesInString.add("DISTILLER " + Integer.toString(current) + ": " + change.getLabel()); //for storing prints
		    	changesInString.add("DISTILLER " + Integer.toString(current) + ": " + change.getSignificanceLevel()); 
		    	current++;
		    }
		    System.out.println();
		}
	}
	
	public ArrayList<String> getArrayList(){
		return (changesInString);
	}
	
	public ArrayList<Integer> getSigList(){
		return (sigLevels);
	}
	
	public void clearArrayList(){
		changesInString.clear();
	}
	
	public void resetSigLevel(){
		sigLevels.clear();
		sigLevels.add(0,0);
		sigLevels.add(1,0);
		sigLevels.add(2,0);
	}

}

