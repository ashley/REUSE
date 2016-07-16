package ch.uzh.ifi.seal.changedistiller.ast.java;

import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class JavaParser {

	/** visits all nodes while file is parsed.  Collects semantic info */
	private CollectCUVisitor visitor;

	/** compilation unit from parsed file; to be returned/collected by the parser client */
	private CompilationUnit compilationUnit;


	public JavaParser()
	{

	}


	public CompilationUnit getCompilationUnit()
	{
		return this.compilationUnit;
	}

	public void parse(String file, String[] libs, String sourceVersion)
	{
		int parserVersion = AST.JLS4;

		ASTParser parser = ASTParser.newParser(parserVersion);
		parser.setEnvironment(libs, new String[] {}, null, true);

		Map options = JavaCore.getOptions();
		JavaCore.setComplianceOptions(sourceVersion, options);
		parser.setCompilerOptions(options);

		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		// note that this bindings recovery and resolution are important for
		// checking information about types, down the line.
		parser.setResolveBindings(true);
		parser.setBindingsRecovery(true);
		parser.setStatementsRecovery(true);
		ParserRequestor req = new ParserRequestor(visitor);

		parser.createASTs(new String[]{file}, null, new String[0], req, null);

		this.compilationUnit = visitor.getCompilationUnit();
	}

}

