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

		StructureNode cu = analyzeDistiller();
		
		org.eclipse.jdt.core.dom.ASTNode compTree = makeTree();
		
		//final TreeNodeExtractor ex = new TreeNodeExtractor(false);
		//ex.extractFromNode(compTree);
		
		final AbstractJavaTreeExtractor format = new JavaAstTreeExtractor();
		TreeNode <Integer> treeInt = format.getTree(compTree);
		
		TreeNode <Integer> intTree = makeActualTree(); 
		
		System.out.println("Done");
	}
	
	public static StructureNode analyzeDistiller(){
		FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
		File file1 = new File("/Users/ashleychen/Desktop/REUSE/REUSE/Repos/Game/10_Accepted/8ab034ba/8ab034ba_AFTER.txt");
		File file2 = new File("/Users/ashleychen/Desktop/REUSE/REUSE/Repos/Game/10_Accepted/8ab034ba/8ab034ba_BEFORE.txt");

		StructureNode outcome = distiller.extractClassifiedSourceCodeChanges(file1, file2);
		List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
		for (SourceCodeChange change: changes){
			System.out.print(change);
		}
		return outcome;
	}
	
	public static void distillAllFiles() throws IOException{
		String repoPath = "/Users/ashleychen/Desktop/REUSE/REUSE/Repos/";
		File [] pulls = new File(repoPath).listFiles();
		for (File i: pulls){
			if(!i.toString().equals("/Users/ashleychen/Desktop/REUSE/REUSE/Repos/.DS_Store")){
				new Repo(i);
			}
		}
	}

	public static org.eclipse.jdt.core.dom.ASTNode makeTree() throws IOException{
		final int nIterations = Integer.parseInt("1");
		final AbstractJavaTreeExtractor format;
		format = new JavaAstTreeExtractor();
	
		final double percentRootsInit = .9;
		int nFiles = 0;
		int nNodes = 0;
		final File fi = new File("/Users/ashleychen/Desktop/FixedFindBugsWarningsRuleTest.java");
				//new RegexFileFilter(".*\\.java$"),
				//DirectoryFileFilter.DIRECTORY)) {
//				org.eclipse.jdt.core.dom.ASTNode treeInt = format.getDistillerTree(fi);
				//org.eclipse.jdt.core.dom.CompilationUnit compTree = format.getDistillerTree(fi);//insert Distiller Tree here
				return null;
	}
	public static TreeNode <Integer> makeActualTree() throws IOException{
		final int nIterations = Integer.parseInt("1");
		final AbstractJavaTreeExtractor format;
		format = new JavaAstTreeExtractor();
	
		final double percentRootsInit = .9;
		int nFiles = 0;
		int nNodes = 0;
		final File fi = new File("/Users/ashleychen/Desktop/FixedFindBugsWarningsRuleTest.java");
				//new RegexFileFilter(".*\\.java$"),
				//DirectoryFileFilter.DIRECTORY)) {
				TreeNode <Integer> treeInt = format.getTree(fi);
				return treeInt;
	}
	
	private static final Logger LOGGER = Logger.getLogger(SampleTSG.class.getName());

}
