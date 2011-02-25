package org.evolizer.changedistiller.java;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Enumeration;

import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.evolizer.changedistiller.model.classifiers.SourceRange;
import org.evolizer.changedistiller.model.classifiers.java.JavaEntityType;
import org.evolizer.changedistiller.model.entities.SourceCodeEntity;
import org.evolizer.changedistiller.treedifferencing.Node;
import org.evolizer.changedistiller.util.CompilationUtils;
import org.junit.Test;

public class WhenDeclarationsAreConverted extends WhenASTsAreConverted {

    @Test
    public void fieldDeclarationShouldBeConverted() throws Exception {
        fSnippet = "private String fField;";
        prepareCompilation();
        transformField("fField");
        assertThat(getTreeString(), is("fField {  { private },String }"));
        assertModifiersCorrectness(getFirstChild(), "private");
        Node type = (Node) getFirstChild().getNextSibling();
        assertThat(getSource(type), is("String"));
    }

    @Test
    public void fieldDeclarationWithInitializerShouldBeConverted() throws Exception {
        fSnippet = "private String fField = \"aString\";";
        prepareCompilation();
        transformField("fField");
        assertThat(getTreeString(), is("fField {  { private },String,\"aString\" }"));
        assertModifiersCorrectness(getFirstChild(), "private");
        Node type = (Node) getFirstChild().getNextSibling();
        assertThat(getSource(type), is("String"));
        Node initializer = (Node) type.getNextSibling();
        assertThat(getSource(initializer), is("\"aString\""));
    }

    @Test
    // just check the modifiers
    public void fieldDeclarationWithMultiModifiersShouldBeConverted() throws Exception {
        fSnippet = "private final String fField;";
        prepareCompilation();
        transformField("fField");
        assertThat(getTreeString(), is("fField {  { private,final },String }"));
        assertModifiersCorrectness(getFirstChild(), "private", "final");
    }

    @Test
    public void fieldDeclarationWithTypeParameterShouldBeConverted() throws Exception {
        fSnippet = "List<String> fList;";
        prepareCompilation();
        transformField("fList");
        assertThat(getTreeString(), is("fList { ,List {  { String } } }"));
        Node typeArguments = (Node) getFirstChild().getNextSibling().getFirstChild();
        assertThat(getSource(typeArguments), is("String"));
        assertThat(getSource((Node) typeArguments.getFirstChild()), is("String"));
    }

    @Test
    public void fieldDeclarationWithQualifiedTypeParameterShouldBeConverted() throws Exception {
        fSnippet = "List<Foo.Bar> fList;";
        prepareCompilation();
        transformField("fList");
        assertThat(getTreeString(), is("fList { ,List {  { Foo.Bar } } }"));
        Node typeArguments = (Node) getFirstChild().getNextSibling().getFirstChild();
        assertThat(getSource(typeArguments), is("Foo.Bar"));
        assertThat(getSource((Node) typeArguments.getFirstChild()), is("Foo.Bar"));
    }

    @Test
    public void fieldDeclarationWithParameterizedQualifiedTypeParameterShouldBeConverted() throws Exception {
        fSnippet = "List<Foo<T>.Bar> fList;";
        prepareCompilation();
        transformField("fList");
        assertThat(getTreeString(), is("fList { ,List {  { Foo<T>.Bar } } }"));
        Node typeArguments = (Node) getFirstChild().getNextSibling().getFirstChild();
        assertThat(getSource(typeArguments), is("Foo<T>.Bar"));
        assertThat(getSource((Node) typeArguments.getFirstChild()), is("Foo<T>.Bar"));
    }

    @Test
    public void fieldDeclarationWithQualifiedParameterizedTypeParameterShouldBeConverted() throws Exception {
        fSnippet = "List<Foo.Bar<T>> fList;";
        prepareCompilation();
        transformField("fList");
        assertThat(getTreeString(), is("fList { ,List {  { Foo.Bar {  { T } } } } }"));
        Node typeArguments = (Node) getFirstChild().getNextSibling().getFirstChild();
        assertThat(getSource(typeArguments), is("Foo.Bar<T>"));
        assertThat(getSource((Node) typeArguments.getFirstChild()), is("Foo.Bar<T>"));
    }

    @Test
    public void fieldDeclarationWithParameterizedTypeParameterShouldBeConverted() throws Exception {
        fSnippet = "List<Bar<T>> fList;";
        prepareCompilation();
        transformField("fList");
        assertThat(getTreeString(), is("fList { ,List {  { Bar {  { T } } } } }"));
        Node typeArguments = (Node) getFirstChild().getNextSibling().getFirstChild();
        assertThat(getSource(typeArguments), is("Bar<T>"));
        assertThat(getSource((Node) typeArguments.getFirstChild()), is("Bar<T>"));
    }

    @Test
    public void fieldDeclarationWithMultipleTypeParametersShouldBeConverted() throws Exception {
        fSnippet = "Map<String, Integer> fList;";
        prepareCompilation();
        transformField("fList");
        assertThat(getTreeString(), is("fList { ,Map {  { String,Integer } } }"));
        Node typeArguments = (Node) getFirstChild().getNextSibling().getFirstChild();
        assertThat(getSource(typeArguments), is("String, Integer"));
        Node firstTypeArgument = (Node) typeArguments.getFirstChild();
        assertThat(getSource(firstTypeArgument), is("String"));
        assertThat(getSource((Node) firstTypeArgument.getNextSibling()), is("Integer"));
    }

    @Test
    public void fieldDeclarationWithWildcardShouldBeConverted() throws Exception {
        fSnippet = "List<?> fList;";
        prepareCompilation();
        transformField("fList");
        assertThat(getTreeString(), is("fList { ,List {  {  } } }"));
    }

    @Test
    public void fieldDeclarationWithUpperBoundedWildcardShouldBeConverted() throws Exception {
        fSnippet = "List<? super Number> fList;";
        prepareCompilation();
        transformField("fList");
        assertThat(getTreeString(), is("fList { ,List {  { super { Number } } } }"));
        assertWildcardTypeArgumentCorrectness("super", "Number");
    }

    @Test
    public void fieldDeclarationWithLowerBoundedWildcardShouldBeConverted() throws Exception {
        fSnippet = "List<? extends Number> fList;";
        prepareCompilation();
        transformField("fList");
        assertThat(getTreeString(), is("fList { ,List {  { extends { Number } } } }"));
        assertWildcardTypeArgumentCorrectness("extends", "Number");
    }

    @Test
    public void fieldDeclarationCompleteShouldBeConverted() throws Exception {
        fSnippet = "public final Map<? extends Number, String> fMap;";
        prepareCompilation();
        transformField("fMap");
        assertThat(getTreeString(), is("fMap {  { public,final },Map {  { extends { Number },String } } }"));
    }

    @Test
    public void fieldDeclarationWithJavadocShouldBeConverted() throws Exception {
        fSnippet = "/**\n * A field\n */\nString aString;";
        prepareCompilation();
        transformField("aString");
        assertThat(getTreeString(), is("aString { /**\n * A field\n */,,String }"));
    }

    @Test
    public void fieldDeclarationWithEmptyJavadocShouldBeConvertedWithoutJavadoc() throws Exception {
        fSnippet = "/**\n *\n *\n */\nString aString;";
        prepareCompilation();
        transformField("aString");
        assertThat(getTreeString(), is("aString { ,String }"));
    }

    @Test
    public void methodDeclarationShouldBeConverted() throws Exception {
        fSnippet = "public void method() {}";
        prepareCompilation();
        transformMethod("method");
        assertThat(getTreeString(), is("method {  { public },method: void,,, }"));
    }

    @Test
    public void methodDeclarationWithReturnTypeShouldBeConverted() throws Exception {
        fSnippet = "public int method() {}";
        prepareCompilation();
        transformMethod("method");
        assertThat(getTreeString(), is("method {  { public },method: int,,, }"));
        Node returnType = (Node) getFirstChild().getNextSibling();
        assertThat(getSource(returnType), is("int"));
    }

    // TODO TC
    // - method declaration with parameterized return type
    // - method declaration with parameters
    // - method declaration with type arguments
    // - method declaration with javadoc
    // - constructor declaration ...
    // - type declaration ...
    // - source code entity types

    private void assertWildcardTypeArgumentCorrectness(String bound, String type) {
        String wildcard = "? " + bound + " " + type;
        Node typeArguments = (Node) getFirstChild().getNextSibling().getFirstChild();
        assertThat(getSource(typeArguments), is(wildcard));
        Node firstTypeArgument = (Node) typeArguments.getFirstChild();
        assertThat(firstTypeArgument.getValue(), is(bound));
        assertThat(getSource(firstTypeArgument), is(wildcard));
        assertThat(getSource((Node) firstTypeArgument.getFirstChild()), is(type));
    }

    @SuppressWarnings("unchecked")
    private void assertModifiersCorrectness(Node modifiers, String... modifierNames) {
        String concatedModifiers = "";
        for (String modifier : modifierNames) {
            concatedModifiers += modifier + ' ';
        }
        concatedModifiers = concatedModifiers.trim();
        assertThat(getSource(modifiers), is(concatedModifiers));
        int i = 0;
        for (Enumeration<Node> e = modifiers.children(); e.hasMoreElements(); i++) {
            assertThat(getSource(e.nextElement()), is(modifierNames[i]));
        }
    }

    private String getSource(Node node) {
        SourceCodeEntity entity = node.getEntity();
        return fCompilation.getSource().substring(entity.getStartPosition(), entity.getEndPosition() + 1);
    }

    @Override
    protected String getSourceCodeWithSnippets(String... sourceSnippets) {
        StringBuilder src = new StringBuilder("public class Foo { ");
        for (String statement : sourceSnippets) {
            src.append(statement).append(' ');
        }
        src.append("}");
        return src.toString();
    }

    private void transformField(String name) {
        fRoot = new Node(new SourceCodeEntity(name, JavaEntityType.FIELD_DECLARATION, new SourceRange()));
        FieldDeclaration field = CompilationUtils.findField(fCompilation.getCompilationUnit(), name);
        field.traverse(getDeclarationTransformer(), (MethodScope) null);
    }

    private JavaASTDeclarationTransformer getDeclarationTransformer() {
        return new JavaASTDeclarationTransformer(fRoot, fCompilation.getScanner(), new JavaASTHelper());
    }

    private void transformMethod(String name) {
        fRoot = new Node(new SourceCodeEntity(name, JavaEntityType.METHOD_DECLARATION, new SourceRange()));
        AbstractMethodDeclaration method = CompilationUtils.findMethod(fCompilation.getCompilationUnit(), name);
        method.traverse(getDeclarationTransformer(), (ClassScope) null);
    }

}
