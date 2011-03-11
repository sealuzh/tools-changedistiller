package org.evolizer.changedistiller.distilling.java;

import org.evolizer.changedistiller.model.classifiers.EntityType;
import org.evolizer.changedistiller.model.classifiers.SourceRange;
import org.evolizer.changedistiller.model.entities.SourceCodeEntity;
import org.evolizer.changedistiller.treedifferencing.Node;
import org.evolizer.changedistiller.util.Compilation;
import org.evolizer.changedistiller.util.CompilationUtils;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.junit.BeforeClass;

public abstract class WhenASTsAreConverted extends JavaDistillerTestCase {

    protected static JavaDeclarationConverter sDeclarationConverter;
    protected static JavaMethodBodyConverter sMethodBodyConverter;

    protected String fSnippet;
    protected Compilation fCompilation;
    protected Node fRoot;

    @BeforeClass
    public static void initialize() throws Exception {
        sDeclarationConverter = sInjector.getInstance(JavaDeclarationConverter.class);
        sMethodBodyConverter = sInjector.getInstance(JavaMethodBodyConverter.class);
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

    protected void createRootNode(EntityType label, String value) {
        fRoot = new Node(label, value);
        fRoot.setEntity(new SourceCodeEntity(value, label, new SourceRange()));
    }

}