package org.evolizer.changedistiller.java;

import org.evolizer.changedistiller.treedifferencing.Node;
import org.evolizer.changedistiller.util.Compilation;
import org.evolizer.changedistiller.util.CompilationUtils;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;

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

    protected Node getLastChild() {
        return (Node) fRoot.getLastChild();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void assertThat(Object actual, Matcher matcher) {
        MatcherAssert.assertThat(actual, matcher);
    }

}