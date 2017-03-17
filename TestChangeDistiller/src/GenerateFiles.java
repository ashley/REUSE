import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class GenerateFiles {
	public static void main(String [] args){
		AST ast = AST.newAST(AST.JLS3);
		CompilationUnit cu = ast.newCompilationUnit();

		TypeDeclaration td = ast.newTypeDeclaration();
		td.setName(ast.newSimpleName("Foo"));
		//TypeParameter tp = ast.newTypeParameter();
		//tp.setName(ast.newSimpleName("X"));
		//td.typeParameters().add(tp);
		cu.types().add(td);

		MethodDeclaration md = ast.newMethodDeclaration();
		List modifiers = md.modifiers();
		md.setReturnType2(ast.newPrimitiveType(PrimitiveType.VOID));
		modifiers.add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
		modifiers.add(ast.newModifier(Modifier.ModifierKeyword.STATIC_KEYWORD));
		md.setName(ast.newSimpleName("Main"));
		
		SingleVariableDeclaration variableDeclaration = ast.newSingleVariableDeclaration();
		variableDeclaration.setType(ast.newArrayType(ast.newSimpleType(ast.newSimpleName("String"))));
		variableDeclaration.setName(ast.newSimpleName("args"));
		
		md.parameters().add(variableDeclaration);
		td.bodyDeclarations().add(md);

		Block block = ast.newBlock();
		md.setBody(block);
		
		VariableDeclarationFragment foo = ast.newVariableDeclarationFragment();
		foo.setName(ast.newSimpleName("foo"));
		ArrayInitializer arr = ast.newArrayInitializer();
		ArrayType arr1 = ast.newArrayType(ast.newSimpleType(ast.newSimpleName("String")));
		foo.setInitializer(arr);
		VariableDeclarationStatement bar = ast.newVariableDeclarationStatement(foo);
		block.statements().add(bar);
		
		/*
		MethodInvocation mi = ast.newMethodInvocation();
		mi.setName(ast.newSimpleName("x"));
		ExpressionStatement e = ast.newExpressionStatement(mi);
		block.statements().add(e);
		*/
		
		
		

		System.out.println(cu);
	}
}
