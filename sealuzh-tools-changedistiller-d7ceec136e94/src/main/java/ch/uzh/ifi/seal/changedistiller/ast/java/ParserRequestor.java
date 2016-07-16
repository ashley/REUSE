package ch.uzh.ifi.seal.changedistiller.ast.java;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;

public class ParserRequestor extends FileASTRequestor{

	private CollectCUVisitor visitor;
	
	public ParserRequestor(CollectCUVisitor v)
	{
		this.visitor = v;
	}

	@Override
	public void acceptAST(String sourceFilePath, CompilationUnit ast)
	{
		this.visitor.setCompilationUnit(ast);
		ast.accept(this.visitor);
		super.acceptAST(sourceFilePath, ast);
	}
}
