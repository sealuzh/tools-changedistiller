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

import static org.hamcrest.CoreMatchers.is;

import java.util.Enumeration;

import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.junit.Test;

import ch.uzh.ifi.seal.changedistiller.ast.java.JavaDeclarationConverter;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import ch.uzh.ifi.seal.changedistiller.util.CompilationUtils;

public class WhenDeclarationsAreConverted extends WhenASTsAreConverted {

    @Test
    public void fieldDeclarationShouldBeConverted() throws Exception {
        fSnippet = "private String fField;";
        prepareCompilation();
        convertField("fField");
        assertThat(getTreeString(), is("fField {  { private },String }"));
        assertModifiersCorrectness(getFirstChild(), "private");
        Node type = getFieldType();
        assertThat(getSource(type), is("String"));
        assertThat(type.getLabel(), is(JavaEntityType.SINGLE_TYPE));
    }

    @Test
    public void fieldDeclarationWithInitializerShouldBeConverted() throws Exception {
        fSnippet = "private String fField = \"aString\";";
        prepareCompilation();
        convertField("fField");
        assertThat(getTreeString(), is("fField {  { private },String,\"aString\" }"));
        Node initializer = getLastChild();
        assertThat(getSource(initializer), is("\"aString\""));
        assertThat(initializer.getLabel(), is(JavaEntityType.STRING_LITERAL));
    }

    @Test
    public void fieldDeclarationWithMultiModifiersShouldBeConverted() throws Exception {
        fSnippet = "private final String fField;";
        prepareCompilation();
        convertField("fField");
        assertThat(getTreeString(), is("fField {  { private,final },String }"));
        assertModifiersCorrectness(getFirstChild(), "private", "final");
    }

    @Test
    public void fieldDeclarationWithTypeParameterShouldBeConverted() throws Exception {
        fSnippet = "List<String> fList;";
        prepareCompilation();
        convertField("fList");
        assertThat(getTreeString(), is("fList { ,List<String> }"));
        Node type = getFieldType();
        assertThat(getSource(type), is("List<String>"));
        assertThat(type.getLabel(), is(JavaEntityType.PARAMETERIZED_TYPE));
    }

    @Test
    public void fieldDeclarationWithQualifiedTypeParameterShouldBeConverted() throws Exception {
        fSnippet = "List<Foo.Bar> fList;";
        prepareCompilation();
        convertField("fList");
        assertThat(getTreeString(), is("fList { ,List<Foo.Bar> }"));
        Node type = getFieldType();
        assertThat(getSource(type), is("List<Foo.Bar>"));
        assertThat(type.getLabel(), is(JavaEntityType.PARAMETERIZED_TYPE));
    }

    private Node getFieldType() {
        return (Node) getFirstChild().getNextSibling();
    }

    private Node getReturnType() {
        return getFieldType();
    }

    private Node getTypeParameters() {
        return getFieldType();
    }

    @Test
    public void fieldDeclarationWithParameterizedQualifiedTypeParameterShouldBeConverted() throws Exception {
        fSnippet = "List<Foo<T>.Bar> fList;";
        prepareCompilation();
        convertField("fList");
        assertThat(getTreeString(), is("fList { ,List<Foo<T>.Bar> }"));
        Node type = getFieldType();
        assertThat(getSource(type), is("List<Foo<T>.Bar>"));
        assertThat(type.getLabel(), is(JavaEntityType.PARAMETERIZED_TYPE));
    }

    @Test
    public void fieldDeclarationWithQualifiedParameterizedTypeParameterShouldBeConverted() throws Exception {
        fSnippet = "List<Foo.Bar<T>> fList;";
        prepareCompilation();
        convertField("fList");
        assertThat(getTreeString(), is("fList { ,List<Foo.Bar<T>> }"));
        Node type = getFieldType();
        assertThat(getSource(type), is("List<Foo.Bar<T>>"));
        assertThat(type.getLabel(), is(JavaEntityType.PARAMETERIZED_TYPE));
    }

    @Test
    public void fieldDeclarationWithParameterizedTypeParameterShouldBeConverted() throws Exception {
        fSnippet = "List<Bar<T>> fList;";
        prepareCompilation();
        convertField("fList");
        assertThat(getTreeString(), is("fList { ,List<Bar<T>> }"));
        Node type = getFieldType();
        assertThat(getSource(type), is("List<Bar<T>>"));
        assertThat(type.getLabel(), is(JavaEntityType.PARAMETERIZED_TYPE));
    }

    @Test
    public void fieldDeclarationWithMultipleTypeParametersShouldBeConverted() throws Exception {
        fSnippet = "Map<String, Integer> fList;";
        prepareCompilation();
        convertField("fList");
        assertThat(getTreeString(), is("fList { ,Map<String, Integer> }"));
        Node type = getFieldType();
        assertThat(getSource(type), is("Map<String, Integer>"));
        assertThat(type.getLabel(), is(JavaEntityType.PARAMETERIZED_TYPE));
    }

    @Test
    public void fieldDeclarationWithWildcardShouldBeConverted() throws Exception {
        fSnippet = "List<?> fList;";
        prepareCompilation();
        convertField("fList");
        assertThat(getTreeString(), is("fList { ,List<?> }"));
        Node type = getFieldType();
        assertThat(getSource(type), is("List<?>"));
        assertThat(type.getLabel(), is(JavaEntityType.PARAMETERIZED_TYPE));
    }

    @Test
    public void fieldDeclarationWithUpperBoundedWildcardShouldBeConverted() throws Exception {
        fSnippet = "List<? super Number> fList;";
        prepareCompilation();
        convertField("fList");
        assertThat(getTreeString(), is("fList { ,List<? super Number> }"));
        Node type = getFieldType();
        assertThat(getSource(type), is("List<? super Number>"));
        assertThat(type.getLabel(), is(JavaEntityType.PARAMETERIZED_TYPE));
    }

    @Test
    public void fieldDeclarationWithLowerBoundedWildcardShouldBeConverted() throws Exception {
        fSnippet = "List<? extends Number> fList;";
        prepareCompilation();
        convertField("fList");
        assertThat(getTreeString(), is("fList { ,List<? extends Number> }"));
        Node type = getFieldType();
        assertThat(getSource(type), is("List<? extends Number>"));
        assertThat(type.getLabel(), is(JavaEntityType.PARAMETERIZED_TYPE));
    }

    @Test
    public void fieldDeclarationCompleteShouldBeConverted() throws Exception {
        fSnippet = "public final Map<? extends Number, String> fMap;";
        prepareCompilation();
        convertField("fMap");
        assertThat(getTreeString(), is("fMap {  { public,final },Map<? extends Number, String> }"));
    }

    @Test
    public void fieldDeclarationWithJavadocShouldBeConverted() throws Exception {
        fSnippet = "/**\n * A field\n */\nString aString;";
        prepareCompilation();
        convertField("aString");
        assertThat(getTreeString(), is("aString { /**\n * A field\n */,,String }"));
        Node javadoc = getFirstChild();
        assertThat(getSource(javadoc), is("/**\n * A field\n */"));
        assertThat(javadoc.getLabel(), is(JavaEntityType.JAVADOC));
    }

    @Test
    public void fieldDeclarationWithEmptyJavadocShouldBeConvertedWithoutJavadoc() throws Exception {
        fSnippet = "/**\n *\n *\n */\nString aString;";
        prepareCompilation();
        convertField("aString");
        assertThat(getTreeString(), is("aString { ,String }"));
    }

    @Test
    public void methodDeclarationShouldBeConverted() throws Exception {
        fSnippet = "public void method() {}";
        prepareCompilation();
        convertMethod("method");
        assertThat(getTreeString(), is("method {  { public },method: void,,, }"));
        assertModifiersCorrectness(getFirstChild(), "public");
    }

    @Test
    public void methodDeclarationWithReturnTypeShouldBeConverted() throws Exception {
        fSnippet = "public int method() {}";
        prepareCompilation();
        convertMethod("method");
        assertThat(getTreeString(), is("method {  { public },method: int,,, }"));
        Node returnType = getReturnType();
        assertThat(getSource(returnType), is("int"));
        assertThat(returnType.getLabel(), is(JavaEntityType.SINGLE_TYPE));
    }

    @Test
    public void methodDeclarationWithQualifiedReturnTypeShouldBeConverted() throws Exception {
        fSnippet = "public org.Foo method() {}";
        prepareCompilation();
        convertMethod("method");
        assertThat(getTreeString(), is("method {  { public },method: org.Foo,,, }"));
        Node returnType = getReturnType();
        assertThat(getSource(returnType), is("org.Foo"));
        assertThat(returnType.getLabel(), is(JavaEntityType.QUALIFIED_TYPE));
    }

    @Test
    public void methodDeclarationWithParameterizedReturnTypeShouldBeConverted() throws Exception {
        fSnippet = "List<String> method() {}";
        prepareCompilation();
        convertMethod("method");
        assertThat(getTreeString(), is("method { ,method: List<String>,,, }"));
        Node returnType = getReturnType();
        assertThat(getSource(returnType), is("List<String>"));
        assertThat(returnType.getLabel(), is(JavaEntityType.PARAMETERIZED_TYPE));
    }

    @Test
    public void methodDeclarationWithOneParameterShouldBeConverted() throws Exception {
        fSnippet = "public void method(int anInteger) {}";
        prepareCompilation();
        convertMethod("method");
        assertThat(getTreeString(), is("method {  { public },method: void,, { anInteger { anInteger: int } }, }"));
        Node parameters = (Node) getLastChild().getPreviousSibling();
        assertThat(getSource(parameters), is("int anInteger"));
        assertThat(parameters.getLabel(), is(JavaEntityType.PARAMETERS));
        Node parameter = (Node) parameters.getFirstChild();
        assertThat(getSource(parameter), is("anInteger"));
        assertThat(parameter.getLabel(), is(JavaEntityType.PARAMETER));
        Node parameterType = (Node) parameters.getFirstLeaf();
        assertThat(getSource(parameterType), is("int"));
        assertThat(parameterType.getLabel(), is(JavaEntityType.SINGLE_TYPE));
    }

    @Test
    public void methodDeclarationWithParametersShouldBeConverted() throws Exception {
        fSnippet = "public void method(int anInteger, List<String> aList) {}";
        prepareCompilation();
        convertMethod("method");
        assertThat(
                getTreeString(),
                is("method {  { public },method: void,, { anInteger { anInteger: int },aList { aList: List<String> } }, }"));
        Node parameters = (Node) getLastChild().getPreviousSibling();
        assertThat(getSource(parameters), is("int anInteger, List<String> aList"));
        assertThat(parameters.getLabel(), is(JavaEntityType.PARAMETERS));
        Node firstParameter = (Node) parameters.getFirstChild();
        assertThat(getSource(firstParameter), is("anInteger"));
        assertThat(firstParameter.getLabel(), is(JavaEntityType.PARAMETER));
        Node firstParameterType = (Node) parameters.getFirstLeaf();
        assertThat(getSource(firstParameterType), is("int"));
        assertThat(firstParameterType.getLabel(), is(JavaEntityType.SINGLE_TYPE));
        Node secondParameter = (Node) firstParameter.getNextSibling();
        assertThat(getSource(secondParameter), is("aList"));
        assertThat(secondParameter.getLabel(), is(JavaEntityType.PARAMETER));
        Node secondParameterType = (Node) secondParameter.getFirstLeaf();
        assertThat(getSource(secondParameterType), is("List<String>"));
        assertThat(secondParameterType.getLabel(), is(JavaEntityType.PARAMETERIZED_TYPE));
    }

    @Test
    public void methodDeclarationWithTypeArgumentShouldBeConverted() throws Exception {
        fSnippet = "public <T> void method() {}";
        prepareCompilation();
        convertMethod("method");
        assertThat(getTreeString(), is("method {  { public },method: void, { T },, }"));
        Node typeArguments = (Node) getLastChild().getPreviousSibling().getPreviousSibling();
        assertThat(getSource(typeArguments), is("T"));
        assertThat(typeArguments.getLabel(), is(JavaEntityType.TYPE_PARAMETERS));
        Node typeArgument = (Node) typeArguments.getFirstLeaf();
        assertThat(getSource(typeArgument), is("T"));
        assertThat(typeArgument.getLabel(), is(JavaEntityType.TYPE_PARAMETER));
    }

    @Test
    public void methodDeclarationWithMultipleTypeArgumentsShouldBeConverted() throws Exception {
        fSnippet = "public <T,U> void method() {}";
        prepareCompilation();
        convertMethod("method");
        assertThat(getTreeString(), is("method {  { public },method: void, { T,U },, }"));
        Node typeArguments = (Node) getLastChild().getPreviousSibling().getPreviousSibling();
        assertThat(getSource(typeArguments), is("T,U"));
        assertThat(typeArguments.getLabel(), is(JavaEntityType.TYPE_PARAMETERS));
        Node firstTypeArgument = (Node) typeArguments.getFirstLeaf();
        assertThat(getSource(firstTypeArgument), is("T"));
        assertThat(firstTypeArgument.getLabel(), is(JavaEntityType.TYPE_PARAMETER));
        Node secondTypeArgument = (Node) firstTypeArgument.getNextSibling();
        assertThat(getSource(secondTypeArgument), is("U"));
        assertThat(secondTypeArgument.getLabel(), is(JavaEntityType.TYPE_PARAMETER));
    }

    @Test
    public void methodDeclarationWithBoundedTypeArgumentShouldBeConverted() throws Exception {
        fSnippet = "public <T extends Number> void method() {}";
        prepareCompilation();
        convertMethod("method");
        assertThat(getTreeString(), is("method {  { public },method: void, { T extends Number },, }"));
        Node typeArguments = (Node) getLastChild().getPreviousSibling().getPreviousSibling();
        assertThat(typeArguments.getLabel(), is(JavaEntityType.TYPE_PARAMETERS));
        assertThat(getSource(typeArguments), is("T extends Number"));
        Node typeArgument = (Node) typeArguments.getFirstLeaf();
        assertThat(getSource(typeArgument), is("T extends Number"));
        assertThat(typeArgument.getLabel(), is(JavaEntityType.TYPE_PARAMETER));
    }

    @Test
    public void methodDeclarationWithMultipleBoundedTypeArgumentShouldBeConverted() throws Exception {
        fSnippet = "public <T extends Number & Serializable> void method() {}";
        prepareCompilation();
        convertMethod("method");
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
        convertMethod("method");
        assertThat(getTreeString(), is("method { ,method: void,,, { IOException } }"));
        Node exceptions = getLastChild();
        assertThat(getSource(exceptions), is("IOException"));
        assertThat(exceptions.getLabel(), is(JavaEntityType.THROW));
        Node exception = (Node) exceptions.getFirstLeaf();
        assertThat(getSource(exception), is("IOException"));
        assertThat(exception.getLabel(), is(JavaEntityType.SINGLE_TYPE));
    }

    @Test
    public void methodDeclarationWithMultipleThrowsShouldBeConverted() throws Exception {
        fSnippet = "void method() throws IOException, OutOfBoundException {}";
        prepareCompilation();
        convertMethod("method");
        assertThat(getTreeString(), is("method { ,method: void,,, { IOException,OutOfBoundException } }"));
        Node exceptions = getLastChild();
        assertThat(getSource(exceptions), is("IOException, OutOfBoundException"));
        Node firstException = (Node) exceptions.getFirstLeaf();
        assertThat(exceptions.getLabel(), is(JavaEntityType.THROW));
        assertThat(getSource(firstException), is("IOException"));
        assertThat(firstException.getLabel(), is(JavaEntityType.SINGLE_TYPE));
        Node secondException = (Node) firstException.getNextSibling();
        assertThat(getSource(secondException), is("OutOfBoundException"));
        assertThat(secondException.getLabel(), is(JavaEntityType.SINGLE_TYPE));
    }

    @Test
    public void methodDeclarationCompleteShouldBeConverted() throws Exception {
        fSnippet = "protected final <T> String method(List<T> aList, int anInteger) throws IOException";
        prepareCompilation();
        convertMethod("method");
        assertThat(
                getTreeString(),
                is("method {  { protected,final },method: String, { T }, { aList { aList: List<T> },anInteger { anInteger: int } }, { IOException } }"));
    }

    @Test
    public void methodDeclarationWithJavadocShouldBeConverted() throws Exception {
        fSnippet = "/**\n * A method\n */\nvoid method() {}";
        prepareCompilation();
        convertMethod("method");
        assertThat(getTreeString(), is("method { /**\n * A method\n */,,method: void,,, }"));
        Node javadoc = getFirstChild();
        assertThat(getSource(javadoc), is("/**\n * A method\n */"));
        assertThat(javadoc.getLabel(), is(JavaEntityType.JAVADOC));
    }

    @Test
    public void methodDeclarationWithEmptyJavadocShouldBeConvertedWithoutJavadoc() throws Exception {
        fSnippet = "/**\n *\n *\n */\nvoid method() {}";
        prepareCompilation();
        convertMethod("method");
        assertThat(getTreeString(), is("method { ,method: void,,, }"));
    }

    @Test
    public void constructorDeclarationShouldBeConverted() throws Exception {
        fSnippet = "public Foo() {}";
        prepareCompilation();
        convertMethod("Foo");
        assertThat(getTreeString(), is("Foo {  { public },,, }"));
    }

    @Test
    public void typeDeclarationShouldBeConverted() throws Exception {
        fSnippet = "public class Bar {}";
        prepareCompilation();
        convertClass("Bar");
        assertThat(getTreeString(), is("Bar {  { public },, }"));
        assertModifiersCorrectness(getFirstChild(), "public");
    }

    @Test
    public void abstractTypeDeclarationShouldBeConverted() throws Exception {
    	fSnippet = "abstract class Bar {}";
    	prepareCompilation();
    	convertClass("Bar");
    	assertThat(getTreeString(), is("Bar {  { abstract },, }"));
    	assertModifiersCorrectness(getFirstChild(), "abstract");
    }
    
    @Test
    public void publicAbstractTypeDeclarationShouldBeConverted() throws Exception {
    	fSnippet = "public abstract class Bar {}";
    	prepareCompilation();
    	convertClass("Bar");
    	assertThat(getTreeString(), is("Bar {  { public,abstract },, }"));
    	assertModifiersCorrectness(getFirstChild(), "public", "abstract");
    }

    @Test
    public void typeDeclarationWithTypeArgumentShouldBeConverted() throws Exception {
        fSnippet = "class Bar<T> {}";
        prepareCompilation();
        convertClass("Bar");
        assertThat(getTreeString(), is("Bar { , { T }, }"));
        Node typeArguments = getTypeParameters();
        assertThat(getSource(typeArguments), is("T"));
        assertThat(typeArguments.getLabel(), is(JavaEntityType.TYPE_PARAMETERS));
        Node typeArgument = (Node) typeArguments.getFirstLeaf();
        assertThat(getSource(typeArgument), is("T"));
        assertThat(typeArgument.getLabel(), is(JavaEntityType.TYPE_PARAMETER));
    }

    @Test
    public void typeDeclarationWithMultipleTypeArgumentsShouldBeConverted() throws Exception {
        fSnippet = "class Bar<T,U> {}";
        prepareCompilation();
        convertClass("Bar");
        assertThat(getTreeString(), is("Bar { , { T,U }, }"));
        Node typeArguments = getTypeParameters();
        assertThat(getSource(typeArguments), is("T,U"));
        assertThat(typeArguments.getLabel(), is(JavaEntityType.TYPE_PARAMETERS));
        Node firstTypeArgument = (Node) typeArguments.getFirstLeaf();
        assertThat(getSource(firstTypeArgument), is("T"));
        assertThat(firstTypeArgument.getLabel(), is(JavaEntityType.TYPE_PARAMETER));
        Node secondTypeArgument = (Node) firstTypeArgument.getNextSibling();
        assertThat(getSource(secondTypeArgument), is("U"));
        assertThat(secondTypeArgument.getLabel(), is(JavaEntityType.TYPE_PARAMETER));
    }

    @Test
    public void typeDeclarationWithBoundedTypeArgumentShouldBeConverted() throws Exception {
        fSnippet = "class Bar<T extends Number> {}";
        prepareCompilation();
        convertClass("Bar");
        assertThat(getTreeString(), is("Bar { , { T extends Number }, }"));
        Node typeArguments = getTypeParameters();
        assertThat(typeArguments.getLabel(), is(JavaEntityType.TYPE_PARAMETERS));
        assertThat(getSource(typeArguments), is("T extends Number"));
        Node typeArgument = (Node) typeArguments.getFirstLeaf();
        assertThat(getSource(typeArgument), is("T extends Number"));
        assertThat(typeArgument.getLabel(), is(JavaEntityType.TYPE_PARAMETER));
    }

    @Test
    public void typeDeclarationWithMultipleBoundedTypeArgumentShouldBeConverted() throws Exception {
        fSnippet = "class Bar<T extends Number & Serializable> {}";
        prepareCompilation();
        convertClass("Bar");
        assertThat(getTreeString(), is("Bar { , { T extends Number & Serializable }, }"));
        Node typeArguments = getTypeParameters();
        assertThat(getSource(typeArguments), is("T extends Number & Serializable"));
        assertThat(typeArguments.getLabel(), is(JavaEntityType.TYPE_PARAMETERS));
        Node typeArgument = (Node) typeArguments.getFirstLeaf();
        assertThat(getSource(typeArgument), is("T extends Number & Serializable"));
        assertThat(typeArgument.getLabel(), is(JavaEntityType.TYPE_PARAMETER));
    }

    @Test
    public void typeDeclarationWithSuperTypeShouldBeConverted() throws Exception {
        fSnippet = "class Bar extends Number {}";
        prepareCompilation();
        convertClass("Bar");
        assertThat(getTreeString(), is("Bar { ,,Number, }"));
        Node superType = (Node) getLastChild().getPreviousSibling();
        assertThat(getSource(superType), is("Number"));
        assertThat(superType.getLabel(), is(JavaEntityType.SINGLE_TYPE));
    }

    @Test
    public void typeDeclarationWithSuperInterfaceShouldBeConverted() throws Exception {
        fSnippet = "class Bar implements Number {}";
        prepareCompilation();
        convertClass("Bar");
        assertThat(getTreeString(), is("Bar { ,, { Number } }"));
        Node superInterfaces = getLastChild();
        assertThat(getSource(superInterfaces), is("Number"));
        assertThat(superInterfaces.getLabel(), is(JavaEntityType.SUPER_INTERFACE_TYPES));
        Node superInterface = (Node) superInterfaces.getFirstLeaf();
        assertThat(getSource(superInterface), is("Number"));
        assertThat(superInterface.getLabel(), is(JavaEntityType.SINGLE_TYPE));
    }

    @Test
    public void typeDeclarationWithMultipleSuperInterfacesShouldBeConverted() throws Exception {
        fSnippet = "class Bar implements Number, Serializable {}";
        prepareCompilation();
        convertClass("Bar");
        assertThat(getTreeString(), is("Bar { ,, { Number,Serializable } }"));
        Node superInterfaces = getLastChild();
        assertThat(getSource(superInterfaces), is("Number, Serializable"));
        assertThat(superInterfaces.getLabel(), is(JavaEntityType.SUPER_INTERFACE_TYPES));
        Node firstSuperInterface = (Node) superInterfaces.getFirstLeaf();
        assertThat(getSource(firstSuperInterface), is("Number"));
        assertThat(firstSuperInterface.getLabel(), is(JavaEntityType.SINGLE_TYPE));
        Node secondSuperInterface = (Node) firstSuperInterface.getNextSibling();
        assertThat(getSource(secondSuperInterface), is("Serializable"));
        assertThat(secondSuperInterface.getLabel(), is(JavaEntityType.SINGLE_TYPE));
    }

    @Test
    public void typeDeclarationWithJavadocShouldBeConverted() throws Exception {
        fSnippet = "/**\n * A class\n */\nclass Bar {}";
        prepareCompilation();
        convertClass("Bar");
        assertThat(getTreeString(), is("Bar { /**\n * A class\n */,,, }"));
        Node javadoc = getFirstChild();
        assertThat(getSource(javadoc), is("/**\n * A class\n */"));
        assertThat(javadoc.getLabel(), is(JavaEntityType.JAVADOC));
    }

    @Test
    public void typeDeclarationWithEmptyJavadocShouldBeConvertedWithoutJavadoc() throws Exception {
        fSnippet = "/**\n *\n *\n */\nclass Bar {}";
        prepareCompilation();
        convertClass("Bar");
        assertThat(getTreeString(), is("Bar { ,, }"));
    }

    @Test
    public void typeDeclarationCompleteShouldBeConverted() throws Exception {
        fSnippet = "protected final class Bar<T> extends Number implements Serializable, Throwable {}";
        prepareCompilation();
        convertClass("Bar");
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
        assertThat(modifiers.getLabel(), is(JavaEntityType.MODIFIERS));
        int i = 0;
        for (Enumeration<Node> e = modifiers.children(); e.hasMoreElements(); i++) {
            Node modifier = e.nextElement();
            assertThat(getSource(modifier), is(modifierNames[i]));
            assertThat(modifier.getLabel(), is(JavaEntityType.MODIFIER));
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

    private void convertField(String name) {
        createRootNode(JavaEntityType.FIELD_DECLARATION, name);
        FieldDeclaration field = CompilationUtils.findField(fCompilation.getCompilationUnit(), name);
        field.traverse(getDeclarationconverter(), (MethodScope) null);
    }

    private JavaDeclarationConverter getDeclarationconverter() {
        sDeclarationConverter.initialize(fRoot, fCompilation.getScanner());
        return sDeclarationConverter;
    }

    private void convertMethod(String name) {
        createRootNode(JavaEntityType.METHOD_DECLARATION, name);
        AbstractMethodDeclaration method = CompilationUtils.findMethod(fCompilation.getCompilationUnit(), name);
        method.traverse(getDeclarationconverter(), (ClassScope) null);
    }

    private void convertClass(String name) {
        createRootNode(JavaEntityType.METHOD_DECLARATION, name);
        TypeDeclaration type = CompilationUtils.findType(fCompilation.getCompilationUnit(), name);
        type.traverse(getDeclarationconverter(), (CompilationUnitScope) null);
    }

}
