package ch.uzh.ifi.seal.changedistiller.ast;

import java.io.File;

import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureNode;

public interface StructureNodeFactory {

	    /**
	     * Creates and returns an {@link ASTHelper} acting on the given {@link File}.
	     * 
	     * @param file
	     *            the AST helper acts on
	     * @param version
	     * 		of the language the AST helper uses to parse the file
	     * @return the AST helper acting on the file
	     */
	    @SuppressWarnings("rawtypes")
	    StructureNode create(ASTHelper left, ASTHelper right);
	    
}
