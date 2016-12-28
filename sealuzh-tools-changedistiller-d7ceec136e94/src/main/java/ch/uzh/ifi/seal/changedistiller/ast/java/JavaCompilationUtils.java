package ch.uzh.ifi.seal.changedistiller.ast.java;

import java.io.BufferedReader;

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
import java.util.Map;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import ch.uzh.ifi.seal.changedistiller.ast.FileUtils;
import ch.uzh.ifi.seal.changedistiller.ast.InvalidSyntaxException;

/**
 * Utility class for Java compilation.
 * 
 * @author Beat Fluri
 * @author linzhp
 * @author wuersch
 * 
 */
public final class JavaCompilationUtils {

    private JavaCompilationUtils() {}

    /**
     * Returns the compiled source as a {@link JavaCompilation}.
     * 
     * @param Java source
     *            to compile
     * @return the compilation of the Java source
     * @throws InvalidSyntaxException if the file has syntax errors.
     */
    public static JavaCompilation compile(String source, String fileName) {
        
        ASTParser parser = ASTParser.newParser(AST.JLS4);  // handles JDK 1.0, 1.1, 1.2, 1.3, 1.4, 1.5, 1.6
        parser.setSource(source.toCharArray());
        // In order to parse 1.5 code, some compiler options need to be set to 1.5
        Map options = JavaCore.getOptions();
        JavaCore.setComplianceOptions(JavaCore.VERSION_1_6, options);
        parser.setCompilerOptions(options);
		
        JavaCompilation javaCompilation = new JavaCompilation((CompilationUnit) parser.createAST(null), source);

		
        return javaCompilation;
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
    /**
     * Returns the compiled file as a {@link JavaCompilation}.
     * 
     * @param file
     *            to compile
     * @param version
     * 			  of Java used in the file
     * @return the compilation of the file
     * @throws InvalidSyntaxException if the file has syntax errors.
     */
    public static JavaCompilation compile(File file, long version) { // FIXME: do I care about ignoring the version?
    	String source = getContentOfFile(file.getAbsolutePath()); 
		return JavaCompilationUtils.compile(source, file.getName());
    }

}
