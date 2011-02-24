package org.evolizer.changedistiller.util;

import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.parser.Scanner;

public class Compilation {

    private CompilationUnitDeclaration fCompilationUnit;
    private Scanner fScanner;

    public Compilation(CompilationUnitDeclaration compilationUnit, Scanner scanner) {
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
