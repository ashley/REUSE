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

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.internal.compiler.parser.Scanner;

import ch.uzh.ifi.seal.changedistiller.ast.ASTNodeTypeConverter;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.EntityType;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.SourceRange;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;

import com.google.inject.Inject;

/**
 * Visitor to generate an intermediate tree (general, rooted, labeled, valued tree) out of a method body.
 * 
 * @author Beat Fluri, Giacomo Ghezzi
 * 
 */
public class JavaMethodBodyConverter extends ASTVisitor {

    private static final String COLON = ":";
    private List<Comment> fComments;
    private Stack<Node> fNodeStack;
    private String fSource;
    private Scanner fScanner; // FIXME: I really need to fix this.

    private ASTNode fLastVisitedNode;
    private Node fLastAddedNode;

    private Stack<ASTNode[]> fLastAssociationCandidate;
    private Stack<Node[]> fLastCommentNodeTuples;
    private ASTNodeTypeConverter fASTHelper;

    @Inject
    JavaMethodBodyConverter(ASTNodeTypeConverter astHelper) {
        fNodeStack = new Stack<Node>();
        fLastAssociationCandidate = new Stack<ASTNode[]>();
        fLastCommentNodeTuples = new Stack<Node[]>();
        fASTHelper = astHelper;
    }

    /**
     * Initializes the method body converter.
     * 
     * @param root
     *            the root node of the tree to generate
     * @param methodRoot
     *            the method AST root node, necessary for comment attachment
     * @param comments
     *            to associate
     * @param scanner
     *            the scanner with which the AST was created
     */
    public void initialize(Node root, ASTNode methodRoot, List<Comment> comments, Scanner scanner) {
        fNodeStack.clear();
        fLastAssociationCandidate.clear();
        fLastCommentNodeTuples.clear();
        fLastVisitedNode = methodRoot;
        fLastAddedNode = root;
        fNodeStack.push(root);
        fComments = comments;
        fScanner = scanner;
        fSource = String.valueOf(scanner.getSource());
    }

    /**
     * Prepares node for comment association.
     * 
     * @param node
     *            the node to prepare for comment association
     */
    public void preVisit(ASTNode node) {
        if (!hasComments() || isUnusableNode(node)) {
            return;
        }
        int i = 0;
        while (i < fComments.size()) {
            Comment comment = fComments.get(i);
            if (previousNodeExistsAndIsNotTheFirstNode() && isCommentBetweenCurrentNodeAndLastNode(comment, node)) {
                ASTNode[] candidate = new ASTNode[]{fLastVisitedNode, comment, node};
                fLastAssociationCandidate.push(candidate);
                Node[] nodeTuple = new Node[2];
                nodeTuple[0] = fLastAddedNode; // preceeding node
                insertCommentIntoTree(comment);
                nodeTuple[1] = fLastAddedNode; // comment
                fLastCommentNodeTuples.push(nodeTuple);
                fComments.remove(i--);
            }
            i++;
        }
    }

    private int getEndPosition(ASTNode node) {
    	return node.getStartPosition() + node.getLength();
    }
    private void insertCommentIntoTree(Comment comment) {
        EntityType label = JavaEntityType.LINE_COMMENT;
        if (comment.isBlockComment()) {
            label = JavaEntityType.BLOCK_COMMENT;
        }
        push(
                label, comment.toString(),
                comment.getStartPosition(),
                getEndPosition(comment));
        pop(comment);
    }

    private boolean previousNodeExistsAndIsNotTheFirstNode() {
        return (fLastVisitedNode != null) && (fLastVisitedNode.getStartPosition() > 0);
    }

    private boolean isCommentBetweenCurrentNodeAndLastNode(Comment comment, ASTNode currentNode) {
        return (fLastVisitedNode.getStartPosition() < comment.getStartPosition())
                && (comment.getStartPosition() < currentNode.getStartPosition());
    }

    private boolean hasComments() {
        return (fComments != null) && !fComments.isEmpty();
    }

    /**
     * Associates a comment to code with the candidate triple {preceedingNode, comment, succeedingNode}
     * 
     * @param node
     *            succeeding node of the triple
     */
    public void postVisit(ASTNode node) {
        if (isUnusableNode(node)) {
            return;
        }
        if (!fLastAssociationCandidate.isEmpty() && (node == fLastAssociationCandidate.peek()[2])) {
            ASTNode preceedingNode = fLastAssociationCandidate.peek()[0];
            ASTNode commentNode = fLastAssociationCandidate.peek()[1];
            ASTNode succeedingNode = fLastAssociationCandidate.peek()[2];

            if ((preceedingNode != null) && (succeedingNode != null)) {
                String preceedingNodeString = getASTString(preceedingNode);
                String succeedingNodeString = getASTString(succeedingNode);
                String commentNodeString = getCommentString(commentNode);

                int rateForPreceeding = proximityRating(preceedingNode, commentNode);
                int rateForSucceeding = proximityRating(commentNode, succeedingNode);
                if (rateForPreceeding == rateForSucceeding) {
                    rateForPreceeding += wordMatching(preceedingNodeString, commentNodeString);
                    rateForSucceeding += wordMatching(succeedingNodeString, commentNodeString);
                }
                if (rateForPreceeding == rateForSucceeding) {
                    rateForSucceeding++;
                }

                Node[] nodeTuple = fLastCommentNodeTuples.peek();
                if (rateForPreceeding > rateForSucceeding) {
                    nodeTuple[1].addAssociatedNode(nodeTuple[0]);
                    nodeTuple[0].addAssociatedNode(nodeTuple[1]);
                } else {
                    nodeTuple[1].addAssociatedNode(fLastAddedNode);
                    fLastAddedNode.addAssociatedNode(nodeTuple[1]);
                }
            }
            fLastAssociationCandidate.pop();
            fLastCommentNodeTuples.pop();
        }
    }

    /**
     * Calculates the proximity between the two given {@link ASTNode}. Usually one of the nodes is a comment.
     * 
     * @param nodeOne
     *            to calculate the proximity
     * @param nodeTwo
     *            to calculate the proximity
     * @return <code>2</code> if the comment node is on the same line as the other node, <code>1</code> if they are on
     *         adjacent line, <code>0</code> otherwise (times two)
     */
    private int proximityRating(ASTNode left, ASTNode right) {
        int result = 0;
        ASTNode nodeOne = left;
        ASTNode nodeTwo = right;
        // swap code, if nodeOne is not before nodeTwo
        if ((nodeTwo.getStartPosition() - nodeOne.getStartPosition()) < 0) {
            ASTNode tmpNode = nodeOne;
            nodeOne = nodeTwo;
            nodeTwo = tmpNode;
        }

        int endOfNodePosition = getEndPosition(nodeOne);

        // comment (nodeTwo) inside nodeOne
        if (endOfNodePosition > nodeTwo.getStartPosition()) {

            // find position before comment start
            String findNodeEndTemp = fSource.substring(nodeOne.getStartPosition(), nodeTwo.getStartPosition());

            // remove white space between nodeOne and comment (nodeTwo)
            int lastNonSpaceChar = findNodeEndTemp.lastIndexOf("[^\\s]");
            if (lastNonSpaceChar > -1) {
                findNodeEndTemp = findNodeEndTemp.substring(lastNonSpaceChar);
            }

            // end position of nodeOne before comment without succeeding white space
            endOfNodePosition = nodeTwo.getStartPosition() - findNodeEndTemp.length();
        }
        String betweenOneAndComment = fSource.substring(endOfNodePosition, nodeTwo.getStartPosition());

        // Comment is on the same line as code, but node in code
        int positionAfterBracket = betweenOneAndComment.lastIndexOf('}');
        int positionAfterSemicolon = betweenOneAndComment.lastIndexOf(';');
        int sameLinePosition = Math.max(positionAfterBracket, positionAfterSemicolon);
        if (sameLinePosition > -1) {
            betweenOneAndComment = betweenOneAndComment.substring(sameLinePosition + 1, betweenOneAndComment.length());
        }

        // 2 points if on the same line as well as inside the code,
        // i.e. there is no line break between the code and the comment
        String newLine = System.getProperty("line.separator");
        if (betweenOneAndComment.indexOf(newLine) == -1) {
            result += 2;

            // 1 point if on the succeeding line,
            // i.e. only one line break between the code and the comment
        } else if (betweenOneAndComment.replaceFirst(newLine, "").indexOf(newLine) == -1) {
            result++;
        }

        return result * 2;
    }

    /**
     * Calculates the word matching between the candidate and the comment string.
     * 
     * @param candidate
     *            to match with
     * @param comment
     *            to match for
     * @return number of tokens the candidate and comment string share (times 2)
     */
    private int wordMatching(String candidate, String comment) {
        int result = 0;

        // split and tokenize candidate string into a hash table
        Map<String, Integer> tokenMatchTable = new Hashtable<String, Integer>();
        String[] candidateTokens = candidate.split("[\\.\\s]+");
        for (String candidateToken : candidateTokens) {
            if (tokenMatchTable.containsKey(candidateToken)) {
                tokenMatchTable.put(candidateToken, tokenMatchTable.remove(candidateToken) + 1);
            } else {
                tokenMatchTable.put(candidateToken, 1);
            }
        }

        // find comment tokens in candidate tokens;
        // number of occurrences are taken as points
        String[] commentTokens = comment.split("\\s+");
        for (String commentToken : commentTokens) {
            if (tokenMatchTable.containsKey(commentToken)) {
                result += tokenMatchTable.get(commentToken);
            }
        }

        return result * 2;
    }

    private String getASTString(ASTNode node) {
        if (node instanceof CompilationUnit) {
            return "";
        }
        String result = node.toString();
        // method and type declaration strings contain their javadoc
        // get rid of the javadoc 
        if (node instanceof MethodDeclaration) {
            MethodDeclaration method = (MethodDeclaration) node;
        	return result.replace(method.getJavadoc().toString(), "");
        }
        if (node instanceof TypeDeclaration) {
        	TypeDeclaration method = (TypeDeclaration) node;
        	return result.replace(method.getJavadoc().toString(), "");}
        }
        return result;
    }

    private String getCommentString(ASTNode node) {
        return ((Comment) node).toString();
    }

    @Override
    public boolean visit(Assignment assignment) {
        return visitExpression(assignment);
    }

    @Override
    public void endVisit(Assignment assignment) {
        endVisitExpression(assignment);
    }

    @Override
    public boolean visit(CompoundAssignment compoundAssignment) {
        return visitExpression(compoundAssignment);
    }

    @Override
    public void endVisit(CompoundAssignment compoundAssignment) {
        endVisitExpression(compoundAssignment);
    }

    @Override
    public boolean visit(PostfixExpression postfixExpression) {
        return visitExpression(postfixExpression);
    }

    @Override
    public void endVisit(PostfixExpression postfixExpression) {
        endVisitExpression(postfixExpression);
    }

    @Override
    public boolean visit(PrefixExpression prefixExpression) {
        return visitExpression(prefixExpression);
    }

    @Override
    public void endVisit(PrefixExpression prefixExpression) {
        endVisitExpression(prefixExpression);
    }

    @Override
    public boolean visit(AllocationExpression allocationExpression) {
        return visitExpression(allocationExpression);
    }

    @Override
    public void endVisit(AllocationExpression allocationExpression) {
        endVisitExpression(allocationExpression);
    }

    @Override
    public boolean visit(QualifiedAllocationExpression qualifiedAllocationExpression) {
        return visitExpression(qualifiedAllocationExpression);
    }

    @Override
    public void endVisit(QualifiedAllocationExpression qualifiedAllocationExpression) {
        endVisitExpression(qualifiedAllocationExpression);
    }

    @Override
    public boolean visit(AssertStatement assertStatement) {
        preVisit(assertStatement);
        String value = assertStatement.assertExpression.toString();
        if (assertStatement.exceptionArgument != null) {
            value += COLON + assertStatement.exceptionArgument.toString();
        }
        push(
                fASTHelper.convertNode(assertStatement),
                value,
                assertStatement.getStartPosition(),
                assertStatement.sourceEnd() + 1);
        return false;
    }

    @Override
    public void endVisit(AssertStatement assertStatement) {
        pop(assertStatement);
        postVisit(assertStatement);
    }

    @Override
    public boolean visit(Block block) {
        // skip block as it is not interesting
        return true;
    }

    @Override
    public void endVisit(Block block) {
        // do nothing
    }

    @Override
    public boolean visit(BreakStatement breakStatement) {
        preVisit(breakStatement);
        pushValuedNode(breakStatement, breakStatement.label != null ? String.valueOf(breakStatement.label) : "");
        return false;
    }

    @Override
    public void endVisit(BreakStatement breakStatement) {
        pop(breakStatement);
        postVisit(breakStatement);
    }

    @Override
    public boolean visit(ExplicitConstructorCall explicitConstructor) {
        preVisit(explicitConstructor);
        pushValuedNode(explicitConstructor, explicitConstructor.toString());
        return false;
    }

    @Override
    public void endVisit(ExplicitConstructorCall explicitConstructor) {
        pop(explicitConstructor);
        postVisit(explicitConstructor);
    }

    @Override
    public boolean visit(ContinueStatement continueStatement) {
        preVisit(continueStatement);
        pushValuedNode(continueStatement, continueStatement.label != null
                ? String.valueOf(continueStatement.label)
                : "");
        return false;
    }

    @Override
    public void endVisit(ContinueStatement continueStatement) {
        pop(continueStatement);
        postVisit(continueStatement);
    }

    @Override
    public boolean visit(DoStatement doStatement) {
        preVisit(doStatement);
        pushValuedNode(doStatement, doStatement.condition.toString());
        doStatement.action.traverse(this);
        return false;
    }

    @Override
    public void endVisit(DoStatement doStatement) {
        pop(doStatement);
        postVisit(doStatement);
    }

    @Override
    public boolean visit(EmptyStatement emptyStatement) {
        preVisit(emptyStatement);
        pushEmptyNode(emptyStatement);
        return false;
    }

    @Override
    public void endVisit(EmptyStatement emptyStatement) {
        pop(emptyStatement);
        postVisit(emptyStatement);
    }

    @Override
    public boolean visit(ForeachStatement foreachStatement) {
        preVisit(foreachStatement);
        pushValuedNode(foreachStatement, foreachStatement.elementVariable.printAsExpression(0, new StringBuffer())
                .toString() + COLON + foreachStatement.collection.toString());
        foreachStatement.action.traverse(this);
        return false;
    }

    @Override
    public void endVisit(ForeachStatement foreachStatement) {
        pop(foreachStatement);
        postVisit(foreachStatement);
    }

    /**
     * Visits an expression.
     * 
     * @param expression
     *            to visit
     * @param scope
     *            in which the expression resides
     * @return <code>true</code> if the children of the expression should be visited, <code>false</code> otherwise.
     */
    public boolean visitExpression(Expression expression) {
        preVisit(expression);
        // all expression processed in this method are statements
        // - use printStatement to get the ';' at the end of the expression
        // - extend the length of the statement by 1 to add ';'
        push(
                fASTHelper.convertNode(expression),
                expression.toString() + ';',
                expression.getStartPosition(),
                getEndPosition(expression) + 1);
        return false;
    }

    private String getSource(int start, int end) {
        return fSource.substring(start, end + 1);
    }

    /**
     * Ends visiting an expression.
     * 
     * @param expression
     *            to end visit with
     * @param scope
     *            in which the visitor visits
     */
    public void endVisitExpression(Expression expression) {
        pop(expression);
        postVisit(expression);
    }

    @Override
    public boolean visit(ForStatement forStatement) {
        preVisit(forStatement);
        // loop condition
        String value = "";
        if (forStatement.condition != null) {
        	value = forStatement.condition.toString();
        }
        pushValuedNode(forStatement, value);
        forStatement.action.traverse(this);
       
        // loop init
        if(forStatement.initializations != null && forStatement.initializations.length > 0) {
        	for(Statement initStatement : forStatement.initializations) {
        		push(
        			JavaEntityType.FOR_INIT,
        			initStatement.toString(),
        			initStatement.getStartPosition(),
        			initStatement.sourceEnd()
        		);
        		
        		initStatement.traverse(this);
        		
        		pop(initStatement);
        	}
        }
        
        // loop afterthought
        if(forStatement.increments != null && forStatement.increments.length > 0) {
        	for(Statement incrementStatement : forStatement.increments) {
        		push(
        			JavaEntityType.FOR_INCR,
        			incrementStatement.toString(),
        			incrementStatement.getStartPosition(),
        			incrementStatement.sourceEnd()
        		);
        		
        		incrementStatement.traverse(this);
        		
        		pop(incrementStatement);
        	}
        }
        
        return false;
    }

    @Override
    public void endVisit(ForStatement forStatement) {
        pop(forStatement);
        postVisit(forStatement);
    }

    @Override
    public boolean visit(IfStatement ifStatement) {
        preVisit(ifStatement);
        String expression = ifStatement.condition.toString();
        push(JavaEntityType.IF_STATEMENT, expression, ifStatement.getStartPosition(), ifStatement.sourceEnd());
        if (ifStatement.thenStatement != null) {
            push(
                    JavaEntityType.THEN_STATEMENT,
                    expression,
                    ifStatement.thenStatement.getStartPosition(),
                    ifStatement.thenStatement.sourceEnd());
            ifStatement.thenStatement.traverse(this);
            pop(ifStatement.thenStatement);
        }
        if (ifStatement.elseStatement != null) {
            push(
                    JavaEntityType.ELSE_STATEMENT,
                    expression,
                    ifStatement.elseStatement.getStartPosition(),
                    ifStatement.elseStatement.sourceEnd());
            ifStatement.elseStatement.traverse(this);
            pop(ifStatement.elseStatement);
        }
        return false;
    }

    @Override
    public void endVisit(IfStatement ifStatement) {
        pop(ifStatement);
        postVisit(ifStatement);
    }

    @Override
    public boolean visit(LabeledStatement labeledStatement) {
        preVisit(labeledStatement);
        pushValuedNode(labeledStatement, String.valueOf(labeledStatement.label));
        labeledStatement.statement.accept(this);
        return false;
    }

    @Override
    public void endVisit(LabeledStatement labeledStatement) {
        pop(labeledStatement);
        postVisit(labeledStatement);
    }

    @Override
    public boolean visit(LocalDeclaration localDeclaration) {
        preVisit(localDeclaration);
        int start = localDeclaration.type.getStartPosition();
        int end = start;
        if (localDeclaration.initialization != null) {
        	end = localDeclaration.initialization.sourceEnd();
        } else {
        	end = localDeclaration.sourceEnd;
        }
        push(fASTHelper.convertNode(localDeclaration), localDeclaration.toString(), start, end + 1);
        return false;
    }

    @Override
    public void endVisit(LocalDeclaration localDeclaration) {
        pop(localDeclaration);
        postVisit(localDeclaration);
    }

    @Override
    public boolean visit(MethodInvocation messageSend) {
        preVisit(messageSend);
        return visitExpression(messageSend);
    }

    @Override
    public void endVisit(MethodInvocation messageSend) {
        endVisitExpression(messageSend);
        postVisit(messageSend);
    }

    @Override
    public boolean visit(ReturnStatement returnStatement) {
        preVisit(returnStatement);
        pushValuedNode(returnStatement, returnStatement.getExpression() != null
                ? returnStatement.getExpression().toString() + ';'
                : "");
        return false;
    }

    @Override
    public void endVisit(ReturnStatement returnStatement) {
        pop(returnStatement);
        postVisit(returnStatement);
    }

    @Override
    public boolean visit(SwitchCase caseStatement) {
        preVisit(caseStatement);
        pushValuedNode(
                caseStatement,
                caseStatement.getExpression() != null ? caseStatement.getExpression().toString() : "default");
        return false;
    }

    @Override
    public void endVisit(SwitchCase caseStatement) {
        pop(caseStatement);
        postVisit(caseStatement);
    }

    @Override
    public boolean visit(SwitchStatement switchStatement) {
        preVisit(switchStatement);
        pushValuedNode(switchStatement, switchStatement.getExpression().toString());
        visitNodes((ASTNode[]) switchStatement.statements().toArray());
        return false;
    }

    @Override
    public void endVisit(SwitchStatement switchStatement) {
        pop(switchStatement);
        postVisit(switchStatement);
    }

    @Override
    public boolean visit(SynchronizedStatement synchronizedStatement) {
        preVisit(synchronizedStatement);
        pushValuedNode(synchronizedStatement, synchronizedStatement.getExpression().toString());
        return true;
    }

    @Override
    public void endVisit(SynchronizedStatement synchronizedStatement) {
        pop(synchronizedStatement);
        postVisit(synchronizedStatement);
    }

    @Override
    public boolean visit(ThrowStatement throwStatement) {
        preVisit(throwStatement);
        pushValuedNode(throwStatement, throwStatement.getExpression().toString() + ';');
        return false;
    }

    @Override
    public void endVisit(ThrowStatement throwStatement) {
        pop(throwStatement);
        postVisit(throwStatement);
    }

    @Override
    public boolean visit(TryStatement node) {
        preVisit(node);
        pushEmptyNode(node);
        push(JavaEntityType.BODY, "", node.tryBlock.getStartPosition(), node.tryBlock.sourceEnd());
        node.tryBlock.traverse(this);
        pop(node.tryBlock);
        visitCatchClauses(node);
        visitFinally(node);
        return false;
    }

    private void visitFinally(TryStatement node) {
        if (node.finallyBlock != null) {
            push(JavaEntityType.FINALLY, "", node.finallyBlock.getStartPosition(), node.finallyBlock.sourceEnd());
            node.finallyBlock.traverse(this);
            pop(node.finallyBlock);
        }
    }

    private void visitCatchClauses(TryStatement node) {
        if ((node.catchBlocks != null) && (node.catchBlocks.length > 0)) {
            Block lastCatchBlock = node.catchBlocks[node.catchBlocks.length - 1];
            push(JavaEntityType.CATCH_CLAUSES, "", node.tryBlock.sourceEnd + 1, lastCatchBlock.sourceEnd);
            int start = node.tryBlock.sourceEnd();
            for (int i = 0; i < node.catchArguments.length; i++) {
                int catchClausegetStartPosition = retrieveStartingCatchPosition(start, node.catchArguments[i].getStartPosition);
                push(
                        JavaEntityType.CATCH_CLAUSE,
                        node.catchArguments[i].type.toString(),
                        catchClausegetStartPosition,
                        node.catchBlocks[i].sourceEnd);
                node.catchBlocks[i].traverse(this);
                pop(node.catchArguments[i].type);
                start = node.catchBlocks[i].sourceEnd();
            }
            pop(null);
        }
    }

    // logic taken from org.eclipse.jdt.core.dom.ASTConverter
    private int retrieveStartingCatchPosition(int start, int end) {
        fScanner.resetTo(start, end);
        try {
            int token;
            while ((token = fScanner.getNextToken()) != TerminalTokens.TokenNameEOF) {
                switch (token) {
                    case TerminalTokens.TokenNamecatch:// 225
                        return fScanner.startPosition;
                }
            }
            // CHECKSTYLE:OFF
        } catch (InvalidInputException e) {
            // CHECKSTYLE:ON
            // ignore
        }
        return -1;
    }

    @Override
    public void endVisit(TryStatement tryStatement) {
        pop(tryStatement);
        postVisit(tryStatement);
    }

    @Override
    public boolean visit(WhileStatement whileStatement) {
        preVisit(whileStatement);
        push(
                fASTHelper.convertNode(whileStatement),
                whileStatement.getExpression().toString(),
                whileStatement.getStartPosition(),
                getEndPosition(whileStatement));
        whileStatement.getBody().accept(this);
        return false;
    }

    @Override
    public void endVisit(WhileStatement whileStatement) {
        pop(whileStatement);
        postVisit(whileStatement);
    }

    private void visitNodes(ASTNode[] nodes) {
        for (ASTNode element : nodes) {
            element.accept(this);
        }
    }

    private void pushValuedNode(ASTNode node, String value) {
        push(fASTHelper.convertNode(node), value, node.getStartPosition(), getEndPosition(node));
    }

    private void pushEmptyNode(ASTNode node) {
        push(fASTHelper.convertNode(node), "", node.getStartPosition(), getEndPosition(node));
    }

    private void push(EntityType label, String value, int start, int end) {
        Node n = new Node(label, value.trim());
        n.setEntity(new SourceCodeEntity(value.trim(), label, new SourceRange(start, end)));
        getCurrentParent().add(n);
        fNodeStack.push(n);
    }

    private void pop(ASTNode node) {
        fLastVisitedNode = node;
        fLastAddedNode = fNodeStack.pop();
    }

    private Node getCurrentParent() {
        return fNodeStack.peek();
    }

    private boolean isUnusableNode(ASTNode node) {
        return node instanceof Comment;
    }
}
