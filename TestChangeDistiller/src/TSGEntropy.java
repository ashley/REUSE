import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.lang.exception.ExceptionUtils;

import codemining.ast.AbstractTreeExtractor;
import codemining.ast.TreeNode;
import codemining.languagetools.ITokenizer;
import codemining.lm.tsg.TSGNode;
import codemining.lm.tsg.TSGrammar;
import codemining.lm.tsg.TreeProbabilityComputer;
import codemining.util.serialization.ISerializationStrategy.SerializationException;
import codemining.util.serialization.Serializer;

/**
 * Compute the TSG entropy and cross-entropy of a set of file.
 *
 * @author Miltos Allamanis <m.allamanis@ed.ac.uk>
 *
 */
public class TSGEntropy {

	/**
	 * @param args
	 * @throws SerializationException
	 */
	
	static double crossEntropy = 0;
	static double probability = 0;
	static TSGrammar<TSGNode> grammar;
	

	public TSGEntropy(String f) throws SerializationException {
	}
	
	public static void main(String path) throws SerializationException {

		//final File directory = new File(path);
		final File f = new File(path);

		//final Collection<File> allFiles = FileUtils.listFiles(directory,new RegexFileFilter(".*\\.txt$"),DirectoryFileFilter.DIRECTORY);
		grammar = (TSGrammar<TSGNode>) Serializer
				.getSerializer().deserializeFrom("tsg1.ser");
		final AbstractTreeExtractor treeFormat = grammar.getTreeExtractor();

		System.out.println("filename,entropy,cross-entropy");
		//for (final File f : allFiles) {
			try {
				System.err.println("FILE:" + f.toString());
				final TreeNode<Integer> intTree = treeFormat.getTree(f);
				final TreeNode<TSGNode> tsgTree = TSGNode.convertTree(intTree,
						0);

				final ITokenizer tokenizer = treeFormat.getTokenizer();
				final List<String> fileTokens = tokenizer
						.tokenListFromCode(FileUtils.readFileToString(f)
								.toCharArray());

				final TreeProbabilityComputer<TSGNode> probabilityComputer = new TreeProbabilityComputer<TSGNode>(
						grammar, false, TreeProbabilityComputer.TSGNODE_MATCHER);
				probability = probabilityComputer
						.getLog2ProbabilityOf(tsgTree);

				crossEntropy = probability / fileTokens.size();
				System.out.println(f.toString() + "," + probability + ","
						+ crossEntropy);
			} catch (final IOException e) {
				LOGGER.warning(ExceptionUtils.getFullStackTrace(e));
			}
		//}

	}
	
	public static Double [] getEntropy(){
		Double [] entropies = {probability,crossEntropy};
		return entropies;
	}

	private static final Logger LOGGER = Logger.getLogger(TSGEntropy.class
			.getName());


}
