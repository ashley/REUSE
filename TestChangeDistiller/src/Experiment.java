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
import clegoues.genprog4java.localization.UnexpectedCoverageResultException;
import clegoues.genprog4java.main.Main;
import codemining.ast.TreeNode;
import codemining.ast.java.AbstractJavaTreeExtractor;
import codemining.ast.java.JavaAstTreeExtractor;
import codemining.ast.java.JavaAstTreeExtractor.TreeNodeExtractor;
import codemining.lm.tsg.TSGNode;
import codemining.lm.tsg.tui.TsgEntropy;
import codemining.lm.tsg.tui.java.GenerateRandom;
import codemining.lm.tsg.tui.java.SampleTSG;
import codemining.util.serialization.ISerializationStrategy.SerializationException;

public class Experiment {
	
	public static void main(String[] args) throws IOException, SerializationException, UnexpectedCoverageResultException{
		
		String [] arguments = {"/Users/ashleychen/Desktop/REUSE/REUSE/Git_Scripts/Repos/elasticsearch",
				"/Users/ashleychen/Desktop/REUSE/REUSE/Results/TestingDirectory_elasticsearch_100_1.ser",
				"1","78","entropy_levels.txt"};
		//BuggyEntropy.main(arguments);
		trainNetty();
	}
	
	public static void trainProjects() throws IOException, SerializationException{
		/*
		String [] testing_1 = {"/home/ashley/reuse/Git_Scripts/Repos/openjpa", "normal","50", "openjpa_50_1", "1","2880","0","0","3200"};
		TrainBuggyCode.main(testing_1);
		String [] testing_2 = {"/home/ashley/reuse/Git_Scripts/Repos/openjpa", "normal","50", "openjpa_50_2", "1","2560","2881","3200","3200"};
		TrainBuggyCode.main(testing_2);
		String [] testing_3 = {"/home/ashley/reuse/Git_Scripts/Repos/openjpa", "normal","50", "openjpa_50_3", "1","2240","2561","3200","3200"};
		TrainBuggyCode.main(testing_3);
		String [] testing_4 = {"/home/ashley/reuse/Git_Scripts/Repos/openjpa", "normal","50", "openjpa_50_4", "1","1920","2241","3200","3200"};
		TrainBuggyCode.main(testing_4);
		*/
		
		/*
		String [] testing_5 = {"/home/ashley/reuse/Git_Scripts/Repos/openjpa", "normal","50", "openjpa_50_5", "1","1600","1921","3200","3200"};
		TrainBuggyCode.main(testing_5);
		String [] testing_6 = {"/home/ashley/reuse/Git_Scripts/Repos/openjpa", "normal","50", "openjpa_50_6", "1","1280","1601","3200","3200"};
		TrainBuggyCode.main(testing_6);
		String [] testing_7 = {"/home/ashley/reuse/Git_Scripts/Repos/openjpa", "normal","50", "openjpa_50_7", "1","960","1281","3200","3200"};
		TrainBuggyCode.main(testing_7);
		*/
		
		/*
		String [] testing_8 = {"/home/ashley/reuse/Git_Scripts/Repos/openjpa", "normal","50", "openjpa_50_8", "1","640","961","3200","3200"};
		TrainBuggyCode.main(testing_8);
		String [] testing_9 = {"/home/ashley/reuse/Git_Scripts/Repos/openjpa", "normal","50", "openjpa_50_9", "1","320","641","3200","3200"};
		TrainBuggyCode.main(testing_9);
		String [] testing_10 = {"/home/ashley/reuse/Git_Scripts/Repos/openjpa", "normal","50", "openjpa_50_10", "321","3200","0","0","3200"};
		TrainBuggyCode.main(testing_10);
		*/
	}
	
	public static void trainDerby() throws IOException, SerializationException{
		/*
		String [] testing_1 = {"/home/ashley/reuse/Git_Scripts/Repos/derby", "normal","50", "derby_50_1", "1","8739","0","0","9710"};
		TrainBuggyCode.main(testing_1);
		String [] testing_2 = {"/home/ashley/reuse/Git_Scripts/Repos/derby", "normal","50", "derby_50_2", "1","7768","8740","9710","9710"};
		TrainBuggyCode.main(testing_2);
		String [] testing_3 = {"/home/ashley/reuse/Git_Scripts/Repos/derby", "normal","50", "derby_50_3", "1","6797","7769","9710","9710"};
		TrainBuggyCode.main(testing_3);
		String [] testing_4 = {"/home/ashley/reuse/Git_Scripts/Repos/derby", "normal","50", "derby_50_4", "1","5826","6798","9710","9710"};
		TrainBuggyCode.main(testing_4);
		*/
		
		/*
		String [] testing_5 = {"/home/ashley/reuse/Git_Scripts/Repos/derby", "normal","50", "derby_50_5", "1","4855","5827","9710","9710"};
		TrainBuggyCode.main(testing_5);
		String [] testing_6 = {"/home/ashley/reuse/Git_Scripts/Repos/derby", "normal","50", "derby_50_6", "1","3884","4856","9710","9710"};
		TrainBuggyCode.main(testing_6);
		String [] testing_7 = {"/home/ashley/reuse/Git_Scripts/Repos/derby", "normal","50", "derby_50_7", "1","2913","3885","9710","9710"};
		TrainBuggyCode.main(testing_7);
		*/
		
		/*
		String [] testing_8 = {"/home/ashley/reuse/Git_Scripts/Repos/derby", "normal","50", "derby_50_8", "1","1942","2914","9710","9710"};
		TrainBuggyCode.main(testing_8);
		String [] testing_9 = {"/home/ashley/reuse/Git_Scripts/Repos/derby", "normal","50", "derby_50_9", "1","971","1943","9710","9710"};
		TrainBuggyCode.main(testing_9);
		String [] testing_10 = {"/home/ashley/reuse/Git_Scripts/Repos/derby", "normal","50", "derby_50_10", "972","9710","0","0","9710"};
		TrainBuggyCode.main(testing_10);
		*/
	}
	public static void trainElastic() throws IOException, SerializationException{
		/*
		String [] testing_1 = {"/home/ashley/reuse/Git_Scripts/Repos/elasticsearch", "normal","50", "elasticsearch_50_1", "1","8001","0","0","8890"};
		TrainBuggyCode.main(testing_1);
		String [] testing_2 = {"/home/ashley/reuse/Git_Scripts/Repos/elasticsearch", "normal","50", "elasticsearch_50_2", "1","7112","8001","8890","8890"};
		TrainBuggyCode.main(testing_2);
		String [] testing_3 = {"/home/ashley/reuse/Git_Scripts/Repos/elasticsearch", "normal","50", "elasticsearch_50_3", "1","6223","7112","8890","8890"};
		TrainBuggyCode.main(testing_3);
		String [] testing_4 = {"/home/ashley/reuse/Git_Scripts/Repos/elasticsearch", "normal","50", "elasticsearch_50_4", "1","5334","6223","8890","8890"};
		TrainBuggyCode.main(testing_4);
		*/
		
		/*
		String [] testing_5 = {"/home/ashley/reuse/Git_Scripts/Repos/elasticsearch", "normal","50", "elasticsearch_50_5", "1","4445","5334","8890","8890"};
		TrainBuggyCode.main(testing_5);
		String [] testing_6 = {"/home/ashley/reuse/Git_Scripts/Repos/elasticsearch", "normal","50", "elasticsearch_50_6", "1","3556","4445","8890","8890"};
		TrainBuggyCode.main(testing_6);
		String [] testing_7 = {"/home/ashley/reuse/Git_Scripts/Repos/elasticsearch", "normal","50", "elasticsearch_50_7", "1","2667","3556","8890","8890"};
		TrainBuggyCode.main(testing_7);
		*/
		
		/*
		String [] testing_8 = {"/home/ashley/reuse/Git_Scripts/Repos/elasticsearch", "normal","50", "elasticsearch_50_8", "1","1778","2667","8890","8890"};
		TrainBuggyCode.main(testing_8);
		String [] testing_9 = {"/home/ashley/reuse/Git_Scripts/Repos/elasticsearch", "normal","50", "elasticsearch_50_9", "1","889","1778","8890","8890"};
		TrainBuggyCode.main(testing_9);
		String [] testing_10 = {"/home/ashley/reuse/Git_Scripts/Repos/elasticsearch", "normal","50", "elasticsearch_50_10", "889","8890","0","0","8890"};
		TrainBuggyCode.main(testing_10);
		*/
	}

	public static void trainNetty() throws IOException, SerializationException{
		/*
		String [] testing_1 = {"/home/ashley/reuse/Git_Scripts/Repos/netty", "normal","50", "netty_50_1", "1","840","0","0","8740"};
		TrainBuggyCode.main(testing_1);
		String [] testing_2 = {"/home/ashley/reuse/Git_Scripts/Repos/netty", "normal","50", "netty_50_2", "1","6992","7867","8740","8740"};
		TrainBuggyCode.main(testing_2);
		String [] testing_3 = {"/home/ashley/reuse/Git_Scripts/Repos/netty", "normal","50", "netty_50_3", "1","6118","6993","8740","8740"};
		TrainBuggyCode.main(testing_3);
		String [] testing_4 = {"/home/ashley/reuse/Git_Scripts/Repos/netty", "normal","50", "netty_50_4", "1","5244","6118","8740","8740"};
		TrainBuggyCode.main(testing_4);
		*/
		
		/*	
		String [] testing_5 = {"/home/ashley/reuse/Git_Scripts/Repos/netty", "normal","50", "netty_50_5", "1","4370","5245","8740","8740"};
		TrainBuggyCode.main(testing_5);
		String [] testing_6 = {"/home/ashley/reuse/Git_Scripts/Repos/netty", "normal","50", "netty_50_6", "1","3496","4371","8740","8740"};
		TrainBuggyCode.main(testing_6);
		String [] testing_7 = {"/home/ashley/reuse/Git_Scripts/Repos/netty", "normal","50", "netty_50_7", "1","2622","3497","8740","8740"};
		TrainBuggyCode.main(testing_7);
		*/
		
		
		String [] testing_8 = {"/home/ashley/reuse/Git_Scripts/Repos/netty", "normal","50", "netty_50_8", "1","1748","2623","8740","8740"};
		TrainBuggyCode.main(testing_8);
		String [] testing_9 = {"/home/ashley/reuse/Git_Scripts/Repos/netty", "normal","50", "netty_50_9", "1","874","1749","8740","8740"};
		TrainBuggyCode.main(testing_9);
		String [] testing_10 = {"/home/ashley/reuse/Git_Scripts/Repos/netty", "normal","50", "netty_50_10", "840","8740","0","0","8740"};
		TrainBuggyCode.main(testing_10);
		
	}
	
	public static void trainModel() throws IOException, SerializationException{
		String [] changeDist = {"/Users/ashleychen/Desktop/REUSE/REUSE/Training_Dataset/b","/Users/ashleychen/Desktop/REUSE/REUSE/Training_Dataset/f","normal","2","foo"};
		TestTreeLM.main(changeDist);
	}
	
	public static void getEntropy() throws IOException, SerializationException{
		String [] changeDist = {"/Users/ashleychen/Desktop/elasticsearch/2/b/TransportNodesOperationAction.java","/Users/ashleychen/Desktop/elasticsearch/2/b/TransportNodesOperationAction.java",
				"/Users/ashleychen/Desktop/TestingDirectory_elasticsearch.ser"};
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