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
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureNode;
import codemining.ast.TreeNode;
import codemining.ast.java.AbstractJavaTreeExtractor;
import codemining.ast.java.JavaAstTreeExtractor;
import codemining.ast.java.JavaAstTreeExtractor.TreeNodeExtractor;
import codemining.lm.tsg.TSGNode;
import codemining.lm.tsg.tui.TsgEntropy;
import codemining.lm.tsg.tui.java.GenerateRandom;
import codemining.lm.tsg.tui.java.SampleTSG;
import codemining.util.serialization.ISerializationStrategy.SerializationException;

public class Test {
	
	public static void main(String[] args) throws IOException, SerializationException{
		
		//String [] trainingFiles = {"beforeFiles","normal","10","afterFiles"};
		//TestTreeLM.main(trainingFiles); //Testing modified SampleTSG
		System.out.println("----------------------------------------------------------------------------------------------------");
		
		String [] changeDist = {"/Users/ashleychen/Desktop/testing/1b/src/main/java/org/apache/commons/lang3","normal","10",
				"/Users/ashleychen/Desktop/testing/1b/src/main/java/org/apache/commons/lang3"};
		//TestTreeLM.main(changeDist);
		
	}
}