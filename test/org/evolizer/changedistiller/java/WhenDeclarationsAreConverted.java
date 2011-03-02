package org.evolizer.changedistiller.java;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Enumeration;

import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
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
        assertThat(getTreeString(), is("fList { ,List<String> }"));
        Node type = (Node) getFirstChild().getNextSibling();
        assertThat(getSource(type), is("List<String>"));
    }

    @Test
    public void fieldDeclarationWithQualifiedTypeParameterShouldBeConverted() throws Exception {
        fSnippet = "List<Foo.Bar> fList;";
        prepareCompilation();
        transformField("fList");
        assertThat(getTreeString(), is("fList { ,List<Foo.Bar> }"));
        Node type = (Node) getFirstChild().getNextSibling();
        assertThat(getSource(type), is("List<Foo.Bar>"));
    }

    @Test
    public void fieldDeclarationWithParameterizedQualifiedTypeParameterShouldBeConverted() throws Exception {
        fSnippet = "List<Foo<T>.Bar> fList;";
        prepareCompilation();
        transformField("fList");
        assertThat(getTreeString(), is("fList { ,List<Foo<T>.Bar> }"));
        Node type = (Node) getFirstChild().getNextSibling();
        assertThat(getSource(type), is("List<Foo<T>.Bar>"));
    }

    @Test
    public void fieldDeclarationWithQualifiedParameterizedTypeParameterShouldBeConverted() throws Exception {
        fSnippet = "List<Foo.Bar<T>> fList;";
        prepareCompilation();
        transformField("fList");
        assertThat(getTreeString(), is("fList { ,List<Foo.Bar<T>> }"));
        Node type = (Node) getFirstChild().getNextSibling();
        assertThat(getSource(type), is("List<Foo.Bar<T>>"));
    }

    @Test
    public void fieldDeclarationWithParameterizedTypeParameterShouldBeConverted() throws Exception {
        fSnippet = "List<Bar<T>> fList;";
        prepareCompilation();
        transformField("fList");
        assertThat(getTreeString(), is("fList { ,List<Bar<T>> }"));
        Node type = (Node) getFirstChild().getNextSibling();
        assertThat(getSource(type), is("List<Bar<T>>"));
    }

    @Test
    public void fieldDeclarationWithMultipleTypeParametersShouldBeConverted() throws Exception {
        fSnippet = "Map<String, Integer> fList;";
        prepareCompilation();
        transformField("fList");
        assertThat(getTreeString(), is("fList { ,Map<String, Integer> }"));
        Node type = (Node) getFirstChild().getNextSibling();
        assertThat(getSource(type), is("Map<String, Integer>"));
    }

    @Test
    public void fieldDeclarationWithWildcardShouldBeConverted() throws Exception {
        fSnippet = "List<?> fList;";
        prepareCompilation();
        transformField("fList");
        assertThat(getTreeString(), is("fList { ,List<?> }"));
        Node type = (Node) getFirstChild().getNextSibling();
        assertThat(getSource(type), is("List<?>"));
    }

    @Test
    public void fieldDeclarationWithUpperBoundedWildcardShouldBeConverted() throws Exception {
        fSnippet = "List<? super Number> fList;";
        prepareCompilation();
        transformField("fList");
        assertThat(getTreeString(), is("fList { ,List<? super Number> }"));
        Node type = (Node) getFirstChild().getNextSibling();
        assertThat(getSource(type), is("List<? super Number>"));
    }

    @Test
    public void fieldDeclarationWithLowerBoundedWildcardShouldBeConverted() throws Exception {
        fSnippet = "List<? extends Number> fList;";
        prepareCompilation();
        transformField("fList");
        assertThat(getTreeString(), is("fList { ,List<? extends Number> }"));
        Node type = (Node) getFirstChild().getNextSibling();
        assertThat(getSource(type), is("List<? extends Number>"));
    }

    @Test
    public void fieldDeclarationCompleteShouldBeConverted() throws Exception {
        fSnippet = "public final Map<? extends Number, String> fMap;";
        prepareCompilation();
        transformField("fMap");
        assertThat(getTreeString(), is("fMap {  { public,final },Map<? extends Number, String> }"));
    }

    @Test
    public void fieldDeclarationWithJavadocShouldBeConverted() throws Exception {
        fSnippet = "/**\n * A field\n */\nString aString;";
        prepareCompilation();
        transformField("aString");
        assertThat(getTreeString(), is("aString { /**\n * A field\n */,,String }"));
        Node javadoc = getFirstChild();
        assertThat(getSource(javadoc), is("/**\n * A field\n */"));
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
        Node modifiers = getFirstChild();
        assertThat(getSource(modifiers), is("public"));
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

    @Test
    public void methodDeclarationWithQualifiedReturnTypeShouldBeConverted() throws Exception {
        fSnippet = "public org.Foo method() {}";
        prepareCompilation();
        transformMethod("method");
        assertThat(getTreeString(), is("method {  { public },method: org.Foo,,, }"));
        Node returnType = (Node) getFirstChild().getNextSibling();
        assertThat(getSource(returnType), is("org.Foo"));
    }

    @Test
    public void methodDeclarationWithParameterizedReturnTypeShouldBeConverted() throws Exception {
        fSnippet = "List<String> method() {}";
        prepareCompilation();
        transformMethod("method");
        assertThat(getTreeString(), is("method { ,method: List<String>,,, }"));
    }

    @Test
    public void methodDeclarationWithOneParameterShouldBeConverted() throws Exception {
        fSnippet = "public void method(int anInteger) {}";
        prepareCompilation();
        transformMethod("method");
        assertThat(getTreeString(), is("method {  { public },method: void,, { anInteger { anInteger: int } }, }"));
        Node parameters = (Node) getLastChild().getPreviousSibling();
        assertThat(getSource(parameters), is("int anInteger"));
        Node parameter = (Node) parameters.getFirstChild();
        assertThat(getSource(parameter), is("anInteger"));
        Node parameterType = (Node) parameters.getFirstLeaf();
        assertThat(getSource(parameterType), is("int"));
    }

    @Test
    public void methodDeclarationWithParametersShouldBeConverted() throws Exception {
        fSnippet = "public void method(int anInteger, List<String> aList) {}";
        prepareCompilation();
        transformMethod("method");
        assertThat(
                getTreeString(),
                is("method {  { public },method: void,, { anInteger { anInteger: int },aList { aList: List<String> } }, }"));
        Node parameters = (Node) getLastChild().getPreviousSibling();
        assertThat(getSource(parameters), is("int anInteger, List<String> aList"));
        Node firstParameter = (Node) parameters.getFirstChild();
        assertThat(getSource(firstParameter), is("anInteger"));
        Node firstParameterType = (Node) parameters.getFirstLeaf();
        assertThat(getSource(firstParameterType), is("int"));
        Node secondParameter = (Node) firstParameter.getNextSibling();
        assertThat(getSource(secondParameter), is("aList"));
        Node secondParameterType = (Node) secondParameter.getFirstLeaf();
        assertThat(getSource(secondParameterType), is("List<String>"));
    }

    @Test
    public void methodDeclarationWithTypeArgumentShouldBeConverted() throws Exception {
        fSnippet = "public <T> void method() {}";
        prepareCompilation();
        transformMethod("method");
        assertThat(getTreeString(), is("method {  { public },method: void, { T },, }"));
        Node typeArguments = (Node) getLastChild().getPreviousSibling().getPreviousSibling();
        assertThat(getSource(typeArguments), is("T"));
        Node typeArgument = (Node) typeArguments.getFirstLeaf();
        assertThat(getSource(typeArgument), is("T"));
    }

    @Test
    public void methodDeclarationWithMultipleTypeArgumentsShouldBeConverted() throws Exception {
        fSnippet = "public <T,U> void method() {}";
        prepareCompilation();
        transformMethod("method");
        assertThat(getTreeString(), is("method {  { public },method: void, { T,U },, }"));
        Node typeArguments = (Node) getLastChild().getPreviousSibling().getPreviousSibling();
        assertThat(getSource(typeArguments), is("T,U"));
        Node firstTypeArgument = (Node) typeArguments.getFirstLeaf();
        assertThat(getSource(firstTypeArgument), is("T"));
        Node secondTypeArgument = (Node) firstTypeArgument.getNextSibling();
        assertThat(getSource(secondTypeArgument), is("U"));
    }

    @Test
    public void methodDeclarationWithBoundedTypeArgumentShouldBeConverted() throws Exception {
        fSnippet = "public <T extends Number> void method() {}";
        prepareCompilation();
        transformMethod("method");
        assertThat(getTreeString(), is("method {  { public },method: void, { T extends Number },, }"));
        Node typeArguments = (Node) getLastChild().getPreviousSibling().getPreviousSibling();
        assertThat(getSource(typeArguments), is("T extends Number"));
        Node typeArgument = (Node) typeArguments.getFirstLeaf();
        assertThat(getSource(typeArgument), is("T extends Number"));
    }

    @Test
    public void methodDeclarationWithMultipleBoundedTypeArgumentShouldBeConverted() throws Exception {
        fSnippet = "public <T extends Number & Serializable> void method() {}";
        prepareCompilation();
        transformMethod("method");
        assertThat(getTreeString(), is("method {  { public },method: void, { T extends Number & Serializable },, }"));
        Node typeArguments = (Node) getLastChild().getPreviousSibling().getPreviousSibling();
        assertThat(getSource(typeArguments), is("T extends Number & Serializable"));
        Node typeArgument = (Node) typeArguments.getFirstLeaf();
        assertThat(getSource(typeArgument), is("T extends Number & Serializable"));
    }

    @Test
    public void methodDeclarationWithOneThrowShouldBeConverted() throws Exception {
        fSnippet = "void method() throws IOException {}";
        prepareCompilation();
        transformMethod("method");
        assertThat(getTreeString(), is("method { ,method: void,,, { IOException } }"));
        Node exceptions = (Node) getLastChild();
        assertThat(getSource(exceptions), is("IOException"));
        Node exception = (Node) exceptions.getFirstLeaf();
        assertThat(getSource(exception), is("IOException"));
    }

    @Test
    public void methodDeclarationWithMultipleThrowsShouldBeConverted() throws Exception {
        fSnippet = "void method() throws IOException, OutOfBoundException {}";
        prepareCompilation();
        transformMethod("method");
        assertThat(getTreeString(), is("method { ,method: void,,, { IOException,OutOfBoundException } }"));
        Node exceptions = (Node) getLastChild();
        assertThat(getSource(exceptions), is("IOException, OutOfBoundException"));
        Node firstException = (Node) exceptions.getFirstLeaf();
        assertThat(getSource(firstException), is("IOException"));
        Node secondException = (Node) firstException.getNextSibling();
        assertThat(getSource(secondException), is("OutOfBoundException"));
    }

    @Test
    public void methodDeclarationCompleteShouldBeConverted() throws Exception {
        fSnippet = "protected final <T> String method(List<T> aList, int anInteger) throws IOException";
        prepareCompilation();
        transformMethod("method");
        assertThat(
                getTreeString(),
                is("method {  { protected,final },method: String, { T }, { aList { aList: List<T> },anInteger { anInteger: int } }, { IOException } }"));
    }

    @Test
    public void methodDeclarationWithJavadocShouldBeConverted() throws Exception {
        fSnippet = "/**\n * A method\n */\nvoid method() {}";
        prepareCompilation();
        transformMethod("method");
        assertThat(getTreeString(), is("method { /**\n * A method\n */,,method: void,,, }"));
        Node javadoc = (Node) getFirstChild();
        assertThat(getSource(javadoc), is("/**\n * A method\n */"));
    }

    @Test
    public void methodDeclarationWithEmptyJavadocShouldBeConvertedWithoutJavadoc() throws Exception {
        fSnippet = "/**\n *\n *\n */\nvoid method() {}";
        prepareCompilation();
        transformMethod("method");
        assertThat(getTreeString(), is("method { ,method: void,,, }"));
    }

    @Test
    public void constructorDeclarationShouldBeConverted() throws Exception {
        fSnippet = "public Foo() {}";
        prepareCompilation();
        transformMethod("Foo");
        assertThat(getTreeString(), is("Foo {  { public },,, }"));
    }

    @Test
    public void typeDeclarationShouldBeConverted() throws Exception {
        fSnippet = "public class Bar {}";
        prepareCompilation();
        transformClass("Bar");
        assertThat(getTreeString(), is("Bar {  { public },, }"));
        Node modifiers = (Node) getFirstChild();
        assertThat(getSource(modifiers), is("public"));
        Node modifier = (Node) modifiers.getFirstLeaf();
        assertThat(getSource(modifier), is("public"));
    }

    @Test
    public void typeDeclarationWithTypeArgumentShouldBeConverted() throws Exception {
        fSnippet = "class Bar<T> {}";
        prepareCompilation();
        transformClass("Bar");
        assertThat(getTreeString(), is("Bar { , { T }, }"));
        Node typeArguments = (Node) getFirstChild().getNextSibling();
        assertThat(getSource(typeArguments), is("T"));
        Node typeArgument = (Node) typeArguments.getFirstLeaf();
        assertThat(getSource(typeArgument), is("T"));
    }

    @Test
    public void typeDeclarationWithMultipleTypeArgumentsShouldBeConverted() throws Exception {
        fSnippet = "class Bar<T,U> {}";
        prepareCompilation();
        transformClass("Bar");
        assertThat(getTreeString(), is("Bar { , { T,U }, }"));
        Node typeArguments = (Node) getFirstChild().getNextSibling();
        assertThat(getSource(typeArguments), is("T,U"));
        Node firstTypeArgument = (Node) typeArguments.getFirstLeaf();
        assertThat(getSource(firstTypeArgument), is("T"));
        Node secondTypeArgument = (Node) firstTypeArgument.getNextSibling();
        assertThat(getSource(secondTypeArgument), is("U"));
    }

    @Test
    public void typeDeclarationWithBoundedTypeArgumentShouldBeConverted() throws Exception {
        fSnippet = "class Bar<T extends Number> {}";
        prepareCompilation();
        transformClass("Bar");
        assertThat(getTreeString(), is("Bar { , { T extends Number }, }"));
        Node typeArguments = (Node) getFirstChild().getNextSibling();
        assertThat(getSource(typeArguments), is("T extends Number"));
        Node typeArgument = (Node) typeArguments.getFirstLeaf();
        assertThat(getSource(typeArgument), is("T extends Number"));
    }

    @Test
    public void typeDeclarationWithMultipleBoundedTypeArgumentShouldBeConverted() throws Exception {
        fSnippet = "class Bar<T extends Number & Serializable> {}";
        prepareCompilation();
        transformClass("Bar");
        assertThat(getTreeString(), is("Bar { , { T extends Number & Serializable }, }"));
        Node typeArguments = (Node) getFirstChild().getNextSibling();
        assertThat(getSource(typeArguments), is("T extends Number & Serializable"));
        Node typeArgument = (Node) typeArguments.getFirstLeaf();
        assertThat(getSource(typeArgument), is("T extends Number & Serializable"));
    }

    @Test
    public void typeDeclarationWithSuperTypeShouldBeConverted() throws Exception {
        fSnippet = "class Bar extends Number {}";
        prepareCompilation();
        transformClass("Bar");
        assertThat(getTreeString(), is("Bar { ,,Number, }"));
        Node superType = (Node) getLastChild().getPreviousSibling();
        assertThat(getSource(superType), is("Number"));
    }

    @Test
    public void typeDeclarationWithSuperInterfaceShouldBeConverted() throws Exception {
        fSnippet = "class Bar implements Number {}";
        prepareCompilation();
        transformClass("Bar");
        assertThat(getTreeString(), is("Bar { ,, { Number } }"));
        Node superInterfaces = (Node) getLastChild();
        assertThat(getSource(superInterfaces), is("Number"));
        Node superInterface = (Node) superInterfaces.getFirstLeaf();
        assertThat(getSource(superInterface), is("Number"));
    }

    @Test
    public void typeDeclarationWithMultipleSuperInterfacesShouldBeConverted() throws Exception {
        fSnippet = "class Bar implements Number, Serializable {}";
        prepareCompilation();
        transformClass("Bar");
        assertThat(getTreeString(), is("Bar { ,, { Number,Serializable } }"));
        Node superInterfaces = (Node) getLastChild();
        assertThat(getSource(superInterfaces), is("Number, Serializable"));
        Node firstSuperInterface = (Node) superInterfaces.getFirstLeaf();
        assertThat(getSource(firstSuperInterface), is("Number"));
        Node secondSuperInterface = (Node) firstSuperInterface.getNextSibling();
        assertThat(getSource(secondSuperInterface), is("Serializable"));
    }

    @Test
    public void typeDeclarationWithJavadocShouldBeConverted() throws Exception {
        fSnippet = "/**\n * A class\n */\nclass Bar {}";
        prepareCompilation();
        transformClass("Bar");
        assertThat(getTreeString(), is("Bar { /**\n * A class\n */,,, }"));
        Node javadoc = (Node) getFirstChild();
        assertThat(getSource(javadoc), is("/**\n * A class\n */"));
    }

    @Test
    public void typeDeclarationWithEmptyJavadocShouldBeConvertedWithoutJavadoc() throws Exception {
        fSnippet = "/**\n *\n *\n */\nclass Bar {}";
        prepareCompilation();
        transformClass("Bar");
        assertThat(getTreeString(), is("Bar { ,, }"));
    }

    @Test
    public void typeDeclarationCompleteShouldBeConverted() throws Exception {
        fSnippet = "protected final class Bar<T> extends Number implements Serializable, Throwable {}";
        prepareCompilation();
        transformClass("Bar");
        assertThat(getTreeString(), is("Bar {  { protected,final }, { T },Number, { Serializable,Throwable } }"));
    }

    // - source code entity types

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

    private void transformClass(String name) {
        fRoot = new Node(new SourceCodeEntity(name, JavaEntityType.METHOD_DECLARATION, new SourceRange()));
        TypeDeclaration type = CompilationUtils.findType(fCompilation.getCompilationUnit(), name);
        type.traverse(getDeclarationTransformer(), (CompilationUnitScope) null);
    }

}
