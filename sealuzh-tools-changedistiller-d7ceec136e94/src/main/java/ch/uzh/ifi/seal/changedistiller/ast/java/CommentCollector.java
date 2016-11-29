package ch.uzh.ifi.seal.changedistiller.ast.java;


import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.NewComment;
import org.eclipse.jdt.core.dom.NewComment.CommentType;
import org.eclipse.jdt.core.dom.NewComment.NewCommentType;

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
        // new NewComment(oldComment.getAST(),comment.getType(), oldComment.getStartPosition(), oldComment.getLength(), " ");
    	int start = oldComment.getStartPosition();
        int end = start + oldComment.getLength();
        NewComment comment = null;
        try{
    	comment = new NewComment(oldComment.getAST());
    	}
    	catch (Exception e){
    		System.out.println(e);
    	}
        //comment.setStart(start);
        //comment.setEnd(end);
        
        // Javadoc comments have positive end position
        
        return comment;
    }

    private boolean isNotYetCollected() {
        return (fComments == null) || fComments.isEmpty();
    }

    public List<NewComment> getComments() {
        return fComments;
    }

}
