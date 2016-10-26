package ch.uzh.ifi.seal.changedistiller.util;

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
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation;

public final class CompilationUtils {

	private static final String TEST_DATA_BASE_DIR = "resources/testdata/";

	private CompilationUtils() {}

	public static JavaCompilation compileSource(String source) {
		ASTParser parser = ASTParser.newParser(AST.JLS4);  // handles JDK 1.0, 1.1, 1.2, 1.3, 1.4, 1.5, 1.6
		parser.setSource(source.toCharArray());
		// In order to parse 1.5 code, some compiler options need to be set to 1.5
		Map options = JavaCore.getOptions();
		JavaCore.setComplianceOptions(JavaCore.VERSION_1_6, options);
		parser.setCompilerOptions(options);
		CompilationUnit parsed = (CompilationUnit) parser.createAST(null); //FIXME: AC Where it's not parsing right

		JavaCompilation javaCompilation = new JavaCompilation(parsed, source);
		return javaCompilation;
	}


	/**
	 * Returns the generated {@link JavaCompilation} from the file identified by the given filename. This method assumes
	 * that the filename is relative to <code>{@value #TEST_DATA_BASE_DIR}</code>.
	 * 
	 * @param filename
	 *            of the file to compile (relative to {@value #TEST_DATA_BASE_DIR}).
	 * @return the compilation of the file
	 */
	public static JavaCompilation compileFile(String filename) {
		String test = getContentOfFile(TEST_DATA_BASE_DIR + filename);
		return CompilationUtils.compileSource(test);
	}

	private static String getContentOfFile(String filename) {
		char[] b = new char[1024];
		StringBuilder sb = new StringBuilder();
		try {
			FileReader reader = new FileReader(new File(filename));
			int n = reader.read(b);
			while (n > 0) {
				sb.append(b, 0, n);
				n = reader.read(b);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	public static List<Comment> extractComments(JavaCompilation sCompilationUnit) {
		return sCompilationUnit.getCompilationUnit().getCommentList();
	}

	public static MethodDeclaration findMethod(CompilationUnit cu, String methodName) {
		List<AbstractTypeDeclaration> types = cu.types();
		for (AbstractTypeDeclaration type : types) {
			if(type instanceof TypeDeclaration) {
				MethodDeclaration[] methods = ((TypeDeclaration) type).getMethods();
				for(MethodDeclaration method : methods) {
					if(method.getName().getIdentifier().equals(methodName)) {
						return method;
					}

				}

			}

		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static FieldDeclaration findField(CompilationUnit cu, String fieldName) {
		List<AbstractTypeDeclaration> types = cu.types();
		for (AbstractTypeDeclaration type : types) {
			if(type instanceof TypeDeclaration) {
				FieldDeclaration[] fields = ((TypeDeclaration) type).getFields();
				for (FieldDeclaration field : fields) { 
					List<VariableDeclarationFragment> fragments = field.fragments();
					for(VariableDeclarationFragment fragment : fragments) {
						if( fragment.getName().getIdentifier().equals(fieldName)) {
							return field;
						}
					}
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static TypeDeclaration findType(CompilationUnit cu, String typeName) {
		List<AbstractTypeDeclaration> types = cu.types();
		for (AbstractTypeDeclaration type : types) {
			if(type instanceof TypeDeclaration) {
				TypeDeclaration[] memberTypes = ((TypeDeclaration) type).getTypes();
				for (TypeDeclaration memberType : memberTypes) {
					if (memberType.getName().getIdentifier().equals(typeName)) {
						return memberType;
					}
				}
			}
		}
		return null;
	}

	public static File getFile(String filename) {
		return new File(TEST_DATA_BASE_DIR + filename);
	}

}
