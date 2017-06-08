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
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;

import ch.uzh.ifi.seal.changedistiller.ast.ASTHelper;
import ch.uzh.ifi.seal.changedistiller.ast.ASTHelperFactory;
import ch.uzh.ifi.seal.changedistiller.ast.ChangeASTHelper;
import ch.uzh.ifi.seal.changedistiller.ast.ChangeASTHelperFactory;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilationUtils;
import ch.uzh.ifi.seal.changedistiller.distilling.refactoring.RefactoringCandidateProcessor;
import ch.uzh.ifi.seal.changedistiller.model.entities.ClassHistory;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureDiffNode;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureDifferencer;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureFinalDiffNode;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureNode;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.java.JavaStructureChangeNode;

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
    private ChangeASTHelperFactory fChangeASTHelperFactory;
    private RefactoringCandidateProcessor fRefactoringProcessor;
    private List<SourceCodeChange> fChanges;
    private ASTHelper<StructureNode> fLeftASTHelper;
    private ChangeASTHelper diffASTHelper;
    private ASTHelper<StructureNode> fRightASTHelper;
    private ClassHistory fClassHistory;
    private String fVersion;
    private List<Comment> leftComments;
    private List<Comment> rightComments;

    @Inject
    FileDistiller(
            DistillerFactory distillerFactory,
            ASTHelperFactory factory,
            RefactoringCandidateProcessor refactoringProcessor,
            ChangeASTHelperFactory changeFactory) {
        fDistillerFactory = distillerFactory;
        fASTHelperFactory = factory;
        fChangeASTHelperFactory = changeFactory;
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
    
    public StructureFinalDiffNode extractChangeNode(File left, File right) {
    	StructureFinalDiffNode outcome = extractChangeFromSource(left, "default", right, "default");
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
        StructureNode outcome = extractDifferences();
        return outcome;
    }
    
    @SuppressWarnings("unchecked")
    public StructureFinalDiffNode extractChangeFromSource(File left, String leftVersion, File right, String rightVersion) {

    	fLeftASTHelper = fASTHelperFactory.create(left, leftVersion);
    	JavaCompilation leftAPIVersion = null; //JavaCompilationUtils.compile(getContentOfFile(leftVersion), left.getName());
    	JavaCompilation rightAPIVersion = null; //JavaCompilationUtils.compile(getContentOfFile(rightVersion), right.getName());
    	fRightASTHelper = fASTHelperFactory.create(right, rightVersion);
    	StructureNode fLeftAST = fLeftASTHelper.createStructureTree();
    	StructureNode fRightAST = fRightASTHelper.createStructureTree();
    	StructureNode [] arr = {fLeftAST,fRightAST};
    	diffASTHelper = fChangeASTHelperFactory.create(arr);
    	JavaStructureChangeNode changeNode = diffASTHelper.createStructureTree();
        leftComments = fLeftASTHelper.getComments();
        rightComments = fRightASTHelper.getComments();
        StructureFinalDiffNode outcome = extractChangeDifferences(leftAPIVersion,rightAPIVersion);
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
	
	private StructureFinalDiffNode extractChangeDifferences(JavaCompilation leftAPIVersion, JavaCompilation rightAPIVersion) {
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
        //StructureFinalDiffNode changeAST = diffASTHelper.createStructureChangeTree(); //Attempted to used customized ASTHelper
        StructureFinalDiffNode changeAST = integrateStructureDiff(fLeftAST, structureDiff,leftAPIVersion,rightAPIVersion);
        return changeAST;
	}
	

    private StructureFinalDiffNode integrateStructureDiff(StructureNode fLeftAST, StructureDiffNode diff, JavaCompilation leftAPIVersion, JavaCompilation rightAPIVersion) {
    	List<StructureDiffNode> foo = diff.getChildren();
    	StructureFinalDiffNode changeAST = new JavaStructureChangeNode("String1", "String2", null);
    	changeAST.setfChildren(foo);	
    	changeAST.setAPIVersion(leftAPIVersion,rightAPIVersion);
    	changeAST.setASTNode(fLeftAST.getASTNode());
		return changeAST;
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
