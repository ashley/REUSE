package ch.uzh.ifi.seal.changedistiller.structuredifferencing.java;

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

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;

import com.google.inject.Inject;

import ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureDiffNode;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureFinalDiffNode;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.java.JavaStructureChangeNode.Type;

/**
 * Node for Java structure differencing.
 * 
 * @author Beat Fluri
 */
public class JavaStructureChangeNode implements StructureFinalDiffNode {

    private Type fType;
    private String fName;
    private String fQualifier;
    private ASTNode fASTNode;
    private JavaCompilation fAPIVersion;
    private List<JavaStructureChangeNode> fChildren;
    private List<StructureDiffNode> newfChildren;

    /**
     * Creates a new Java structure node
     * 
     * @param cu
     *            of the node
     * @param qualifier
     *            of the node
     * @param name
     *            of the node
     * @param astNode
     *            representing the structure node
     */
    public JavaStructureChangeNode(String qualifier, String name, ASTNode astNode) {
        fType = Type.CU;
        fQualifier = qualifier;
        fName = name;
        fASTNode = astNode;
        fChildren = new LinkedList<JavaStructureChangeNode>();
    }

    /**
     * Adds a new {@link JavaStructureChangeNode} child.
     * 
     * @param node
     *            to add as child
     */
    public void addChild(JavaStructureChangeNode node) {
        fChildren.add(node);
    }

    public List<JavaStructureChangeNode> getChildren() {
        return fChildren;
    }

    @Override
    public String toString() {
        return fType.name() + ": " + fName;
    }

    /**
     * Java structure node types.
     * 
     * @author Beat Fluri
     */
    public enum Type {
        CU,
        FIELD,
        CONSTRUCTOR,
        METHOD,
        INTERFACE,
        CLASS,
        ANNOTATION,
        ENUM;
  

    }

    public Type getType() {
        return fType;
    }

    public String getName() {
        return fName;
    }

    /**
     * Returns the fully qualified name of this node if the node has a qualifier. Otherwise, {@link #getName()} is
     * returned.
     * 
     * @return the fully qualified name of this node, if the node has a qualifier; name otherwise.
     */
	public String getFullyQualifiedName() {
        if (fQualifier != null) {
            return fQualifier + "." + fName;
        }
        return getName();
    }

    public String getContent() {
        return fASTNode.toString();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if ((obj != null) && (getClass() == obj.getClass())) {
            JavaStructureChangeNode other = (JavaStructureChangeNode) obj;
            return (fType == other.getType()) && fName.equals(other.getName());
        }
        return super.equals(obj);
    }

    public ASTNode getASTNode() {
    	return fASTNode;
    }
    
    public void setASTNode(ASTNode n){
    	fASTNode = n;
    }

    public boolean isClassOrInterface() {
        return (fType == Type.CLASS) || (fType == Type.INTERFACE);
    }

    public boolean isMethodOrConstructor() {
        return (fType == Type.METHOD) || (fType == Type.CONSTRUCTOR);
    }

    public boolean isField() {
        return fType == Type.FIELD;
    }

    public boolean isOfSameTypeAs(StructureFinalDiffNode other) {
        if (other.getClass() == getClass()) {
            return fType == ((JavaStructureChangeNode) other).fType;
        }
        return false;
    }

	@Override
	public void setfChildren(List<StructureDiffNode> list) {
		newfChildren = list;
		
	}
	
	public List<StructureDiffNode> getNewfChildren(){
		return newfChildren;
	}

	public void setAPIVersion(JavaCompilation leftAPIVersion, JavaCompilation rightAPIVersion) {
		fAPIVersion = leftAPIVersion;
		
	}


}
