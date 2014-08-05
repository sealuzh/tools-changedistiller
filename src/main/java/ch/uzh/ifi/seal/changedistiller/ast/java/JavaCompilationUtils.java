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

import ch.uzh.ifi.seal.changedistiller.ast.FileUtils;
import ch.uzh.ifi.seal.changedistiller.ast.InvalidSyntaxException;

/**
 * Utility class for Java compilation.
 * 
 * @author Beat Fluri
 * @author linzhp
 * @author wuersch
 * 
 */
public final class JavaCompilationUtils {

    private JavaCompilationUtils() {}

    /**
     * Returns the compiled source as a {@link JavaCompilation}.
     * 
     * @param Java source
     *            to compile
     * @return the compilation of the Java source
     * @throws InvalidSyntaxException if the file has syntax errors.
     */
    public static JavaCompilation compile(String source, String fileName) {
        CompilerOptions options = getDefaultCompilerOptions();
        Parser parser = createCommentRecorderParser(options);
        ICompilationUnit cu = createCompilationUnit(source, fileName);
        CompilationResult compilationResult = createDefaultCompilationResult(cu, options);
        JavaCompilation javaCompilation = new JavaCompilation(parser.parse(cu, compilationResult), parser.scanner);
        
        if (compilationResult.hasSyntaxError) {
        	throw new InvalidSyntaxException(new String(compilationResult.getFileName()), compilationResult.toString());
        }
		
        return javaCompilation;
    }
    
    /**
     * Returns the compiled file as a {@link JavaCompilation}.
     * 
     * @param file
     *            to compile
     * @param version
     * 			  of Java used in the file
     * @return the compilation of the file
     * @throws InvalidSyntaxException if the file has syntax errors.
     */
    public static JavaCompilation compile(File file, long version) {
        CompilerOptions options = getDefaultCompilerOptions(version);
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

    private static CompilerOptions getDefaultCompilerOptions(long version) {
        CompilerOptions options = new CompilerOptions();
        options.docCommentSupport = true;
        options.complianceLevel = version;
        options.sourceLevel = version;
        options.targetJDK = version;
        return options;
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
