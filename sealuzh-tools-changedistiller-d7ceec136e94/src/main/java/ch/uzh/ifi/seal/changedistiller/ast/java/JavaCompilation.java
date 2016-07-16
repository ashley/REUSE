package ch.uzh.ifi.seal.changedistiller.ast.java;

import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.compiler.parser.Scanner; // FIXME: I really need to fix this.

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

/**
 * Container for {@link CompilationUnitDeclaration} and the corresponding {@link Scanner}.
 * 
 * @author Beat Fluri
 */
public class JavaCompilation {

    private CompilationUnit fCompilationUnit;
    private Scanner fScanner;

    /**
     * Create a new Java compilation
     * 
     * @param compilationUnit
     *            of the compilation
     * @param scanner
     *            that produced the compilation
     */
    public JavaCompilation(CompilationUnit compilationUnit, Scanner scanner) {
        fCompilationUnit = compilationUnit;
        fScanner = scanner;
    }

    public CompilationUnit getCompilationUnit() {
        return fCompilationUnit;
    }

    public String getSource() {
        return String.valueOf(fScanner.source);
    }

    public Scanner getScanner() {
        return fScanner;
    }
}
