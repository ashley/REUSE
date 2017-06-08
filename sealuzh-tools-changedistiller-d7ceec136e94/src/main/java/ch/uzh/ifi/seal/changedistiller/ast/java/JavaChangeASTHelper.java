package ch.uzh.ifi.seal.changedistiller.ast.java;

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

import java.io.File;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import ch.uzh.ifi.seal.changedistiller.ast.ASTHelper;
import ch.uzh.ifi.seal.changedistiller.ast.ChangeASTHelper;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.ChangeModifier;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.EntityType;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.SourceRange;
import ch.uzh.ifi.seal.changedistiller.model.entities.AttributeHistory;
import ch.uzh.ifi.seal.changedistiller.model.entities.ClassHistory;
import ch.uzh.ifi.seal.changedistiller.model.entities.MethodHistory;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.model.entities.StructureEntityVersion;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureDiffNode;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureDifferencer;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureNode;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.java.JavaStructureChangeNode;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.java.JavaStructureChangeNode.Type;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.java.JavaStructureChangeTreeBuilder;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.java.JavaStructureNode;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.java.JavaStructureTreeBuilder;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;


public class JavaChangeASTHelper implements ChangeASTHelper<JavaStructureChangeNode> {

    private JavaDeclarationConverter fDeclarationConverter;
    private JavaMethodBodyConverter fBodyConverter;
    private JavaASTNodeTypeConverter fASTHelper;
    private JavaCompilation fCompilation;
    private List<Comment> fComments;
    private StructureNode fLeftAST;
    private StructureNode fRightAST;
    private StructureDiffNode fDiff;
    
    @Inject 
    JavaChangeASTHelper(@Assisted StructureNode[] fAST,
            JavaASTNodeTypeConverter astHelper,
            JavaDeclarationConverter declarationConverter,
            JavaMethodBodyConverter bodyConverter) {
    	fASTHelper = astHelper;
        fDeclarationConverter = declarationConverter;
        fBodyConverter = bodyConverter;
        fLeftAST = fAST[0];
        fRightAST = fAST[1];
        getDifferences();
    }

	private void getDifferences(){
        StructureDifferencer structureDifferencer = new StructureDifferencer();
        structureDifferencer.extractDifferences(fLeftAST,fRightAST);
        fDiff = structureDifferencer.getDifferences();
    }
    
    @Override
    public Node createDeclarationChangeTree(JavaStructureChangeNode node) {
        ASTNode astNode = node.getASTNode();
        Node root = createRootNode(node, astNode);
        return createDeclarationTree(astNode, root);
    }

    private Node createDeclarationTree(ASTNode astNode, Node root) {
        fDeclarationConverter.initialize(root, fCompilation.getSource());
        if (astNode instanceof TypeDeclaration) {
            ((TypeDeclaration) astNode).accept(fDeclarationConverter);
        } else if (astNode instanceof MethodDeclaration) {
            ((MethodDeclaration) astNode).accept(fDeclarationConverter);
        } else if (astNode instanceof FieldDeclaration) {
            ((FieldDeclaration) astNode).accept(fDeclarationConverter);
        }
        return root;
    }

    @Override
    public Node createDeclarationChangeTree(JavaStructureChangeNode node, String qualifiedName) {
        ASTNode astNode = node.getASTNode();
        Node root = createRootNode(node, astNode);
        root.setValue(qualifiedName);
        return createDeclarationTree(astNode, root);
    }

    private Node createRootNode(JavaStructureChangeNode node, ASTNode astNode) {
        Node root = new Node(fASTHelper.convertNode(astNode), node.getFullyQualifiedName());
        root.setEntity(createChangeEntity(node));
        return root;
    }

    @Override
    public Node createMethodBodyChangeTree(JavaStructureChangeNode node) {
        ASTNode astNode = node.getASTNode();
        if (astNode instanceof MethodDeclaration) {
            Node root = createRootNode(node, astNode);
            fBodyConverter.initialize(root, astNode, fComments, fCompilation.getSource());
            ((MethodDeclaration) astNode).accept(fBodyConverter);
            return root;
        }
        return null;
    }

    @Override
    public JavaStructureChangeNode createStructureChangeTree() {
        CompilationUnit cu = fCompilation.getCompilationUnit();
        JavaStructureChangeNode node = new JavaStructureChangeNode(null, null, cu);
        cu.accept(new JavaStructureChangeTreeBuilder(node));
        return node;
    }
    
    public EntityType convertChangeType(JavaStructureChangeNode node) {
        return fASTHelper.convertNode(node.getASTNode());
    }

    public SourceCodeEntity createChangeEntity(JavaStructureChangeNode node) {
        return new SourceCodeEntity(
                node.getFullyQualifiedName(),
                convertType(node),
                extractModifier(node.getASTNode()),
                createSourceRange(node.getASTNode()),
                node.getASTNode());
    }

    private int getEndPosition(ASTNode node) {
    	return node.getStartPosition() + node.getLength();
    }
    private SourceRange createSourceRange(ASTNode astNode) {
        return new SourceRange(astNode.getStartPosition(), getEndPosition(astNode)); 
    }

    @Override
    public StructureEntityVersion createStructureEntityChangeVersion(JavaStructureChangeNode node, String versionNum) {
        return new StructureEntityVersion(
                convertType(node),
                node.getFullyQualifiedName(),
                extractModifier(node.getASTNode()),
                versionNum);
    }
    
    @Override
    public StructureEntityVersion createStructureEntityChangeVersion(JavaStructureChangeNode node) {
        return new StructureEntityVersion(
                convertType(node),
                node.getFullyQualifiedName(),
                extractModifier(node.getASTNode()));
    }

    private int extractModifier(ASTNode node) {
        int ecjModifer = -1; // important FIXME: I think I did this conversion properly, but check
        if (node instanceof MethodDeclaration) {
            ecjModifer = ((MethodDeclaration) node).getModifiers(); 
        } else if (node instanceof FieldDeclaration) {
            ecjModifer = ((FieldDeclaration) node).getModifiers();
        } else if (node instanceof TypeDeclaration) {
            ecjModifer = ((TypeDeclaration) node).getModifiers();
        }
        if (ecjModifer > -1) {
            return convertECJModifier(ecjModifer);
        }
        return 0;
    }

    private int convertECJModifier(int ecjModifer) {
        int modifier = 0x0;
        if (isAbstract(ecjModifer)) {
            modifier |= ChangeModifier.ABSTRACT;
        }
        if (isFinal(ecjModifer)) {
            modifier |= ChangeModifier.FINAL;
        }
        if (isNative(ecjModifer)) {
        	modifier |= ChangeModifier.NATIVE;
        }
        if (isStatic(ecjModifer)) {
        	modifier |= ChangeModifier.STATIC;
        }
        if (isStrictFP(ecjModifer)) {
        	modifier |= ChangeModifier.STRICTFP;
        }
        if (isSynchronized(ecjModifer)) {
        	modifier |= ChangeModifier.SYNCHRONIZED;
        }
        if (isTransient(ecjModifer)) {
        	modifier |= ChangeModifier.TRANSIENT;
        }
        if (isVolatile(ecjModifer)) {
        	modifier |= ChangeModifier.VOLATILE;
        }
        if (isPublic(ecjModifer)) {
            modifier |= ChangeModifier.PUBLIC;
        }
        if (isProtected(ecjModifer)) {
            modifier |= ChangeModifier.PROTECTED;
        }
        if (isPrivate(ecjModifer)) {
            modifier |= ChangeModifier.PRIVATE;
        }
        return modifier;
    }
    
    private boolean isNative(int ecjModifier) {
    	return Modifier.isNative(ecjModifier); 
    }
    
    private boolean isStatic(int ecjModifier) {
    	return Modifier.isStatic(ecjModifier);
    }
    
    private boolean isStrictFP(int ecjModifier) {
    	return Modifier.isStrictfp(ecjModifier); 
    }

    private boolean isSynchronized(int ecjModifier) {
    	return Modifier.isSynchronized(ecjModifier);
    }

    private boolean isTransient(int ecjModifier) {
    	return Modifier.isTransient(ecjModifier);
    }

    private boolean isVolatile(int ecjModifier) {
    	return Modifier.isVolatile(ecjModifier); 
    }
    

    private boolean isAbstract(int ecjModifier) {
        return Modifier.isAbstract(ecjModifier);
    }
    
    private boolean isPrivate(int ecjModifier) {
        return Modifier.isPrivate(ecjModifier); 
    }

    private boolean isProtected(int ecjModifier) {
        return Modifier.isProtected(ecjModifier);
    }

    private boolean isPublic(int ecjModifier) {
        return Modifier.isPublic(ecjModifier); 
    }

    private boolean isFinal(int ecjModifier) {
        return Modifier.isFinal(ecjModifier);
    }

    @Override
    public StructureEntityVersion createMethodInClassChangeHistory(ClassHistory classHistory, JavaStructureChangeNode node, String versionNum) {
        MethodHistory mh;
        StructureEntityVersion method = createStructureEntityChangeVersion(node, versionNum);
        if (classHistory.getMethodHistories().containsKey(method.getUniqueName())) {
            mh = classHistory.getMethodHistories().get(method.getUniqueName());
            mh.addVersion(method);
        } else {
            mh = new MethodHistory(method);
            classHistory.getMethodHistories().put(method.getUniqueName(), mh);
        }
        return method;
    }

    @Override
    public StructureEntityVersion createMethodInClassChangeHistory(ClassHistory classHistory, JavaStructureChangeNode node) {
        MethodHistory mh;
        StructureEntityVersion method = createStructureEntityChangeVersion(node);
        if (classHistory.getMethodHistories().containsKey(method.getUniqueName())) {
            mh = classHistory.getMethodHistories().get(method.getUniqueName());
            mh.addVersion(method);
        } else {
            mh = new MethodHistory(method);
            classHistory.getMethodHistories().put(method.getUniqueName(), mh);
        }
        return method;
    }
    
    @Override
    public StructureEntityVersion createFieldInClassChangeHistory(ClassHistory classHistory, JavaStructureChangeNode node, String versionNum) {
        AttributeHistory ah = null;
        StructureEntityVersion attribute = createStructureEntityChangeVersion(node, versionNum);
        if (classHistory.getAttributeHistories().containsKey(attribute.getUniqueName())) {
            ah = classHistory.getAttributeHistories().get(attribute.getUniqueName());
            ah.addVersion(attribute);
        } else {
            ah = new AttributeHistory(attribute);
            classHistory.getAttributeHistories().put(attribute.getUniqueName(), ah);
        }
        return attribute;

    }
    
    @Override
    public StructureEntityVersion createFieldInClassChangeHistory(ClassHistory classHistory, JavaStructureChangeNode node) {
        AttributeHistory ah = null;
        StructureEntityVersion attribute = createStructureEntityChangeVersion(node);
        if (classHistory.getAttributeHistories().containsKey(attribute.getUniqueName())) {
            ah = classHistory.getAttributeHistories().get(attribute.getUniqueName());
            ah.addVersion(attribute);
        } else {
            ah = new AttributeHistory(attribute);
            classHistory.getAttributeHistories().put(attribute.getUniqueName(), ah);
        }
        return attribute;

    }

    @Override
    public StructureEntityVersion createInnerClassInClassChangeHistory(ClassHistory classHistory, JavaStructureChangeNode node, String versionNum) {
        ClassHistory ch = null;
        StructureEntityVersion clazz = createStructureEntityChangeVersion(node, versionNum);
        if (classHistory.getInnerClassHistories().containsKey(clazz.getUniqueName())) {
            ch = classHistory.getInnerClassHistories().get(clazz.getUniqueName());
            ch.addVersion(clazz);
        } else {
            ch = new ClassHistory(clazz);
            classHistory.getInnerClassHistories().put(clazz.getUniqueName(), ch);
        }
        return clazz;

    }
    
    @Override
    public StructureEntityVersion createInnerClassInClassChangeHistory(ClassHistory classHistory, JavaStructureChangeNode node) {
        ClassHistory ch = null;
        StructureEntityVersion clazz = createStructureEntityChangeVersion(node);
        if (classHistory.getInnerClassHistories().containsKey(clazz.getUniqueName())) {
            ch = classHistory.getInnerClassHistories().get(clazz.getUniqueName());
            ch.addVersion(clazz);
        } else {
            ch = new ClassHistory(clazz);
            classHistory.getInnerClassHistories().put(clazz.getUniqueName(), ch);
        }
        return clazz;

    }

	@Override
	public EntityType convertType(JavaStructureChangeNode node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CompilationUnit returnCU() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JavaStructureChangeNode createMyTree() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Comment> getComments() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JavaStructureChangeNode createStructureTree() {
		ASTNode cu = fRightAST.getASTNode();
		JavaStructureChangeNode node = new JavaStructureChangeNode(null, null, cu);
		cu.accept(new JavaStructureChangeTreeBuilder(node));
	    return node;
	}


}
