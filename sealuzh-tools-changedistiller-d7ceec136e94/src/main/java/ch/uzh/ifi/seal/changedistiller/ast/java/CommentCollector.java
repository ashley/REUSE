package ch.uzh.ifi.seal.changedistiller.ast.java;


import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;

import ch.uzh.ifi.seal.changedistiller.ast.java.NewComment.NewCommentType;

public class CommentCollector {

    private JavaCompilation fCompilationUnit;
    private String fSource;
    private List<NewComment> fComments;

    public CommentCollector(JavaCompilation compilationUnit, String source) {
        fCompilationUnit = compilationUnit;
        fSource = source;
    }

    /**
     * Collects the comments of the {@link CompilationUnitDeclaration}.
     */
    public void collect() {
        if (isNotYetCollected()) {
            fComments = new LinkedList<NewComment>();
            List<Comment> comments = fCompilationUnit.getCompilationUnit().getCommentList();
            for (Comment comment : comments) {
                fComments.add(createComment(comment));
            }
        }
    }

    // Logic taken from org.eclipse.jdt.core.dom.ASTConverter
    private NewComment createComment(Comment oldComment) {
        NewComment comment = null;
        int start = oldComment.getStartPosition();
        int end = start + oldComment.getLength();
        
        // Javadoc comments have positive end position
        if (end > 0) {
            comment = new NewComment(NewCommentType.JAVA_DOC, start, end, fSource.substring(start, end));
        } else {
            end = -end;
            // we cannot know without testing chars again
            if (start == 0) {
                if (fSource.charAt(1) == '/') {
                    comment = new NewComment(NewCommentType.LINE_COMMENT, start, end, fSource.substring(start, end));
                } else {
                    comment = new NewComment(NewCommentType.BLOCK_COMMENT, start, end, fSource.substring(start, end));
                }
            } else if (start > 0) { // Block comment have positive start position
                comment = new NewComment(NewCommentType.BLOCK_COMMENT, start, end, fSource.substring(start, end));
            } else { // Line comment have negative start and end position
                start = -start;
                comment = new NewComment(NewCommentType.LINE_COMMENT, start, end, fSource.substring(start, end));
            }
        }
        return comment;
    }

    private boolean isNotYetCollected() {
        return (fComments == null) || fComments.isEmpty();
    }

    public List<NewComment> getComments() {
        return fComments;
    }

}
