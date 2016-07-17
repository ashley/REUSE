package ch.uzh.ifi.seal.changedistiller.ast.java;

import java.util.List;

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

import java.util.Stack;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.WildcardType;

import ch.uzh.ifi.seal.changedistiller.ast.ASTNodeTypeConverter;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.EntityType;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.SourceRange;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;

import com.google.inject.Inject;

/**
 * Visitor to generate an intermediate tree (general, rooted, labeled, valued tree) out of a field, class, or method
 * declaration.
 * 
 * @author Beat Fluri
 * 
 */
public class JavaDeclarationConverter extends ASTVisitor {

	private static final String COLON_SPACE = ": ";
	private boolean fEmptyJavaDoc;
	private Stack<Node> fNodeStack;
	private boolean fInMethodDeclaration;
	private String fSource;
	private ASTNodeTypeConverter fASTHelper;

	@Inject
	JavaDeclarationConverter(ASTNodeTypeConverter astHelper) {
		fASTHelper = astHelper;
		fNodeStack = new Stack<Node>();
	}

	/**
	 * Initializes the declaration converter.
	 * 
	 * @param root
	 *            of the resulting declaration tree
	 * @param scanner
	 *            of the source file that is traversed
	 */
	public void initialize(Node root, String source) {
		fSource = source; // not confident we need source
		fNodeStack.clear();
		fNodeStack.push(root);
	}
	
	// FIXME: translated argument visit to single variable decl; will it affect
	// more than we want it to?
	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(SingleVariableDeclaration node) {
		boolean isNotParam = getCurrentParent().getLabel() != JavaEntityType.PARAMETERS;
		pushValuedNode(node, node.getName().getIdentifier());
		if (isNotParam) {
			visitModifiers(node.modifiers());
		}
		node.getType().accept(this);
		return false;
	}


	@Override
	public boolean visit(Block block) {
		// skip block as it is not interesting
		return true;
	}

	@Override
	public void endVisit(Block block) {
		// do nothing pop is not needed (see visit(Block, BlockScope))
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean visit(FieldDeclaration fieldDeclaration) {
		if (fieldDeclaration.getJavadoc() != null) {
			fieldDeclaration.getJavadoc().accept(this);
		}
		visitModifiers(fieldDeclaration.modifiers());
		fieldDeclaration.getType().accept(this);
		List<VariableDeclarationFragment> fragments = fieldDeclaration.fragments();
	
		for(VariableDeclarationFragment frag : fragments) {
			visitExpression(frag.getInitializer());
		}
		return false;
	}

	@Override
	public void endVisit(FieldDeclaration fieldDeclaration) {
		pop();
	}

	private void visitExpression(Expression expression) {
		if (expression != null) {
			push(
					fASTHelper.convertNode(expression),
					expression.toString(),
					expression.getStartPosition(),
					getEndPosition(expression) - 1,
					expression);
			pop();
		}
	}

	private void visitModifiers(List<IExtendedModifier> modifiersList) {
		push(JavaEntityType.MODIFIERS, "", -1, -1, null); // FIXME: not confident about that null.  Possibly parent makes more sense?
		if(modifiersList != null && !modifiersList.isEmpty()) {
			Node modifiers = fNodeStack.peek();
			for(IExtendedModifier mod : modifiersList) {
			if(mod instanceof Modifier) // FIXME: check this, it's the part about which I'm least certain	
			{
				Modifier asMod = (Modifier) mod; // FIXME: previously this checked each type of modifier
												 // but I think I can just do a toString().  Check me!
				push(JavaEntityType.MODIFIER,
						asMod.getKeyword().toString(),
						asMod.getStartPosition(),
						getEndPosition(asMod) - 1,
						asMod); 		
				pop();
			}
			}
			setSourceRange(modifiers);
		}
		pop();
	}

	private void setSourceRange(Node modifiers) {
		SourceCodeEntity firstModifier = ((Node) modifiers.getFirstLeaf()).getEntity();
		SourceCodeEntity lastModifier = ((Node) modifiers.getLastLeaf()).getEntity();
		modifiers.getEntity().setStartPosition(firstModifier.getStartPosition());
		modifiers.getEntity().setEndPosition(lastModifier.getEndPosition());
	}

	@Override
	public boolean visit(Javadoc javadoc) {
		String string = null;
		string = getSource(javadoc);
		if (isJavadocEmpty(string)) {
			fEmptyJavaDoc = true;
		} else {
			pushValuedNode(javadoc, string);
		}
		return false;
	}

	@Override
	public void endVisit(Javadoc javadoc) {
		if (!fEmptyJavaDoc) {
			pop();
		}
		fEmptyJavaDoc = false;
	}

	private boolean isJavadocEmpty(String doc) {
		String[] splittedDoc = doc.split("/\\*+\\s*");
		StringBuilder tmp = new StringBuilder();
		for (String s : splittedDoc) {
			tmp.append(s);
		}

		String result = tmp.toString();

		try {
			result = result.split("\\s*\\*/")[0];
		} catch (ArrayIndexOutOfBoundsException e) {
			result = result.replace('/', ' ');
		}

		result = result.replace('*', ' ').trim();

		return result.equals("");
	}


	@Override
	public void endVisit(MethodDeclaration methodDeclaration) {
		pop();
	}

//	public boolean visit(Argument node) {
//	boolean isNotParam = getCurrentParent().getLabel() != JavaEntityType.PARAMETERS;
//	pushValuedNode(node, String.valueOf(node.name));
//	if (isNotParam) {
//		visitModifiers(node.modifiers);
//	}
//	node.type.traverse(this, scope);
//	return false;

	@SuppressWarnings({ "unchecked", "deprecation" })
	public boolean visit(MethodDeclaration methodDeclaration) {
		if (methodDeclaration.getJavadoc() != null) {
			methodDeclaration.getJavadoc().accept(this);
		}
		fInMethodDeclaration = true;
		visitModifiers(methodDeclaration.modifiers());
		visitReturnType(methodDeclaration);
		
		visitAbstractVariableDeclarations(JavaEntityType.TYPE_PARAMETERS, methodDeclaration.typeParameters());
		visitAbstractVariableDeclarations(JavaEntityType.PARAMETERS, methodDeclaration.parameters());
		fInMethodDeclaration = false;
		visitList(JavaEntityType.THROW, methodDeclaration.thrownExceptions());
		return false; // FIXME: I THINK
	}

	private void visitReturnType(MethodDeclaration methodDeclaration) {
		if (methodDeclaration.getReturnType2() != null) {
			methodDeclaration.getReturnType2().accept(this);
		}
	}

	@Override
	public boolean visit(ParameterizedType type) {
		int start = type.getStartPosition();
		int end = getEndPosition(type); 
		pushValuedNode(type, prefixWithNameOfParrentIfInMethodDeclaration() + getSource(start, end));
		fNodeStack.peek().getEntity().setEndPosition(end - 1);
		return false;
	}

	private String getSource(ASTNode node) {
		return getSource(node.getStartPosition(), getEndPosition(node));
	}

	private String getSource(int start, int end) {
		return fSource.substring(start, end + 1);
	}

	private String prefixWithNameOfParrentIfInMethodDeclaration() {
		return fInMethodDeclaration ? getCurrentParent().getValue() + COLON_SPACE : "";
	}

	@Override
	public void endVisit(ParameterizedType type) {
		pop();
	}

	@Override
	public boolean visit(QualifiedType type) {
		pushValuedNode(type, prefixWithNameOfParrentIfInMethodDeclaration() + type.toString());
		return false;
	}

	@Override
	public void endVisit(QualifiedType type) {
		pop();
	}
	
	@Override
	public boolean visit(PrimitiveType primitiveType) {
		pushValuedNode(primitiveType, prefixWithNameOfParrentIfInMethodDeclaration() + primitiveType.toString());
		return false;

	}

	@Override
	public void endVisit(PrimitiveType node) {
		pop();
	}
	
	@Override
	public boolean visit(SimpleType type) {
        pushValuedNode(type, prefixWithNameOfParrentIfInMethodDeclaration() + type.toString());
		return false;
	}

	@Override
	public void endVisit(SimpleType type) {
		pop();
	}

	@Override
	public boolean visit(ArrayType arrayType) {
		pushValuedNode(arrayType, prefixWithNameOfParrentIfInMethodDeclaration() + arrayType.toString());
		return false;
	}

	@Override
	public void endVisit(ArrayType arrayType) {
		pop();
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public boolean visit(TypeDeclaration typeDeclaration) {
		if (typeDeclaration.getJavadoc()  != null) {
			typeDeclaration.getJavadoc().accept(this);
		}
		visitModifiers(typeDeclaration.modifiers());
		visitAbstractVariableDeclarations(JavaEntityType.TYPE_PARAMETERS, typeDeclaration.typeParameters());
		if (typeDeclaration.getSuperclassType() != null) { 
			typeDeclaration.getSuperclassType().accept(this);
		}
		visitList(JavaEntityType.SUPER_INTERFACE_TYPES, typeDeclaration.superInterfaceTypes());
		return false;
	}

	@Override
	public void endVisit(TypeDeclaration typeDeclaration) {
		pop();
	}

	@Override
	public boolean visit(TypeParameter typeParameter) {
		push(
				fASTHelper.convertNode(typeParameter),
				getSource(typeParameter.getStartPosition(), getEndPosition(typeParameter) - 1),
				typeParameter.getStartPosition(),
				getEndPosition(typeParameter),
				typeParameter);
		return false;
	}

	@Override
	public void endVisit(TypeParameter typeParameter) {
		pop();
	}


	@Override
	public boolean visit(WildcardType type) {
		String bound = "";
		if(type.isUpperBound()) {
			bound = "extends";
		} else {
			bound = "super";
		}
		pushValuedNode(type, bound);
		return true;
	}

	@Override
	public void endVisit(WildcardType type) {
		pop();
	}

	
	private void visitAbstractVariableDeclarations(
			JavaEntityType parentLabel,
			List<VariableDeclaration> declarations) {
		int start = -1;
		int end = -1;
		push(parentLabel, "", start, end, null); // FIXME: not confident about that 
		if (declarations != null && !declarations.isEmpty()) {
			start = declarations.get(0).getStartPosition();
			end = getEndPosition(declarations.get(declarations.size()- 1));
			for(VariableDeclaration decl : declarations) {
				decl.accept(this);
			}
		}
		adjustSourceRangeOfCurrentNode(start, end);
		pop();
	}

	private void adjustSourceRangeOfCurrentNode(int start, int end) {
		fNodeStack.peek().getEntity().setStartPosition(start);
		fNodeStack.peek().getEntity().setEndPosition(end);
	}

	private void visitList(JavaEntityType parentLabel, List<ASTNode> nodes) {
		int start = -1;
		int end = -1;
		push(parentLabel, "", start, end, null); // FIXME: not confident about the null
		if (nodes != null && !nodes.isEmpty()) {
			start = nodes.get(0).getStartPosition();
			for(ASTNode node : nodes) {
				node.accept(this);
			}
			end = getLastChildOfCurrentNode().getEntity().getEndPosition() - 1;
		}
		adjustSourceRangeOfCurrentNode(start, end);
		pop();
	}


	private Node getLastChildOfCurrentNode() {
		return (Node) fNodeStack.peek().getLastChild();
	}

	private void pushValuedNode(ASTNode node, String value) {
		push(fASTHelper.convertNode(node), value, node.getStartPosition(), getEndPosition(node) - 1, node);
	}

	private void push(EntityType label, String value, int start, int end, ASTNode node) {
		Node n = new Node(label, value.trim());
		n.setEntity(new SourceCodeEntity(value.trim(), label, new SourceRange(start, end), node));
		getCurrentParent().add(n);
		fNodeStack.push(n);
	}

	private void pop() {
		fNodeStack.pop();
	}

	private Node getCurrentParent() {
		return fNodeStack.peek();
	}

	private int getEndPosition(ASTNode node) {
		return node.getStartPosition() + node.getLength();
	}

}
