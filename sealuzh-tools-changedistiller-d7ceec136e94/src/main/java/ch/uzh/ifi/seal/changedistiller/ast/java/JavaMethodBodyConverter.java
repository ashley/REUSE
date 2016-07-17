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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

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

    private ASTNode fLastVisitedNode;
    private Node fLastAddedNode;

    private Stack<ASTNode[]> fLastAssociationCandidate;
    private Stack<Node[]> fLastCommentNodeTuples;
    private ASTNodeTypeConverter fASTHelper;

    // FIXME: compare to the entity types to make sure we cover all of them
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
    public void initialize(Node root, ASTNode methodRoot, List<Comment> comments, String source) {
    	// FIXME: I am VERY close to killing the source, but to do that I need to figure out
    	// why we need the source for proximity computation, which is clearly required for comment association.
        fNodeStack.clear();
        fLastAssociationCandidate.clear();
        fLastCommentNodeTuples.clear();
        fLastVisitedNode = methodRoot;
        fLastAddedNode = root;
        fNodeStack.push(root);
        fComments = comments == null ? null : new LinkedList(comments);
        fSource = source;
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
    	int end = node.getStartPosition() + node.getLength();
    	char charAt = fSource.charAt(end);
    	switch(charAt) {
    	case ' ':
    	case ';': return end - 1;
    	default: return end;
    	}
    }
    
    private void insertCommentIntoTree(Comment comment) {
        EntityType label = JavaEntityType.LINE_COMMENT;
        if (comment.isBlockComment()) {
            label = JavaEntityType.BLOCK_COMMENT;
        }
        push(
                label, comment.toString(),
                comment.getStartPosition(),
                getEndPosition(comment),
                comment);
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
        	return result.replace(method.getJavadoc().toString(), "");
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
    public boolean visit(AssertStatement assertStatement) {
        preVisit(assertStatement);
        String value = assertStatement.getExpression().toString();
        if (assertStatement.getMessage() != null) {
            value += COLON + assertStatement.getMessage().toString();
        }
        push(
                fASTHelper.convertNode(assertStatement),
                value,
                assertStatement.getStartPosition(),
                getEndPosition(assertStatement),
                assertStatement);
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
        pushValuedNode(breakStatement, breakStatement.getLabel()  != null ? breakStatement.getLabel().getIdentifier()  : "");
        return false;
    }

    @Override
    public void endVisit(BreakStatement breakStatement) {
        pop(breakStatement);
        postVisit(breakStatement);
    }

    @Override
    public boolean visit(ConstructorInvocation node) {
    	preVisit(node);
        push(fASTHelper.convertNode(node), node.toString(), node.getStartPosition(), getEndPosition(node), node);

    	return false;
    }
    
    @Override
    public boolean visit(ClassInstanceCreation explicitConstructor) {
        preVisit(explicitConstructor); // for some reason I'm not handling classinstancecreations inside visitExpression, hence the + 1 (wanna grab the ;) 
        push(fASTHelper.convertNode(explicitConstructor), explicitConstructor.toString() + ";", explicitConstructor.getStartPosition(), getEndPosition(explicitConstructor) + 1, explicitConstructor);
        return false;
    }

    @Override
    public void endVisit(ClassInstanceCreation explicitConstructor) {
        pop(explicitConstructor);
        postVisit(explicitConstructor);
    }

    @Override
    public boolean visit(ContinueStatement continueStatement) {
        preVisit(continueStatement);
        pushValuedNode(continueStatement, continueStatement.getLabel() != null
                ? continueStatement.getLabel().getIdentifier()
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
        pushValuedNode(doStatement, "(" + doStatement.getExpression().toString() + ")");
        doStatement.getBody().accept(this);
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
    public boolean visit(EnhancedForStatement foreachStatement) {
        preVisit(foreachStatement);
        pushValuedNode(foreachStatement, foreachStatement.getParameter().toString() // I think this does the right thing
                + COLON + foreachStatement.getExpression().toString());
        foreachStatement.getBody().accept(this);
        return false;
    }

    @Override
    public void endVisit(EnhancedForStatement foreachStatement) {
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
                getEndPosition(expression) + 1,
                expression);
        return false;
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

	@SuppressWarnings("unchecked")
    @Override
    public boolean visit(ForStatement forStatement) {
        preVisit(forStatement);
        // loop condition
        String value = "";
        if (forStatement.getExpression() != null) {
        	value =  "(" + forStatement.getExpression().toString() + ")" ;
        }
        pushValuedNode(forStatement, value);
        forStatement.getBody().accept(this);
       
        // loop init
        forStatement.initializers(); // statements or variable declaration expressions
        if(forStatement.initializers() != null && forStatement.initializers().size() > 0) {
			List<ASTNode> initializers = forStatement.initializers();
        	for(ASTNode initStatement : initializers) {
        		push(
        			JavaEntityType.FOR_INIT,
        			initStatement.toString(),
        			initStatement.getStartPosition(),
        			getEndPosition(initStatement),
        			initStatement
        		);
        		initStatement.accept(this);
        		pop(initStatement);
        	}
        }
        
        // loop afterthought
        if(forStatement.updaters() != null && forStatement.updaters().size() > 0) {
            List<Expression> updaters = forStatement.updaters();
        	for(Expression incrementStatement : updaters) {
        		push(
        			JavaEntityType.FOR_INCR,
        			incrementStatement.toString(),
        			incrementStatement.getStartPosition(),
        			getEndPosition(incrementStatement),
        			incrementStatement
        		);
        		
        		incrementStatement.accept(this);
        		
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
        String expression = ifStatement.getExpression().toString();
        push(JavaEntityType.IF_STATEMENT, expression, ifStatement.getStartPosition(), getEndPosition(ifStatement), ifStatement);
        if (ifStatement.getThenStatement() != null) {
            push(
                    JavaEntityType.THEN_STATEMENT,
                    expression,
                    ifStatement.getThenStatement().getStartPosition(),
                    getEndPosition(ifStatement.getThenStatement()),
                    ifStatement.getThenStatement());
            ifStatement.getThenStatement().accept(this);
            pop(ifStatement.getThenStatement());
        }
        if (ifStatement.getElseStatement() != null) {
            push(
                    JavaEntityType.ELSE_STATEMENT,
                    expression,
                    ifStatement.getElseStatement().getStartPosition(),
                    getEndPosition(ifStatement.getElseStatement()),
                    ifStatement.getElseStatement());
            ifStatement.getElseStatement().accept(this);
            pop(ifStatement.getElseStatement());
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
        pushValuedNode(labeledStatement, labeledStatement.getLabel().getIdentifier());
        labeledStatement.getBody().accept(this);
        return false;
    }

    @Override
    public void endVisit(LabeledStatement labeledStatement) {
        pop(labeledStatement);
        postVisit(labeledStatement);
    }

    @Override
    public boolean visit(VariableDeclarationStatement localDeclaration) {
    	// FIXME: this is one of the methods whose correctness I am least certain of
        preVisit(localDeclaration);
        int start = localDeclaration.getType().getStartPosition();
        push(fASTHelper.convertNode(localDeclaration), 
        		localDeclaration.toString(), start, getEndPosition(localDeclaration),
        		localDeclaration);
        return false;
    }

    @Override
    public void endVisit(VariableDeclarationStatement localDeclaration) {
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

    @SuppressWarnings("unchecked")
	@Override
    public boolean visit(SwitchStatement switchStatement) {
        preVisit(switchStatement);
        pushValuedNode(switchStatement, switchStatement.getExpression().toString());
        List<Statement> statements = switchStatement.statements();
        for (Statement element : statements) {
            element.accept(this);
        }
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
        push(JavaEntityType.BODY, "", node.getBody().getStartPosition(), getEndPosition(node.getBody()), node.getBody());
        node.getBody().accept(this);
        pop(node.getBody());
        visitCatchClauses(node);
        visitFinally(node);
        return false;
    }

    private void visitFinally(TryStatement node) {
        if (node.getFinally() != null) {
            push(JavaEntityType.FINALLY, "", node.getFinally().getStartPosition(), getEndPosition(node.getFinally()) - 2, node.getFinally());
            node.getFinally().accept(this);
            pop(node.getFinally());
        }
    }

    @SuppressWarnings("unchecked")
	private void visitCatchClauses(TryStatement node) {
        if ((node.catchClauses() != null) && (node.catchClauses().size() > 0)) {
            Block lastCatchBlock = ((CatchClause) node.catchClauses().get(node.catchClauses().size() - 1)).getBody(); // FIXME: it's not clear that the node to pass to the push on the next line should be the last catch block
            push(JavaEntityType.CATCH_CLAUSES, "", getEndPosition(node.getBody()), getEndPosition(lastCatchBlock) , lastCatchBlock);
            int start = getEndPosition(node.getBody());
            List<CatchClause> catchClauses = node.catchClauses();
            for(CatchClause catchClause : catchClauses) {
                // FIXME: there was a complicated thing here to compute the positions of
                // catch clauses that I believe to be unnecessary when using the jdt dom
                // but if things go weird, this may be related
                push(
                        JavaEntityType.CATCH_CLAUSE,
                        catchClause.getException().getType().toString(), 
                        catchClause.getStartPosition(),
                        getEndPosition(catchClause),
                        catchClause);
                catchClause.getBody().accept(this); 
                pop(catchClause.getException().getType());
            }
            pop(null);
        }
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
               "(" + whileStatement.getExpression().toString() +")",
                whileStatement.getStartPosition(),
                getEndPosition(whileStatement),
                whileStatement);
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
        push(fASTHelper.convertNode(node), value, node.getStartPosition(), getEndPosition(node), node);
    }

    private void pushEmptyNode(ASTNode node) {
        push(fASTHelper.convertNode(node), "", node.getStartPosition(), getEndPosition(node), node);
    }

    private void push(EntityType label, String value, int start, int end, ASTNode node) {
        Node n = new Node(label, value.trim());
        n.setEntity(new SourceCodeEntity(value.trim(), label, new SourceRange(start, end), node));
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
