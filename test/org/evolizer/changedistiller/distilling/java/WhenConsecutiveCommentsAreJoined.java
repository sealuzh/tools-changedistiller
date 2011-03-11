package org.evolizer.changedistiller.distilling.java;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.evolizer.changedistiller.distilling.java.Comment;
import org.evolizer.changedistiller.distilling.java.CommentCleaner;
import org.evolizer.changedistiller.util.Compilation;
import org.evolizer.changedistiller.util.CompilationUtils;
import org.junit.BeforeClass;
import org.junit.Test;

public class WhenConsecutiveCommentsAreJoined {

    private static Compilation sCompilationUnit;
    private static List<Comment> sComments;

    @BeforeClass
    public static void prepareCompilationUnit() throws Exception {
        sCompilationUnit = CompilationUtils.compileFile("ClassWithConsecutiveComments.java");
        List<Comment> comments = CompilationUtils.extractComments(sCompilationUnit);
        CommentCleaner visitor = new CommentCleaner(sCompilationUnit.getSource());
        for (Comment comment : comments) {
            visitor.process(comment);
        }
        sComments = visitor.getComments();
    }

    @Test
    public void consecutiveLineCommentsShouldBeJoined() throws Exception {
        assertThat(getCommentString(sComments.get(0)), is("// a simple method invocation\n        // simple indeed"));
        assertThat(sComments.get(0).getComment(), is("// a simple method invocation\n        // simple indeed"));
    }

    @Test
    public void consecutiveBlockCommentsShouldNotBeJoined() throws Exception {
        assertThat(getCommentString(sComments.get(1)), is("/* first block comment */"));
        assertThat(sComments.get(1).getComment(), is("/* first block comment */"));
        assertThat(getCommentString(sComments.get(2)), is("/* second block comment */"));
        assertThat(sComments.get(2).getComment(), is("/* second block comment */"));
    }

    @Test
    public void consecutiveBlockAndLineCommentsShouldNotBeJoined() throws Exception {
        assertThat(getCommentString(sComments.get(3)), is("/* no more methods */"));
        assertThat(sComments.get(3).getComment(), is("/* no more methods */"));
        assertThat(getCommentString(sComments.get(4)), is("// no more line comments"));
        assertThat(sComments.get(4).getComment(), is("// no more line comments"));
    }

    @Test
    public void deadCodeShouldBeRemoved() throws Exception {
        assertThat(sComments.size(), is(5));
    }

    public String getCommentString(Comment comment) {
        return sCompilationUnit.getSource().substring(comment.sourceStart(), comment.sourceEnd());
    }

}
