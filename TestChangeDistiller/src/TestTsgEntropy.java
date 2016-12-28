import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.eclipse.jdt.core.dom.ASTNode;

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

		//final Collection<File> allFiles = FileUtils.listFiles(directory,new RegexFileFilter(".*\\.txt$"),DirectoryFileFilter.DIRECTORY);
		grammar = (TSGrammar<TSGNode>) Serializer
				.getSerializer().deserializeFrom("tsg.ser");
		//final AbstractTreeExtractor treeFormat = grammar.getTreeExtractor();
		final AbstractJavaTreeExtractor format = (AbstractJavaTreeExtractor) grammar.getTreeExtractor();

		System.out.println("filename,entropy,cross-entropy");
		
		StructureNode cu = analyzeDistiller(args[0],args[1]); //file 1 and file 2
			final ASTNode astTree = cu.getASTNode(); //new
			final TreeNode<Integer> intTree = format.getTree(astTree);
			final TreeNode<TSGNode> tsgTree = TSGNode.convertTree(intTree, 0); //new

			final ITokenizer tokenizer = format.getTokenizer();
			//final List<String> fileTokens = tokenizer.tokenListFromCode(FileUtils.readFileToString(f).toCharArray());

			final TreeProbabilityComputer<TSGNode> probabilityComputer = new TreeProbabilityComputer<TSGNode>(
					grammar, false, TreeProbabilityComputer.TSGNODE_MATCHER);
			probability = probabilityComputer
					.getLog2ProbabilityOf(tsgTree);

			//crossEntropy = probability / fileTokens.size();
			System.out.println("Changes: " + probability);

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
		List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
		/*for (SourceCodeChange change: changes){
			System.out.println(change);
		}*/
		return outcome;
	}

}
