import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
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

public class TrainTSGCode {

	public static void main(final String[] args) throws IOException, SerializationException {
		if (args.length < 9) {
			System.err.println(
					"Repository <DirectoryPath>, mode <normal|binary|variables>, iterations <number>, fileName <string>, firstInt, secondInt, third Int, fourth Int, # of commits");
			return;
		}
		final String serializedFile = "TestingDirectory_TSG_"+args[3]+".ser";
		final int nIterations = Integer.parseInt(args[2]);

		final File samplerCheckpoint = new File("tsgSampler.ser");
		final CollapsedGibbsSampler sampler;

		if (samplerCheckpoint.exists()) {
			sampler = (CollapsedGibbsSampler) Serializer.getSerializer().deserializeFrom("tsgSampler.ser");
			LOGGER.info("Resuming sampling");

		} else {

			final AbstractJavaTreeExtractor format;
			if (args[1].equals("normal")) {
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
			
			try{
				File[] repository = new File(args[0]).listFiles();
				System.out.println("LENGTH: " + repository.length);
				int first = Integer.parseInt(args[4]);
				int second = Integer.parseInt(args[5]);
				int third = Integer.parseInt(args[6]);
				int fourth = Integer.parseInt(args[7]);
				for(int i=1;i<Integer.parseInt(args[8]);i++){
					if((i >= first && i <=second) || (i >= third && i <= fourth)){
						System.out.println(i);
					File commits = repository[i];
					if (commits.isDirectory()){
						File[] buggyFolders = commits.listFiles();
						if(buggyFolders.length == 2){
							System.out.println("GOING INTO BUGGY FILES");
							System.out.println(buggyFolders[0].getName().equals("b"));
							System.out.println(buggyFolders[0].getName().equals("f"));
							System.out.println("FOLDER NAME: <" + buggyFolders[0].getName()+">");
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
								try {
									String beforePath = iBefore.next().getAbsolutePath();
									String afterPath = iAfter.next().getAbsolutePath();
									System.out.println(beforePath);
									
									
									System.out.println("Starting tree conversion");
									final TreeNode<TSGNode> astB = TSGNode.convertTree(
											format.getTree(new File(beforePath)), percentRootsInit);
									nNodes += astB.getTreeSize();
									nFiles++;
									sampler.addTree(astB);
									final TreeNode<TSGNode> astA = TSGNode.convertTree(
											format.getTree(new File(afterPath)), percentRootsInit);
									nNodes += astA.getTreeSize();
									nFiles++;
									sampler.addTree(astA);
								}
								catch (final Exception e) {
									LOGGER.warning(
										"Failed to get AST for " + args[0] + " " + ExceptionUtils.getFullStackTrace(e));
								}
							}
							}
						}
					}
					}
				}
			}
			catch(Exception e){
				throw e;
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

		sampler.pruneRareTrees((int) (AbstractTSGSampler.BURN_IN_PCT * nIterations) - 10);
		System.out.println("Working: " + grammarToUse.toString());
		finished.set(true); // we have finished and thus the shutdown hook can
		// now stop waiting for us.
	}

	private static final Logger LOGGER = Logger.getLogger(SampleTSG.class.getName());

	public static StructureFinalDiffNode modifiedDistiller(String before, String after) {
		FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
		File file1 = new File(before);
		File file2 = new File(after);

		StructureFinalDiffNode outcome = distiller.extractChangeNode(file1, file2);

		return outcome;
	}

	public static StructureNode analyzeDistiller(String before, String after) {
		FileDistiller distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
		File file1 = new File(before);
		File file2 = new File(after);

		StructureNode outcome = distiller.extractClassifiedSourceCodeChanges(file1, file2);
		return outcome;
	}

	public static void prepareAST(ASTNode node) {
		node.accept(new ASTVisitor() {
			@Override
			public void endVisit(Javadoc node) {
				node.delete();
			}
		});
	}
}
