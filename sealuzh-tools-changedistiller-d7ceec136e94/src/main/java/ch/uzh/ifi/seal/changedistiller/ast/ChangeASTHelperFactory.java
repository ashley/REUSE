package ch.uzh.ifi.seal.changedistiller.ast;

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

import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureDiffNode;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureFinalDiffNode;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureNode;

/**
 * Factory interface to create {@link ASTHelper} from a {@link File} and a version number.
 * 
 * @author Beat Fluri
 */
public interface ChangeASTHelperFactory {

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
    ChangeASTHelper create(File file, String version);
    
    @SuppressWarnings("rawtypes")
	ChangeASTHelper create(StructureNode fLeftAST, StructureNode fRightAST);

	//@SuppressWarnings("rawtypes")
	//ChangeASTHelper create(StructureFinalDiffNode node);
}
