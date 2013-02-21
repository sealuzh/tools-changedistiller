package ch.uzh.ifi.seal.changedistiller.ast.java;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;

import ch.uzh.ifi.seal.changedistiller.ast.java.Comment.CommentType;

/**
 * Collects comments for a {@link CompilationUnitDeclaration}.
 * 
 * @author Beat Fluri
 */
public class CommentCollector {

    private CompilationUnitDeclaration fCompilationUnit;
    private String fSource;
    private List<Comment> fComments;

    /**
     * Creates a new comment collector.
     * 
     * @param compilationUnit
     *            from which the comments should be collected
     * @param source
     *            of the compilation unit
     */
    public CommentCollector(CompilationUnitDeclaration compilationUnit, String source) {
        fCompilationUnit = compilationUnit;
        fSource = source;
    }

    /**
     * Collects the comments of the {@link CompilationUnitDeclaration}.
     */
    public void collect() {
        if (isNotYetCollected()) {
            fComments = new LinkedList<Comment>();
            for (int[] positions : fCompilationUnit.comments) {
                fComments.add(createComment(positions));
            }
        }
    }

    // Logic taken from org.eclipse.jdt.core.dom.ASTConverter
    private Comment createComment(int[] positions) {
        Comment comment = null;
        int start = positions[0];
        int end = positions[1];
        // Javadoc comments have positive end position
        if (end > 0) {
            comment = new Comment(CommentType.JAVA_DOC, start, end, fSource.substring(start, end));
        } else {
            end = -end;
            // we cannot know without testing chars again
            if (start == 0) {
                if (fSource.charAt(1) == '/') {
                    comment = new Comment(CommentType.LINE_COMMENT, start, end, fSource.substring(start, end));
                } else {
                    comment = new Comment(CommentType.BLOCK_COMMENT, start, end, fSource.substring(start, end));
                }
            } else if (start > 0) { // Block comment have positive start position
                comment = new Comment(CommentType.BLOCK_COMMENT, start, end, fSource.substring(start, end));
            } else { // Line comment have negative start and end position
                start = -start;
                comment = new Comment(CommentType.LINE_COMMENT, start, end, fSource.substring(start, end));
            }
        }
        return comment;
    }

    private boolean isNotYetCollected() {
        return (fComments == null) || fComments.isEmpty();
    }

    public List<Comment> getComments() {
        return fComments;
    }

}
