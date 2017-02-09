package ch.uzh.ifi.seal.changedistiller.distilling;

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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;

import ch.uzh.ifi.seal.changedistiller.ast.ASTHelper;
import ch.uzh.ifi.seal.changedistiller.ast.ASTHelperFactory;
import ch.uzh.ifi.seal.changedistiller.distilling.refactoring.RefactoringCandidateProcessor;
import ch.uzh.ifi.seal.changedistiller.model.entities.ClassHistory;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureDiffNode;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureDifferencer;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureNode;
import clegoues.genprog4java.treelm.GrammarUtils;

import com.google.inject.Inject;

/**
 * Distills {@link SourceCodeChange}s between two {@link File}.
 * 
 * @author Beat Fluri
 * @author Giacomo Ghezzi
 */
public class FileDistiller {

    private DistillerFactory fDistillerFactory;
    private ASTHelperFactory fASTHelperFactory;
    private RefactoringCandidateProcessor fRefactoringProcessor;

    private List<SourceCodeChange> fChanges;
    private ASTHelper<StructureNode> fLeftASTHelper;
    private ASTHelper<StructureNode> fRightASTHelper;
    private ClassHistory fClassHistory;
    private String fVersion;
    private List<Comment> leftComments;
    private List<Comment> rightComments;

    @Inject
    FileDistiller(
            DistillerFactory distillerFactory,
            ASTHelperFactory factory,
            RefactoringCandidateProcessor refactoringProcessor) {
        fDistillerFactory = distillerFactory;
        fASTHelperFactory = factory;
        fRefactoringProcessor = refactoringProcessor;
    }

    /**
     * Extracts classified {@link SourceCodeChange}s between two {@link File}s.
     * 
     * @param left
     *            file to extract changes
     * @param right
     *            file to extract changes
     * @return 
     */
    public StructureNode extractClassifiedSourceCodeChanges(File left, File right) {
    	StructureNode outcome = extractClassifiedSourceCodeChanges(left, "default", right, "default");
    	return outcome;
    }

    /**
     * Extracts classified {@link SourceCodeChange}s between two {@link File}s.
     * 
     * @param left
     *            file to extract changes
     * @param leftVersion
     * 			  version of the language in the left file
     * @param right
     *            file to extract changes
     * @param leftVersion
     * 			  version of the language in the right file
     */
    @SuppressWarnings("unchecked")
    public StructureNode extractClassifiedSourceCodeChanges(File left, String leftVersion, File right, String rightVersion) {

    	fLeftASTHelper = fASTHelperFactory.create(left, leftVersion);
        fRightASTHelper = fASTHelperFactory.create(right, rightVersion);
        leftComments = fLeftASTHelper.getComments();
        rightComments = fRightASTHelper.getComments();
        System.out.println("LEFT COMMENTS");
        for (Comment comment:leftComments){
        	System.out.print(comment);
        }
        System.out.println("RIGHT COMMENTS");
        for (Comment comment:leftComments){
        	System.out.print(comment);
        }
        StructureNode outcome = extractDifferences();
        return outcome;
    }

	private StructureNode extractDifferences() {
		StructureDifferencer structureDifferencer = new StructureDifferencer();
		StructureNode fLeftAST = fLeftASTHelper.createStructureTree();
		StructureNode fRightAST = fRightASTHelper.createStructureTree();
        structureDifferencer.extractDifferences(fLeftAST,fRightAST);
        StructureDiffNode structureDiff = structureDifferencer.getDifferences();
        if (structureDiff != null) {
        	fChanges = new LinkedList<SourceCodeChange>();
            // first node is (usually) the compilation unit
            processRootChildren(structureDiff);
            
        } else {
        	fChanges = Collections.emptyList();
        }
        return fLeftAST;
	}

    public void extractClassifiedSourceCodeChanges(File left, File right, String version) {
    	fVersion = version;
    	this.extractClassifiedSourceCodeChanges(left, right);
    }
    
    private void processRootChildren(StructureDiffNode diffNode) {
        for (StructureDiffNode child : diffNode.getChildren()) {
            if (child.isClassOrInterfaceDiffNode() && mayHaveChanges(child.getLeft(), child.getRight())) {
                if (fClassHistory == null) {
                	if (fVersion != null) {
                		fClassHistory = new ClassHistory(fRightASTHelper.createStructureEntityVersion(child.getRight(), fVersion));
                	} else {
                		fClassHistory = new ClassHistory(fRightASTHelper.createStructureEntityVersion(child.getRight()));
                	}
                }
                processClassDiffNode(child);
            }
        }
    }

    private void processClassDiffNode(StructureDiffNode child) {
    	ClassDistiller classDistiller;
    	if (fVersion != null) {
        classDistiller =
                new ClassDistiller(
                        child,
                        fClassHistory,
                        fLeftASTHelper,
                        fRightASTHelper,
                        fRefactoringProcessor,
                        fDistillerFactory,
                        fVersion);
    	} else {
    		classDistiller =
                new ClassDistiller(
                        child,
                        fClassHistory,
                        fLeftASTHelper,
                        fRightASTHelper,
                        fRefactoringProcessor,
                        fDistillerFactory);
    	}
        classDistiller.extractChanges();
        fChanges.addAll(classDistiller.getSourceCodeChanges());
    }

    private boolean mayHaveChanges(StructureNode left, StructureNode right) {
        return (left != null) && (right != null);
    }

    public List<SourceCodeChange> getSourceCodeChanges() {
        return fChanges;
    }

    public ClassHistory getClassHistory() {
        return fClassHistory;
    }
    
    public boolean getCommentChanges(){
    	boolean match = true;
        for (Comment lComment: leftComments){
        	boolean commentMatch = false;
        	for (Comment rComment: rightComments){
        	}
        		if (lComment.isLineComment()){ //.getComment().equals(rComment.getComment())){
        			commentMatch = true;
        		}
        	if (commentMatch == false){
        		match = false;
        	}
        }
    	return match;
    }

}
