package ch.uzh.ifi.seal.changedistiller.structuredifferencing.java;

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

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import ch.uzh.ifi.seal.changedistiller.structuredifferencing.java.JavaStructureChangeNode.Type;

/**
 * Creates a tree of {@link JavaStructureNode}s.
 * 
 * @author Beat Fluri
 */
public class JavaStructureChangeTreeBuilder extends ASTVisitor {

	private Stack<JavaStructureChangeNode> fNodeStack;
	private Stack<char[]> fQualifiers;

	/**
	 * Creates a new Java structure tree builder.
	 * 
	 * @param root
	 *            of the structure tree
	 */
	public JavaStructureChangeTreeBuilder(JavaStructureChangeNode root) {
		fNodeStack = new Stack<JavaStructureChangeNode>();
		fNodeStack.push(root);
		fQualifiers = new Stack<char[]>();
	}

	@Override
	public boolean visit(CompilationUnit compilationUnitDeclaration) {
		if (compilationUnitDeclaration.getPackage() != null) {
			String qualifiedName = compilationUnitDeclaration.getPackage().getName().getFullyQualifiedName();
			String[] qualifiers = qualifiedName.split("\\."); // FIXME: not confident this is how regexes work
			for (String qualifier : qualifiers)  {
				fQualifiers.push(qualifier.toCharArray());
			}
		}
		return true;
	}

	/*      Old one:
	 *   StringBuffer name = new StringBuffer();
        name.append(fieldDeclaration.name);
        name.append(" : ");
        if (fieldDeclaration.type == null &&  fNodeStack.peek().getType().compareTo(JavaStructureNode.Type.ENUM) == 0) {
        	name.append(fNodeStack.peek().getName());
        } else {
        	fieldDeclaration.type.print(0, name);
        }
        push(Type.FIELD, name.toString(), fieldDeclaration);
        return false;
    }
	 */



	@Override
	public boolean visit(FieldDeclaration fieldDeclaration) {
		StringBuffer name = new StringBuffer();
		List<VariableDeclarationFragment> fragments = fieldDeclaration.fragments();
		for(VariableDeclarationFragment frag : fragments) {
			name.append(frag.getName().getIdentifier());
			name.append(" : ");
			if (fieldDeclaration.getType() == null &&  fNodeStack.peek().getType().compareTo(JavaStructureChangeNode.Type.ENUM) == 0) {
				name.append(fNodeStack.peek().getName());
			} else {
				name.append(fieldDeclaration.getType().toString()); // FIXME: I'm not sure about what was going on w/print before
				//	fieldDeclaration.getType().print(0, name);
			}
			push(Type.FIELD, name.toString(), fieldDeclaration);
		}
		return false;
	}

	@Override
	public void endVisit(FieldDeclaration fieldDeclaration) {
		pop();
	}

	@Override
	public boolean visit(MethodDeclaration methodDeclaration) {
		if(methodDeclaration.isConstructor())
			push(Type.CONSTRUCTOR, getMethodSignature(methodDeclaration), methodDeclaration);
		else
			push(Type.METHOD, getMethodSignature(methodDeclaration), methodDeclaration);
		return false;
	}

	@Override
	public void endVisit(MethodDeclaration methodDeclaration) {
		pop();
	}


	@Override
	public boolean visit(EnumDeclaration node) {
		push(Type.ENUM, node.getName().getIdentifier(), node);
		fQualifiers.push(node.getName().getIdentifier().toCharArray());
		return true;
	}

	@Override
	public void endVisit(EnumDeclaration node) {
		pop();
		fQualifiers.pop();
	}

	@Override
	public boolean visit(AnnotationTypeDeclaration node) {
		push(Type.ANNOTATION, node.getName().getIdentifier(), node); 
		fQualifiers.push(node.getName().getIdentifier().toCharArray());
		return true;
	}

	@Override
	public void endVisit(AnnotationTypeDeclaration node) {
		pop();
		fQualifiers.pop();
	}
	@Override
	public boolean visit(TypeDeclaration typeDeclaration) {
		Type type = null;

		if(typeDeclaration.isInterface()) {
			type = Type.INTERFACE;
		} else {
			type = Type.CLASS;
		}

		push(type, typeDeclaration.getName().getIdentifier(), typeDeclaration);
		fQualifiers.push(typeDeclaration.getName().getIdentifier().toCharArray());
		MethodDeclaration[] methods = typeDeclaration.getMethods();
		boolean hasConstructor = false;
		for(MethodDeclaration method : methods) {
			if(method.isConstructor()) {
				hasConstructor = true;
				break;
			}
		}
		if(!hasConstructor) {
			// construct implicit constructor?
			AST myAST = typeDeclaration.getAST();
			MethodDeclaration methodDeclaration = myAST.newMethodDeclaration();
			methodDeclaration.setConstructor(true);
			methodDeclaration.setName(myAST.newSimpleName(typeDeclaration.getName().getIdentifier()));
			Block newBlock = myAST.newBlock();
			newBlock.statements().add(myAST.newSuperConstructorInvocation());
			methodDeclaration.setBody(newBlock);
			push(Type.CONSTRUCTOR, getMethodSignature(methodDeclaration), methodDeclaration);
			pop();
		}
		return true;
	}

	@Override
	public void endVisit(TypeDeclaration typeDeclaration) {
		pop();
		fQualifiers.pop();
	}

	private String getMethodSignature(MethodDeclaration methodDeclaration) {
		StringBuffer signature = new StringBuffer();
		signature.append(methodDeclaration.getName().getIdentifier());
		signature.append('(');
		if (methodDeclaration.parameters() != null) {
			for (int i = 0; i < methodDeclaration.parameters().size(); i++) {
				if (i > 0) {
					signature.append(',');
				}
				SingleVariableDeclaration param =  (SingleVariableDeclaration) methodDeclaration.parameters().get(i);
				signature.append(param.getType().toString());
			}
		}
		signature.append(')');
		return signature.toString();
	}

	private void push(Type type, String name, ASTNode astNode) {
		JavaStructureChangeNode node = new JavaStructureChangeNode(getQualifier(), name, astNode);
		fNodeStack.peek().addChild(node);
		fNodeStack.push(node);
	}

	private String getQualifier() {
		if (!fQualifiers.isEmpty()) {
			StringBuilder qualifier = new StringBuilder();
			for (int i = 0; i < fQualifiers.size(); i++) {
				qualifier.append(fQualifiers.get(i));
				if (i < fQualifiers.size() - 1) {
					qualifier.append('.');
				}
			}
			return qualifier.toString();
		}
		return null;
	}

	private void pop() {
		fNodeStack.pop();
	}

}
