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
import codemining.lm.tsg.tui.java.SampleTSG;
import codemining.util.serialization.ISerializationStrategy.SerializationException;

public class Test {
	
	public static void main(String[] args) throws IOException, SerializationException{
		//String [] arguments = {"/Users/ashleychen/Desktop/CS113","normal","1"};
		//SampleTSG.main(arguments);
		//distillAllFiles();
		//String [] addresses = {"testfiles/Circle_BEFORE.java", "testfiles/Circle_AFTER.java"};
		//CD.main(addresses);
		
		
		//StructureNode cu = analyzeDistiller(addresses[0],addresses[1]);
		
		//org.eclipse.jdt.core.dom.ASTNode compTree = makeTree(addresses[0]);
		//org.eclipse.jdt.core.dom.ASTNode changeTree = makeTree(cu);
		String [] ingredients = {"testfiles/Circle_BEFORE.java","normal","2","testfiles/Circle_AFTER.java"};
		TestTreeLM.main(ingredients);
		//String [] tsgIng = {"testfiles","normal","2"};
		//SampleTSG.main(tsgIng);
		/*
		final AbstractJavaTreeExtractor format = new JavaAstTreeExtractor();
		TreeNode <Integer> treeInt = format.getTree(compTree);
		System.out.println("Done");
		TreeNode <Integer> intTree = makeActualTree(addresses[0]); 
		
		System.out.println("Done");
		*/
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

	public static org.eclipse.jdt.core.dom.ASTNode makeTree(String filePath) throws IOException{
		final int nIterations = Integer.parseInt("1");
		final AbstractJavaTreeExtractor format;
		format = new JavaAstTreeExtractor();
	
		final double percentRootsInit = .9;
		int nFiles = 0;
		int nNodes = 0;
		final File fi = new File(filePath);
				//org.eclipse.jdt.core.dom.ASTNode treeInt = format.getDistillerTree(fi);
				org.eclipse.jdt.core.dom.CompilationUnit treeInt = format.getDistillerTree(fi);//insert Distiller Tree here
				return treeInt;
	}
	
	public static org.eclipse.jdt.core.dom.ASTNode makeTree(StructureNode jsn) throws IOException{
		final int nIterations = Integer.parseInt("1");
		final AbstractJavaTreeExtractor format;
		format = new JavaAstTreeExtractor();
	
		final double percentRootsInit = .9;
		int nFiles = 0;
		int nNodes = 0;
		//org.eclipse.jdt.core.dom.CompilationUnit treeInt = format.getDistillerTree(jsn);//insert Distiller Tree here
		org.eclipse.jdt.core.dom.CompilationUnit treeInt = jsn.getASTNode();
		return treeInt;
	}
	public static TreeNode <Integer> makeActualTree(String filePath) throws IOException{
		final int nIterations = Integer.parseInt("1");
		final AbstractJavaTreeExtractor format;
		format = new JavaAstTreeExtractor();
	
		final double percentRootsInit = .9;
		int nFiles = 0;
		int nNodes = 0;
		final File fi = new File(filePath);
				TreeNode <Integer> treeInt = format.getTree(fi);
				return treeInt;
	}
	
	private static final Logger LOGGER = Logger.getLogger(SampleTSG.class.getName());

}
