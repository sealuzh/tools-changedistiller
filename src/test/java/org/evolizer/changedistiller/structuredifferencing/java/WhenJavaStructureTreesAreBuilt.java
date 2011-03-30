package org.evolizer.changedistiller.structuredifferencing.java;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.evolizer.changedistiller.compilation.java.JavaCompilation;
import org.evolizer.changedistiller.structuredifferencing.java.JavaStructureNode.Type;
import org.evolizer.changedistiller.util.CompilationUtils;
import org.junit.Test;

public class WhenJavaStructureTreesAreBuilt {

    private String fSnippet;
    private JavaStructureNode fRoot;

    @Test
    public void structureTreeWithClassShouldBeCreated() throws Exception {
        fSnippet = "class Clazz {}";
        createStructureTree();
        JavaStructureNode classNode = fRoot.getChildren().get(0);
        assertThat(classNode.getType(), is(Type.CLASS));
        assertNameCorrectness(classNode, "Clazz");
    }

    @Test
    public void structureTreeWithInterfaceShouldBeCreated() throws Exception {
        fSnippet = "interface Type {}";
        createStructureTree();
        JavaStructureNode classNode = fRoot.getChildren().get(0);
        assertThat(classNode.getType(), is(Type.INTERFACE));
        assertNameCorrectness(classNode, "Type");
    }

    @Test
    public void structureTreeWithAnnotationShouldBeCreated() throws Exception {
        fSnippet = "@interface Annotation {}";
        createStructureTree();
        JavaStructureNode classNode = fRoot.getChildren().get(0);
        assertThat(classNode.getType(), is(Type.ANNOTATION));
        assertNameCorrectness(classNode, "Annotation");
    }

    @Test
    public void structureTreeWithEnumShouldBeCreated() throws Exception {
        fSnippet = "enum Enumeration {}";
        createStructureTree();
        JavaStructureNode classNode = fRoot.getChildren().get(0);
        assertThat(classNode.getType(), is(Type.ENUM));
        assertNameCorrectness(classNode, "Enumeration");
    }

    @Test
    public void structureTreeWithFieldShouldBeCreated() throws Exception {
        fSnippet = getCompilationUnit("private int fInteger = 12;");
        createStructureTree();
        JavaStructureNode fieldNode = fRoot.getChildren().get(0).getChildren().get(0);
        assertThat(fieldNode.getType(), is(Type.FIELD));
        assertThat(fieldNode.getName(), is("fInteger : int"));
        assertThat(fieldNode.getFullyQualifiedName(), is("Clazz.fInteger : int"));
    }

    @Test
    public void structureTreeWithDefaultConstructorShouldBeCreated() throws Exception {
        fSnippet = getCompilationUnit("Clazz() {}");
        createStructureTree();
        JavaStructureNode constructorNode = fRoot.getChildren().get(0).getChildren().get(0);
        assertThat(constructorNode.getType(), is(Type.CONSTRUCTOR));
        assertThat(constructorNode.getName(), is("Clazz()"));
        assertThat(constructorNode.getFullyQualifiedName(), is("Clazz.Clazz()"));
    }

    @Test
    public void structureTreeWithConstructorShouldBeCreated() throws Exception {
        fSnippet = getCompilationUnit("Clazz(int a) {}");
        createStructureTree();
        JavaStructureNode constructorNode = fRoot.getChildren().get(0).getChildren().get(0);
        assertThat(constructorNode.getType(), is(Type.CONSTRUCTOR));
        assertThat(constructorNode.getName(), is("Clazz(int)"));
        assertThat(constructorNode.getFullyQualifiedName(), is("Clazz.Clazz(int)"));
    }

    @Test
    public void structureTreeWithMethodShouldBeCreated() throws Exception {
        fSnippet = getCompilationUnit("void method() {}");
        createStructureTree();
        JavaStructureNode methodNode = fRoot.getChildren().get(0).getChildren().get(1);
        assertThat(methodNode.getType(), is(Type.METHOD));
        assertThat(methodNode.getName(), is("method()"));
        assertThat(methodNode.getFullyQualifiedName(), is("Clazz.method()"));
    }

    @Test
    public void structureTreeWithMethodWithReturnTypeShouldBeCreated() throws Exception {
        fSnippet = getCompilationUnit("int method() {}");
        createStructureTree();
        JavaStructureNode methodNode = fRoot.getChildren().get(0).getChildren().get(1);
        assertThat(methodNode.getType(), is(Type.METHOD));
        assertThat(methodNode.getName(), is("method()"));
        assertThat(methodNode.getFullyQualifiedName(), is("Clazz.method()"));
    }

    @Test
    public void structureTreeWithMethodWithParametersShouldBeCreated() throws Exception {
        fSnippet = getCompilationUnit("void method(String name, int length) {}");
        createStructureTree();
        JavaStructureNode methodNode = fRoot.getChildren().get(0).getChildren().get(1);
        assertThat(methodNode.getType(), is(Type.METHOD));
        assertThat(methodNode.getName(), is("method(String,int)"));
        assertThat(methodNode.getFullyQualifiedName(), is("Clazz.method(String,int)"));
    }

    @Test
    public void structureTreeWithQualifiedClassShouldBeCreated() throws Exception {
        fSnippet = "package org.foo;\nclass Clazz {}";
        createStructureTree();
        JavaStructureNode classNode = fRoot.getChildren().get(0);
        assertThat(classNode.getType(), is(Type.CLASS));
        assertThat(classNode.getName(), is("Clazz"));
        assertThat(classNode.getFullyQualifiedName(), is("org.foo.Clazz"));
    }

    @Test
    public void structureTreeWithQualifiedFieldShouldBeCreated() throws Exception {
        fSnippet = getQualifiedCompilationUnit("private int fInteger = 12;");
        createStructureTree();
        JavaStructureNode fieldNode = fRoot.getChildren().get(0).getChildren().get(0);
        assertThat(fieldNode.getType(), is(Type.FIELD));
        assertThat(fieldNode.getName(), is("fInteger : int"));
        assertThat(fieldNode.getFullyQualifiedName(), is("org.foo.Clazz.fInteger : int"));
    }

    @Test
    public void structureTreeWithQualifiedConstructorShouldBeCreated() throws Exception {
        fSnippet = getQualifiedCompilationUnit("Clazz() {}");
        createStructureTree();
        JavaStructureNode constructorNode = fRoot.getChildren().get(0).getChildren().get(0);
        assertThat(constructorNode.getType(), is(Type.CONSTRUCTOR));
        assertThat(constructorNode.getName(), is("Clazz()"));
        assertThat(constructorNode.getFullyQualifiedName(), is("org.foo.Clazz.Clazz()"));
    }

    @Test
    public void structureTreeWithQualifiedMethodShouldBeCreated() throws Exception {
        fSnippet = getQualifiedCompilationUnit("void method(String name, int length) {}");
        createStructureTree();
        JavaStructureNode methodNode = fRoot.getChildren().get(0).getChildren().get(1);
        assertThat(methodNode.getType(), is(Type.METHOD));
        assertThat(methodNode.getName(), is("method(String,int)"));
        assertThat(methodNode.getFullyQualifiedName(), is("org.foo.Clazz.method(String,int)"));
    }

    private void createStructureTree() {
        JavaCompilation compilation = CompilationUtils.compileSource(fSnippet);
        CompilationUnitDeclaration cu = compilation.getCompilationUnit();
        fRoot = new JavaStructureNode(Type.CU, null, null, cu);
        cu.traverse(new JavaStructureTreeBuilder(fRoot), (CompilationUnitScope) null);
    }

    private String getCompilationUnit(String snippet) {
        return "class Clazz { " + snippet + " }";
    }

    private String getQualifiedCompilationUnit(String snippet) {
        return "package org.foo;\nclass Clazz { " + snippet + " }";
    }

    private void assertNameCorrectness(JavaStructureNode node, String name) {
        assertThat(node.getName(), is(name));
        assertThat(node.getFullyQualifiedName(), is(name));
    }
}
