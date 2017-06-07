import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.eclipse.cdt.internal.core.dom.parser.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureDiffNode;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureFinalDiffNode;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureNode;
import codemining.ast.TreeNode;
import codemining.ast.java.AbstractJavaTreeExtractor;
import codemining.ast.java.JavaAstTreeExtractor;
import codemining.ast.java.JavaAstTreeExtractor.TreeNodeExtractor;
import codemining.lm.tsg.TSGNode;
import codemining.lm.tsg.tui.TsgEntropy;
import codemining.lm.tsg.tui.java.GenerateRandom;
import codemining.lm.tsg.tui.java.SampleTSG;
import codemining.util.serialization.ISerializationStrategy.SerializationException;

public class Test {
	
	public static void main(String[] args) throws IOException, SerializationException{
		testNewAST();
	}
	
	public static void trainModel() throws IOException, SerializationException{
		String [] changeDist = {"/Users/ashleychen/Desktop/b","/Users/ashleychen/Desktop/f","normal","5","/Users/ashleychen/Desktop/tsg.ser"};
		TestTreeLM.main(changeDist);
	}
	
	public static void getEntropy() throws IOException, SerializationException{
		String [] changeDist = {"/Users/ashleychen/Desktop/test_1.java","/Users/ashleychen/Desktop/test_2.java","/Users/ashleychen/Desktop/REUSE/REUSE/tsg.ser"};
		TestTsgEntropy.main(changeDist);
	}
	
	public static void testCD(){
		FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
		File file1 = new File("/Users/ashleychen/Desktop/test_1.java");
		File file2 = new File("/Users/ashleychen/Desktop/test_2.java");

		StructureNode outcome = distiller.extractClassifiedSourceCodeChanges(file1, file2); 
		List<SourceCodeChange> changes = distiller.getSourceCodeChanges(); // To print changes for debugging
		for (SourceCodeChange change: changes){
			System.out.println(change);
		}
	}
	
	public static void testNewAST(){
		FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
		File file1 = new File("/Users/ashleychen/Desktop/test_1.java");
		File file2 = new File("/Users/ashleychen/Desktop/test_2.java");

		StructureFinalDiffNode outcome = distiller.extractChangeNode(file1, file2);
		List<SourceCodeChange> changes = distiller.getSourceCodeChanges(); // To print changes for debugging
		for (SourceCodeChange change: changes){
			System.out.println(change);
		}
	}

	public static void getRandomCode() throws IOException, SerializationException{
		String [] changeDist = {"/Users/ashleychen/Desktop/REUSE/REUSE/Results/SourceCodeModel.ser","1"};
		GenerateRandom.main(changeDist);
	}
}