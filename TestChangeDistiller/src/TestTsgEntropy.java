import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Javadoc;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureNode;
import codemining.ast.java.AbstractJavaTreeExtractor;
import codemining.ast.TreeNode;
import codemining.languagetools.ITokenizer;
import codemining.lm.tsg.TSGNode;
import codemining.lm.tsg.TSGrammar;
import codemining.lm.tsg.TreeProbabilityComputer;
import codemining.lm.tsg.tui.TsgEntropy;
import codemining.util.serialization.Serializer;
import codemining.util.serialization.ISerializationStrategy.SerializationException;

public class TestTsgEntropy {
	/**
	 * @param args
	 * @throws SerializationException
	 */
	
	static double crossEntropy = 0;
	static double probability = 0;
	static TSGrammar<TSGNode> grammar;
	

	public TestTsgEntropy(String repoName, String reusePath,String iterations) throws SerializationException {
		grammar = (TSGrammar<TSGNode>) Serializer
				.getSerializer().deserializeFrom(reusePath + "/Entropy-model/saved-ser-"+ iterations + "-iters/"+repoName+".ser");
	}
	
	public static void main(String [] args) throws SerializationException, IOException {

		//final File directory = new File(path);
		//final File f = new File(args[0]);
		if (args.length < 3) {
			System.err.println(
					"Before <beforeFile>, After <afterFile>, tsg <tsgFile>");
			return ;
		}
		//final Collection<File> allFiles = FileUtils.listFiles(directory,new RegexFileFilter(".*\\.txt$"),DirectoryFileFilter.DIRECTORY);
		grammar = (TSGrammar<TSGNode>) Serializer
				.getSerializer().deserializeFrom(args[2]);
		//final AbstractTreeExtractor treeFormat = grammar.getTreeExtractor();
		final AbstractJavaTreeExtractor format = (AbstractJavaTreeExtractor) grammar.getTreeExtractor();

		System.out.println("filename,entropy,cross-entropy");
		
		StructureNode cu = analyzeDistiller(args[0],args[1]); //file 1 and file 2
			if (cu != null){
				final ASTNode astTree = cu.getASTNode(); //new
				prepareAST(astTree);
				final TreeNode<Integer> intTree = format.getTree(astTree);
				final TreeNode<TSGNode> tsgTree = TSGNode.convertTree(intTree, 0); //new
	
				final ITokenizer tokenizer = format.getTokenizer();
				final List<String> fileTokensLeft = tokenizer.tokenListFromCode(FileUtils.readFileToString(new File(args[0])).toCharArray());
				final List<String> fileTokensRight = tokenizer.tokenListFromCode(FileUtils.readFileToString(new File(args[1])).toCharArray());
	
				final TreeProbabilityComputer<TSGNode> probabilityComputer = new TreeProbabilityComputer<TSGNode>(grammar, false, TreeProbabilityComputer.TSGNODE_MATCHER);
				probability = probabilityComputer
						.getLog2ProbabilityOf(tsgTree);
	
				System.out.println("Changes: " + probability + "\n" + "CROSS-ENTROPY-LEFT: " + probability / fileTokensLeft.size() + "\n" 
				+ "CROSS-ENTROPY-RIGHT: " + probability / fileTokensRight.size() + "\n" + 
				"CROSS-ENTROPY-AVG: " + probability / (fileTokensLeft.size()+fileTokensRight.size()/2) + "\n" + 
				"CROSS-ENTROPY-DIFF: " + (probability / fileTokensLeft.size() - probability / fileTokensRight.size()));
			}
			else{
				System.err.println("Cannot calculate entropy level. Change-Tree Expression is NULL");
			}
			return;
 
	}
	
	public static Double [] getEntropy(){
		Double [] entropies = {probability,crossEntropy};
		return entropies;
	}

	private static final Logger LOGGER = Logger.getLogger(TsgEntropy.class
			.getName());
	
	public static StructureNode analyzeDistiller(String before, String after){
		FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
		File file1 = new File(before);
		File file2 = new File(after);

		StructureNode outcome = distiller.extractClassifiedSourceCodeChanges(file1, file2);
		//List<SourceCodeChange> changes = distiller.getSourceCodeChanges();  To print changes for debugging
		/*for (SourceCodeChange change: changes){
			System.out.println(change);
		}*/
		return outcome;
	}
	
	public static void prepareAST( ASTNode node ) {
		node.accept( new ASTVisitor() {
			@Override
			public void endVisit( Javadoc node ) {
				node.delete();
			}
		} );
	}

}
