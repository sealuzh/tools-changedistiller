package org.evolizer.changedistiller.compilation.java;

import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.parser.Scanner;

/**
 * Container for {@link CompilationUnitDeclaration} and the corresponding {@link Scanner}.
 * 
 * @author Beat Fluri
 */
public class JavaCompilation {

    private CompilationUnitDeclaration fCompilationUnit;
    private Scanner fScanner;

    /**
     * Create a new Java compilation
     * 
     * @param compilationUnit
     *            of the compilation
     * @param scanner
     *            that produced the compilation
     */
    public JavaCompilation(CompilationUnitDeclaration compilationUnit, Scanner scanner) {
        fCompilationUnit = compilationUnit;
        fScanner = scanner;
    }

    public CompilationUnitDeclaration getCompilationUnit() {
        return fCompilationUnit;
    }

    public String getSource() {
        return String.valueOf(fScanner.source);
    }

    public Scanner getScanner() {
        return fScanner;
    }
}
