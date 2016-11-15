package org.eclipse.jdt.core.dom;

/*
 * #%L
 * ChangeDistiller
 * %%
 * Copyright (C) 2011 - 2013 Software Architecture and Evolution Lab, Department of Informatics, UZH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

//import org.eclipse.jdt.core.dom.ast.ASTNode;

/**
 * AST node representing any kind of comment.
 * 
 * @author Beat Fluri
 * 
 * modified by Ashley Chen
 */
public abstract class NewComment extends ASTNode {

    private String fComment;
    private NewCommentType fType;
	private ASTNode alternateRoot = null;
	private int sourceStart;
	private int sourceEnd;

    public NewComment(AST ast,NewCommentType type, int sourceStart, int sourceEnd, String comment) {
    	super(ast);
        fType = type;
        this.sourceStart = sourceStart;
        this.sourceEnd = sourceEnd;
        fComment = comment;
    }
    
    NewComment(AST ast) {
		super(ast);
	}
    
    public void setStart(int i){
    	sourceStart = i;
    }
    
    public void setEnd(int i){
    	sourceEnd = i;
    }

    public StringBuffer print(int indent, StringBuffer output) {
        printIndent(indent, output);
        output.append(fComment);
        return output;
    }

    public NewCommentType getType() {
        return fType;
    }

    public String getComment() {
        return fComment;
    }

    /**
     * Type of comments that java provides.
     * 
     * @author Beat Fluri
     */
    public enum NewCommentType {
        LINE_COMMENT,
        BLOCK_COMMENT,
        JAVA_DOC
    }

    /*public int getLength() {
        return sourceEnd() - sourceStart();
    }*/

    public void setComment(String comment) {
        fComment = comment;
    }

    public boolean isLineComment() {
        return getType() == NewCommentType.LINE_COMMENT;
    }

    public boolean isJavadocComment() {
        return getType() == NewCommentType.JAVA_DOC;
    }
    
    public final ASTNode getAlternateRoot() {
		return this.getAlternateRoot();
	}
    
    public final void setAlternateRoot(ASTNode root) {
		// alternate root is *not* considered a structural property
		// but we protect them nevertheless
		checkModifiable();
		this.alternateRoot  = root;
	}
    
	int memSize() {
		return BASE_NODE_SIZE + 1 * 4;
	}
	
	public static StringBuffer printIndent(int indent, StringBuffer output) {

		for (int i = indent; i > 0; i--) output.append("  "); //$NON-NLS-1$
		return output;
	}

}
