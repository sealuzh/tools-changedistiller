package org.evolizer.changedistiller.java;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.DefaultErrorHandlingPolicies;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.core.util.CommentRecorderParser;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test case testing comment extraction for a compilation unit.
 * 
 * @author Beat Fluri
 */
public class WhenCommentsAreExtracted {

    private static CompilationUnitDeclaration sCompilationUnit;
    private static String sSource;

    @BeforeClass
    public static void prepareCompilationUnit() throws Exception {
        CompilerOptions options = new CompilerOptions();
        options.docCommentSupport = true;
        Parser parser =
                new CommentRecorderParser(new ProblemReporter(
                        DefaultErrorHandlingPolicies.proceedWithAllProblems(),
                        options,
                        new DefaultProblemFactory()), false);
        ICompilationUnit cu =
                new org.eclipse.jdt.internal.compiler.batch.CompilationUnit(getContentOfFile(
                        "resources/testdata/ClassWithComments.java").toCharArray(), "ClassWithComments.java", null);
        CompilationResult compilationResult = new CompilationResult(cu, 0, 0, options.maxProblemsPerUnit);
        sCompilationUnit = parser.dietParse(cu, compilationResult);
        sSource = new String(parser.scanner.source);
    }

    @Test
    public void compilationUnitOfClassWithCommentsShouldHaveComments() throws Exception {
        CommentCollector collector = new CommentCollector(sCompilationUnit, sSource);
        collector.collect();
        List<Comment> comments = collector.getComments();
        assertThat(comments.size(), is(3));
        assertThat(comments.get(0).getComment(), is("/**\n * A class with comments.\n *\n * @author Beat Fluri\n */"));
        assertThat(comments.get(1).getComment(), is("// a simple method invocation"));
        assertThat(comments.get(2).getComment(), is("/* no more methods */"));
    }

    private static String getContentOfFile(String filename) {
        char[] b = new char[1024];
        StringBuilder sb = new StringBuilder();
        try {
            FileReader reader = new FileReader(new File(filename));
            int n = reader.read(b);
            while (n > 0) {
                sb.append(b, 0, n);
                n = reader.read(b);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

}
