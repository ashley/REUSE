import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureNode;
import codemining.ast.TreeNode;
import codemining.ast.java.AbstractJavaTreeExtractor;
import codemining.ast.java.JavaAstTreeExtractor;
import codemining.ast.java.JavaAstTreeExtractor.TreeNodeExtractor;
import codemining.lm.tsg.tui.java.GenerateRandom;
import codemining.lm.tsg.tui.java.SampleTSG;
import codemining.util.serialization.ISerializationStrategy.SerializationException;

public class Test {
	
	public static void main(String[] args) throws IOException, SerializationException{
		String [] addresses1 = {"testfiles/TestLeft.java","testfiles/TestRight.java"};	
		String [] addresses2 = {"testfiles/Circle_BEFORE.java","testfiles/Circle_AFTER.java"};	
		
		
		String [] trainingFiles = {"beforeFiles","normal","100","afterFiles"};
		TestTreeLM.main(trainingFiles); //Testing modified SampleTSG
		
		System.out.println("----------------------------------------------------------------------------------------------------");
		//String [] entropyIng = {"testfiles/Circle_AFTER.java","testfiles/Circle_BEFORE.java"};
		String [] entropyIng = {"beforeFiles/test1.java","afterFiles/test1.java"};
		TestTsgEntropy.main(entropyIng); //Testing modified Entropy generator
	}
	
	public static StructureNode analyzeDistiller(String before, String after){
		FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
		File file1 = new File(before);
		File file2 = new File(after);

		StructureNode outcome = distiller.extractClassifiedSourceCodeChanges(file1, file2);
		List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
		/*for (SourceCodeChange change: changes){
			System.out.println(change);
		}*/
		return outcome;
	}
	
	public static void distillAllFiles() throws IOException, SerializationException{
		String repoPath = "/Users/ashleychen/Desktop/REUSE/REUSE/Repos/";
		File [] repos = new File(repoPath).listFiles();
		for (File i: repos){
			if(!i.toString().equals("/Users/ashleychen/Desktop/REUSE/REUSE/Repos/.DS_Store")){
				new Repo(i);
			}
		}
	}


}
