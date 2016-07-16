package ch.uzh.ifi.seal.changedistiller.ast.java;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class CollectCUVisitor extends ASTVisitor {

	private CompilationUnit cu;

	public CompilationUnit getCompilationUnit() {
		return cu;
	}

	public void setCompilationUnit(CompilationUnit cu) {
		this.cu = cu;
	}
	
}
