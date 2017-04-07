import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
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
import codemining.lm.tsg.tui.java.GenerateRandom;
import codemining.lm.tsg.tui.java.SampleTSG;
import codemining.util.serialization.ISerializationStrategy.SerializationException;

public class Test {
	
	public static void main(String[] args) throws IOException, SerializationException{
		/*
		String [] trainingFiles = {"beforeFiles","normal","10","afterFiles"};
		TestTreeLM.main(trainingFiles); //Testing modified SampleTSG
		System.out.println("----------------------------------------------------------------------------------------------------");
		*/
		
		parseTestFiles("java_samples_After","java_samples_After");
	}
	
	public static void parseTestFiles(String bugDir, String fixDir) throws SerializationException, IOException{
		ArrayList<String> results = new ArrayList<String>();
		File [] bFiles = new File(new File(bugDir).getAbsolutePath()).listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.toLowerCase().endsWith(".java");
		    }
		});
		
		File [] fFiles = new File(new File(fixDir).getAbsolutePath()).listFiles(new FilenameFilter() {
		    public boolean accept(File dir, String name) {
		        return name.toLowerCase().endsWith(".java");
		    }
		});

		for(int i=0;i<bFiles.length;i++){
			String [] entropyIng = {bFiles[i].toString(),fFiles[i].toString()};
			System.err.println(bFiles[i]);
			results.add(bFiles[i] + "\n" + TestTsgEntropy.main(entropyIng)); //Testing modified Entropy generator
		}
		for(String n: results){
			System.out.println(n + "\n");
		}
	}


}
