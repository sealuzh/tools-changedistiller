package org.evolizer.changedistiller.java;

import org.evolizer.changedistiller.treedifferencing.Node;
import org.evolizer.changedistiller.util.Compilation;
import org.evolizer.changedistiller.util.CompilationUtils;

public abstract class WhenASTsAreConverted {

    protected String fSnippet;
    protected Compilation fCompilation;
    protected Node fRoot;

    public WhenASTsAreConverted() {
        super();
    }

    protected void prepareCompilation() {
        fCompilation = CompilationUtils.compileSource(getSourceCodeWithSnippets(fSnippet));
    }

    protected abstract String getSourceCodeWithSnippets(String... sourceSnippets);

    protected String getTreeString() {
        return fRoot.print(new StringBuilder()).toString();
    }

    protected Node getFirstLeaf() {
        return ((Node) fRoot.getFirstLeaf());
    }

    protected Node getFirstChild() {
        return (Node) fRoot.getFirstChild();
    }

}