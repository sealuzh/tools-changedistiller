package org.evolizer.changedistiller.compilation.java;

import java.io.File;

import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.DefaultErrorHandlingPolicies;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.core.util.CommentRecorderParser;
import org.evolizer.changedistiller.compilation.FileUtils;

/**
 * Utility class for Java compilation.
 * 
 * @author Beat Fluri
 */
public final class JavaCompilationUtils {

    private JavaCompilationUtils() {}

    /**
     * Returns the compiled file as a {@link JavaCompilation}.
     * 
     * @param file
     *            to compile
     * @return the compilation of the file
     */
    public static JavaCompilation compile(File file) {
        CompilerOptions options = getDefaultCompilerOptions();
        Parser parser = createCommentRecorderParser(options);
        ICompilationUnit cu = createCompilationUnit(FileUtils.getContent(file), file.getName());
        CompilationResult compilationResult = createDefaultCompilationResult(cu, options);
        return new JavaCompilation(parser.parse(cu, compilationResult), parser.scanner);
    }

    private static CompilationResult createDefaultCompilationResult(ICompilationUnit cu, CompilerOptions options) {
        return new CompilationResult(cu, 0, 0, options.maxProblemsPerUnit);
    }

    private static ICompilationUnit createCompilationUnit(String source, String filename) {
        return new CompilationUnit(source.toCharArray(), filename, null);
    }

    private static CompilerOptions getDefaultCompilerOptions() {
        CompilerOptions options = new CompilerOptions();
        options.docCommentSupport = true;
        options.complianceLevel = ClassFileConstants.JDK1_6;
        options.sourceLevel = ClassFileConstants.JDK1_6;
        options.targetJDK = ClassFileConstants.JDK1_6;
        return options;
    }

    private static Parser createCommentRecorderParser(CompilerOptions options) {
        return new CommentRecorderParser(new ProblemReporter(
                DefaultErrorHandlingPolicies.proceedWithAllProblems(),
                options,
                new DefaultProblemFactory()), false);
    }

}
