package org.evolizer.changedistiller.ast.java;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.evolizer.changedistiller.ast.java.Comment;
import org.evolizer.changedistiller.ast.java.JavaCompilation;
import org.evolizer.changedistiller.util.CompilationUtils;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test case testing comment extraction for a compilation unit.
 * 
 * @author Beat Fluri
 */
public class WhenCommentsAreExtracted {

    private static JavaCompilation sCompilationUnit;

    @BeforeClass
    public static void prepareCompilationUnit() throws Exception {
        sCompilationUnit = CompilationUtils.compileFile("ClassWithComments.java");
    }

    @Test
    public void compilationUnitOfClassWithCommentsShouldHaveComments() throws Exception {
        List<Comment> comments = CompilationUtils.extractComments(sCompilationUnit);
        assertThat(comments.size(), is(3));
        assertThat(comments.get(0).getComment(), is("/**\n * A class with comments.\n *\n * @author Beat Fluri\n */"));
        assertThat(comments.get(1).getComment(), is("// a simple method invocation"));
        assertThat(comments.get(2).getComment(), is("/* no more methods */"));
    }

}
