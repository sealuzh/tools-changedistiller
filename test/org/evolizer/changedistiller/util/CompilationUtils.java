package org.evolizer.changedistiller.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.DefaultErrorHandlingPolicies;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.core.util.CommentRecorderParser;
import org.evolizer.changedistiller.java.Comment;
import org.evolizer.changedistiller.java.CommentCollector;

public final class CompilationUtils {

    private static final String TEST_DATA_BASE_DIR = "resources/testdata/";

    private CompilationUtils() {}

    public static CompilationUnitWithSource prepareCompilationUnit(String filename) {
        CompilerOptions options = new CompilerOptions();
        options.docCommentSupport = true;
        Parser parser =
                new CommentRecorderParser(new ProblemReporter(
                        DefaultErrorHandlingPolicies.proceedWithAllProblems(),
                        options,
                        new DefaultProblemFactory()), false);
        ICompilationUnit cu =
                new org.eclipse.jdt.internal.compiler.batch.CompilationUnit(getContentOfFile(
                        TEST_DATA_BASE_DIR + filename).toCharArray(), filename, null);
        CompilationResult compilationResult = new CompilationResult(cu, 0, 0, options.maxProblemsPerUnit);
        return new CompilationUnitWithSource(parser.dietParse(cu, compilationResult), new String(parser.scanner.source));
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

    public static List<Comment> extractComments(CompilationUnitWithSource sCompilationUnit) {
        CommentCollector collector =
                new CommentCollector(sCompilationUnit.getCompilationUnit(), sCompilationUnit.getSource());
        collector.collect();
        return collector.getComments();
    }

}
