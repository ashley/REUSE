import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Javadoc;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureDiffNode;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureFinalDiffNode;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureNode;
import codemining.ast.TreeNode;
import codemining.ast.java.AbstractJavaTreeExtractor;
import codemining.ast.java.BinaryJavaAstTreeExtractor;
import codemining.ast.java.JavaAstTreeExtractor;
import codemining.ast.java.TempletizedJavaTreeExtractor;
import codemining.ast.java.VariableTypeJavaTreeExtractor;
import codemining.lm.tsg.FormattedTSGrammar;
import codemining.lm.tsg.TSGNode;
import codemining.lm.tsg.samplers.AbstractTSGSampler;
import codemining.lm.tsg.samplers.CollapsedGibbsSampler;
import codemining.lm.tsg.samplers.TempletizedCollapsedGibbsSampler;
import codemining.lm.tsg.tui.java.SampleTSG;
import codemining.util.serialization.Serializer;
import codemining.util.serialization.ISerializationStrategy.SerializationException;

public class TestTreeLM {

	/**
	 * @param args
	 * @throws IOException
	 * @throws SerializationException
	 */
	public static void main(final String[] args) throws IOException, SerializationException {
		if (args.length < 4) {
			System.err.println(
					"buggyFiles <DirectoryPath>, fixedFiles <DirectoryPath>, mode <normal|binary|variables>, iterations <number>, modelDestination <DirectoryPath>");
			return;
		}
		final String serializedFile = args.length == 5 ? args[4].trim() + ".ser" : "tsg.ser";
		final int nIterations = Integer.parseInt(args[3]);

		final File samplerCheckpoint = new File("tsgSampler.ser");
		final CollapsedGibbsSampler sampler;

		if (samplerCheckpoint.exists()) {
			sampler = (CollapsedGibbsSampler) Serializer.getSerializer().deserializeFrom("tsgSampler.ser");
			LOGGER.info("Resuming sampling");

		} else {

			final AbstractJavaTreeExtractor format;
			if (args[2].equals("normal")) {
				format = new JavaAstTreeExtractor();

				sampler = new CollapsedGibbsSampler(20, 10, new FormattedTSGrammar(format),
						new FormattedTSGrammar(format));
			} else if (args[1].equals("binary")) {
				format = new BinaryJavaAstTreeExtractor(new JavaAstTreeExtractor());

				sampler = new CollapsedGibbsSampler(20, 10, new FormattedTSGrammar(format),
						new FormattedTSGrammar(format));
			} else if (args[1].equals("binary-metavariables")) {
				format = new BinaryJavaAstTreeExtractor(new TempletizedJavaTreeExtractor());
				sampler = new TempletizedCollapsedGibbsSampler(20, 10, format);
			} else if (args[1].equals("metavariables")) {
				format = new TempletizedJavaTreeExtractor();
				sampler = new TempletizedCollapsedGibbsSampler(20, 10, format);
			} else if (args[1].equals("variables")) {
				format = new VariableTypeJavaTreeExtractor();
				sampler = new CollapsedGibbsSampler(20, 10, new FormattedTSGrammar(format),
						new FormattedTSGrammar(format));
			} else {
				throw new IllegalArgumentException("Unrecognizable parameter " + args[2]);
			}
			final double percentRootsInit = .9;
			int nFiles = 0;
			int nNodes = 0;
			
			File[] beforeDirectory = new File(args[0]).listFiles();
			File[] afterDirectory = new File(args[1]).listFiles();
			
			for (int i=0;i<beforeDirectory.length;i++) {
				try {
					//StructureFinalDiffNode cu = modifiedDistiller(beforeDirectory[i].getAbsolutePath(),afterDirectory[i].getAbsolutePath());
					StructureNode cu = analyzeDistiller(beforeDirectory[i].getAbsolutePath(),afterDirectory[i].getAbsolutePath());
					if (cu != null){
						ASTNode treeInt = cu.getASTNode();
						prepareAST(treeInt);
						//org.eclipse.jdt.core.dom.ASTNode treeInt = format.getDistillerTree(fi);
						TreeNode<Integer>  formatted = format.getTree(treeInt);
						//TreeNode<Integer> changeFormatted = format.getChangeTree(cu);
						final TreeNode<TSGNode> ast = TSGNode.convertTree(formatted, percentRootsInit);
						nNodes += ast.getTreeSize();
						nFiles++;
						sampler.addTree(ast);
					}
					else{
						System.err.println("Tree Expression is null");
					}
				} catch (final Exception e) {
					LOGGER.warning(
							"Failed to get AST for " + args[0] + " " + ExceptionUtils.getFullStackTrace(e));
				}
			}
			LOGGER.info("Loaded " + nFiles + " files containing " + nNodes
					+ " nodes");
			sampler.lockSamplerData();
		}

		final AtomicBoolean finished = new AtomicBoolean(false);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				int i = 0;
				while (!finished.get() && i < 1000) {
					try {
						Thread.sleep(500);
						i++;
					} catch (final InterruptedException e) {
						LOGGER.warning(ExceptionUtils.getFullStackTrace(e));
					}
				}
			}

		});

		final int nItererationCompleted = sampler.performSampling(nIterations);

		final FormattedTSGrammar grammarToUse;
		if (nItererationCompleted >= nIterations) {
			LOGGER.info("Sampling complete. Outputing burnin grammar...");
			grammarToUse = (FormattedTSGrammar) sampler.getBurnInGrammar();
		} else {
			LOGGER.warning("Sampling not complete. Outputing sample grammar...");
			grammarToUse = (FormattedTSGrammar) sampler.getSampleGrammar();
		}
		
		
		try {
			Serializer.getSerializer().serialize(grammarToUse, serializedFile);
			LOGGER.info("Serialize grammar complete");
		} catch (final Throwable e) {
			LOGGER.severe("Failed to serialize grammar: " + ExceptionUtils.getFullStackTrace(e));
			System.out.println("Failed to serialize grammar: " + ExceptionUtils.getFullStackTrace(e));
			
		}

		try {
			Serializer.getSerializer().serialize(sampler, "tsgSamplerCheckpoint.ser");
		} catch (final Throwable e) {
			LOGGER.severe("Failed to checkpoint sampler: " + ExceptionUtils.getFullStackTrace(e));
			System.out.println("Failed to checkpoint sampler: " + ExceptionUtils.getFullStackTrace(e));
		}

		// sampler.pruneNonSurprisingRules(1);
		sampler.pruneRareTrees((int) (AbstractTSGSampler.BURN_IN_PCT * nIterations) - 10);
		System.out.println("Working: " + grammarToUse.toString());
		finished.set(true); // we have finished and thus the shutdown hook can
		// now stop waiting for us.
	}

	private static final Logger LOGGER = Logger.getLogger(SampleTSG.class.getName());
	
	public static StructureFinalDiffNode modifiedDistiller(String before, String after){
		FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
		File file1 = new File(before);
		File file2 = new File(after);

		StructureFinalDiffNode outcome = distiller.extractChangeNode(file1, file2);
		
		//List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
		/*for (SourceCodeChange change: changes){
			System.out.println(change);
		}*/
		return outcome;
	}
	
	public static StructureNode analyzeDistiller(String before, String after){
		FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
		File file1 = new File(before);
		File file2 = new File(after);

		StructureNode outcome = distiller.extractClassifiedSourceCodeChanges(file1, file2);
		//List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
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