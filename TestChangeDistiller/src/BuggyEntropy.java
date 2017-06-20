import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Javadoc;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureNode;
import codemining.ast.TreeNode;
import codemining.ast.java.AbstractJavaTreeExtractor;
import codemining.languagetools.ITokenizer;
import codemining.lm.tsg.TSGNode;
import codemining.lm.tsg.TSGrammar;
import codemining.lm.tsg.TreeProbabilityComputer;
import codemining.lm.tsg.tui.TsgEntropy;
import codemining.util.serialization.Serializer;
import codemining.util.serialization.ISerializationStrategy.SerializationException;

public class BuggyEntropy {
	static double crossEntropy = 0;
	static double probability = 0;
	static TSGrammar<TSGNode> grammar;
	
	@SuppressWarnings("unchecked")
	public static void main(String [] args) throws SerializationException, IOException{
		if (args.length < 5) {
			System.err.println(
					"Repository <directory>  Model <.ser>  firstInt, secondInt  filename");
			return;
		}
		
		grammar = (TSGrammar<TSGNode>) Serializer
				.getSerializer().deserializeFrom(args[1]);
		
		File[] repository = new File(args[0]).listFiles();
		int first = Integer.parseInt(args[2]);
		int second = Integer.parseInt(args[3]);
		String filename = args[4];
		
		Double ENTROPY = 0.0;
		Double CE_AVG = 0.0;
		Double CE_LEFT = 0.0;
		Double CE_RIGHT = 0.0;
		Double CE_DIFF = 0.0;
		Integer COMPUTED_COMMITS = 0;
		
		ArrayList<String> formattedEntropy = new ArrayList<String>();
		
		for(int i=first;i<second;i++){
				File commits = repository[i];
				if (commits.isDirectory()){
					File[] buggyFolders = commits.listFiles();
					if(buggyFolders.length == 2){
						System.out.println(buggyFolders[0].getName().equals("b"));
						System.out.println(buggyFolders[0].getName().equals("f"));
						Collection<File> beforeDirectory = null;
						Collection<File> afterDirectory = null;
						if(buggyFolders[0].getName().equals("b")){
							beforeDirectory = FileUtils.listFiles(buggyFolders[0], new RegexFileFilter(".*\\.java$"),DirectoryFileFilter.DIRECTORY);
							afterDirectory = FileUtils.listFiles(buggyFolders[1], new RegexFileFilter(".*\\.java$"),DirectoryFileFilter.DIRECTORY);
						}
						else if(buggyFolders[0].getName().equals("f")){
							beforeDirectory = FileUtils.listFiles(buggyFolders[1], new RegexFileFilter(".*\\.java$"),DirectoryFileFilter.DIRECTORY);
							afterDirectory = FileUtils.listFiles(buggyFolders[0], new RegexFileFilter(".*\\.java$"),DirectoryFileFilter.DIRECTORY);
						}
						if(beforeDirectory != null && afterDirectory != null){
							System.out.println("Directories are not NULL");
							Iterator<File> iBefore = beforeDirectory.iterator();
							Iterator<File> iAfter = afterDirectory.iterator();
							while (iBefore.hasNext() && iAfter.hasNext()) {
									File before = iBefore.next();
									File after = iAfter.next();
									String beforePath = before.getAbsolutePath();
									String afterPath = after.getAbsolutePath();
									if(before.getName().equals(after.getName())){
										System.out.println(beforePath);
										HashMap<String,Double> entropy = computeEntropy(beforePath,afterPath);
										formattedEntropy.add(beforePath + " " +
												entropy.get("Changes") + " " + 
												entropy.get("CE-Avg") + " " +
												entropy.get("CE-Left") + " " +
												entropy.get("CE-Right") + " " +
												entropy.get("CE-Diff") 
												);
										ENTROPY += entropy.get("Changes");
										CE_AVG += entropy.get("CE-Avg");
										CE_LEFT += entropy.get("CE-Left");
										CE_RIGHT += entropy.get("CE-Right");
										CE_DIFF += entropy.get("CE-Diff");
										COMPUTED_COMMITS++;
									}
							}
						}
				}
			}
			System.out.println("ENTROPY: " + ENTROPY/COMPUTED_COMMITS + "\n" +
								"CE-AVG: " + CE_AVG/COMPUTED_COMMITS + "\n" +
								"CE-LEFT: " + CE_LEFT/COMPUTED_COMMITS + "\n" + 
 								"CE-RIGHT: " + CE_RIGHT/COMPUTED_COMMITS + "\n" +
								"CE-DIFF: " + CE_DIFF/COMPUTED_COMMITS);
			StringBuilder sb = new StringBuilder();
			sb.append("before_file_path change CE-Avg CE-Left CE-Right CE-Diff" + "\n");
			for(String s: formattedEntropy){
				sb.append(s);
				sb.append("\n");
			}
			writeCSV(sb.toString(),filename);
		}
	}//main

	public static void writeCSV(String body, String filename) throws IOException{
		FileWriter fw = null;
		BufferedWriter bw = null;
		try{
			fw = new FileWriter(filename);
			 bw = new BufferedWriter(fw);
			bw.write(body);
		}
		catch (Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				if(bw !=null)
					bw.close();
				if(fw!=null)
					fw.close();
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
		
	}
	
	public static HashMap<String,Double> computeEntropy(String before, String after) throws IOException{
		StructureNode cu = analyzeDistiller(before, after); //file 1 and file 2
		final AbstractJavaTreeExtractor format = (AbstractJavaTreeExtractor) grammar.getTreeExtractor();
		if (cu != null){
			final ASTNode astTree = cu.getASTNode(); //new
			prepareAST(astTree);
			final TreeNode<Integer> intTree = format.getTree(astTree);
			final TreeNode<TSGNode> tsgTree = TSGNode.convertTree(intTree, 0); //new

			final ITokenizer tokenizer = format.getTokenizer();
			final List<String> fileTokensLeft = tokenizer.tokenListFromCode(FileUtils.readFileToString(new File(before)).toCharArray());
			final List<String> fileTokensRight = tokenizer.tokenListFromCode(FileUtils.readFileToString(new File(after)).toCharArray());

			final TreeProbabilityComputer<TSGNode> probabilityComputer = new TreeProbabilityComputer<TSGNode>(grammar, false, TreeProbabilityComputer.TSGNODE_MATCHER);
			probability = probabilityComputer
					.getLog2ProbabilityOf(tsgTree);
			
			HashMap<String,Double> results = new HashMap<String,Double>();
			
			results.put("Changes", probability / fileTokensLeft.size());
			results.put("CE-Left", probability / fileTokensLeft.size());
			results.put("CE-Right", probability / fileTokensRight.size());
			results.put("CE-Avg", probability / (fileTokensLeft.size()+fileTokensRight.size()/2));
			results.put("CE-Diff", (probability / fileTokensLeft.size() - probability / fileTokensRight.size()));

			return results;
		}
		else{
			System.err.println("Cannot calculate entropy level. Change-Tree Expression is NULL");
			return null;
		}
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
		
}//class
