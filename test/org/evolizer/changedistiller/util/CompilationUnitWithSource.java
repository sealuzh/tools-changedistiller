package org.evolizer.changedistiller.util;

import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;

public class CompilationUnitWithSource {

    private CompilationUnitDeclaration fCompilationUnit;
    private String fSource;

    public CompilationUnitWithSource(CompilationUnitDeclaration compilationUnit, String source) {
        fCompilationUnit = compilationUnit;
        fSource = source;
    }

    public CompilationUnitDeclaration getCompilationUnit() {
        return fCompilationUnit;
    }

    public String getSource() {
        return fSource;
    }

}
