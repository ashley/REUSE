import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;

public class Distiller {
	private static ArrayList<String> changesInString = new ArrayList<String>();
	private static List<SourceCodeChange> changes;
	private static ArrayList<Integer> sigLevels = new ArrayList<Integer>();
	private static Map<String,Integer> changeMap = new HashMap<>();
	
	Distiller(){
		resetSigLevel();
		changeMap = createChangeMap();
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
		    	System.out.println("ENTITY: " + change.getParentEntity().getLabel());
		    	changesInString.add("ENTITY: " + change.getParentEntity().getLabel());
		    	changeMap.put(change.getParentEntity().getLabel(), changeMap.get(change.getParentEntity().getLabel()) + 1);
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
	
	public Map<String,Integer> getChangeMap(){
		return changeMap;
	}
	
	public Map<String, Integer> createChangeMap(){
		changeMap.put("ARGUMENTS",0);
		changeMap.put("ARRAY_ACCESS",0);
		changeMap.put("ARRAY_CREATION",0);
		changeMap.put("ARRAY_INITIALIZER",0);
		changeMap.put("ARRAY_TYPE",0);
		changeMap.put("ASSERT_STATEMENT",0);
		changeMap.put("ASSIGNMENT",0);
		changeMap.put("FIELD",0);
		changeMap.put("BLOCK",0);
		changeMap.put("BLOCK_COMMENT",0);
		changeMap.put("BODY",0);
		changeMap.put("BOOLEAN_LITERAL",0);
		changeMap.put("BREAK_STATEMENT",0);
		changeMap.put("CAST_EXPRESSION",0);
		changeMap.put("CATCH_CLAUSE",0);
		changeMap.put("CATCH_CLAUSES",0);
		changeMap.put("CHARACTER_LITERAL",0);
		changeMap.put("CLASS",0);
		changeMap.put("CLASS_INSTANCE_CREATION",0);
		changeMap.put("COMPILATION_UNIT",0);
		changeMap.put("CONDITIONAL_EXPRESSION",0);
		changeMap.put("CONSTRUCTOR_INVOCATION",0);
		changeMap.put("CONTINUE_STATEMENT",0);
		changeMap.put("DO_STATEMENT",0);
		changeMap.put("ELSE_STATEMENT",0);
		changeMap.put("EMPTY_STATEMENT",0);
		changeMap.put("FOREACH_STATEMENT",0);
		changeMap.put("FIELD_ACCESS",0);
		changeMap.put("FIELD_DECLARATION",0);
		changeMap.put("FINALLY",0);
		changeMap.put("FOR_STATEMENT",0);
		changeMap.put("IF_STATEMENT",0);
		changeMap.put("INFIX_EXPRESSION",0);
		changeMap.put("INSTANCEOF_EXPRESSION",0);
		changeMap.put("JAVADOC",0);
		changeMap.put("LABELED_STATEMENT",0);
		changeMap.put("LINE_COMMENT",0);
		changeMap.put("METHOD",0);
		changeMap.put("METHOD_DECLARATION",0);
		changeMap.put("METHOD_INVOCATION",0);
		changeMap.put("MODIFIER",0);
		changeMap.put("MODIFIERS",0);
		changeMap.put("NULL_LITERAL",0);
		changeMap.put("NUMBER_LITERAL",0);
		changeMap.put("PARAMETERIZED_TYPE",0);
		changeMap.put("PARAMETERS",0);
		changeMap.put("PARAMETER",0);
		changeMap.put("POSTFIX_EXPRESSION",0);
		changeMap.put("PREFIX_EXPRESSION",0);
		changeMap.put("PRIMITIVE_TYPE",0);
		changeMap.put("QUALIFIED_NAME",0);
		changeMap.put("QUALIFIED_TYPE",0);
		changeMap.put("RETURN_STATEMENT",0);
		changeMap.put("ROOT_NODE",0);
		changeMap.put("SIMPLE_NAME",0);
		changeMap.put("SINGLE_TYPE",0);
		changeMap.put("STRING_LITERAL",0);
		changeMap.put("SUPER_INTERFACE_TYPES",0);
		changeMap.put("SWITCH_CASE",0);
		changeMap.put("SWITCH_STATEMENT",0);
		changeMap.put("SYNCHRONIZED_STATEMENT",0);
		changeMap.put("THEN_STATEMENT",0);
		changeMap.put("THROW",0);
		changeMap.put("THROW_STATEMENT",0);
		changeMap.put("TRY_STATEMENT",0);
		changeMap.put("TYPE_PARAMETERS",0);
		changeMap.put("TYPE_DECLARATION",0);
		changeMap.put("TYPE_LITERAL",0);
		changeMap.put("TYPE_PARAMETER",0);
		changeMap.put("VARIABLE_DECLARATION_STATEMENT",0);
		changeMap.put("WHILE_STATEMENT",0);
		changeMap.put("WILDCARD_TYPE",0);
		changeMap.put("FOR_INIT",0);
		changeMap.put("FOR_INCR",0);
		Map<String, Integer> treeMap = new TreeMap<String, Integer>(changeMap);
		return treeMap;
	}
}

