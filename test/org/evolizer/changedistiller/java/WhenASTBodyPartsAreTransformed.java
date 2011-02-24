package org.evolizer.changedistiller.java;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.evolizer.changedistiller.model.classifiers.EntityType;
import org.evolizer.changedistiller.model.entities.SourceCodeEntity;
import org.evolizer.changedistiller.treedifferencing.Node;
import org.evolizer.changedistiller.util.Compilation;
import org.evolizer.changedistiller.util.CompilationUtils;
import org.junit.Test;

public class WhenASTBodyPartsAreTransformed {

    private String fStatement;
    private Compilation fCompilation;
    private Node fRoot;

    @Test
    public void assignmentShouldBeTransformed() throws Exception {
        fStatement = "b = foo.bar();";
        prepareCompilation();
        transform();
        assertThat(getFirstLeaf().getLabel(), is(EntityType.ASSIGNMENT));
        assertTreeStringCorrectness();
        assertSourceRangeCorrectness();
    }

    @Test
    public void compoundAssignmentShouldBeTransformed() throws Exception {
        fStatement = "b += foo.bar();";
        prepareCompilation();
        transform();
        assertThat(getFirstLeaf().getLabel(), is(EntityType.ASSIGNMENT));
        assertTreeStringCorrectness();
        assertSourceRangeCorrectness();
    }

    @Test
    public void postfixExpressionShouldBeTransformed() throws Exception {
        fStatement = "b ++;";
        prepareCompilation();
        transform();
        assertThat(getFirstLeaf().getLabel(), is(EntityType.POSTFIX_EXPRESSION));
        assertTreeStringCorrectness();
        assertSourceRangeCorrectness();
    }

    @Test
    public void prefixExpressionShouldBeTransformed() throws Exception {
        fStatement = "++ b;";
        prepareCompilation();
        transform();
        assertThat(getFirstLeaf().getLabel(), is(EntityType.PREFIX_EXPRESSION));
        assertTreeStringCorrectness();
        assertSourceRangeCorrectness();
    }

    @Test
    public void allocationExpressionShouldBeTransformed() throws Exception {
        fStatement = "new Foo(bar);";
        prepareCompilation();
        transform();
        assertThat(getFirstLeaf().getLabel(), is(EntityType.CLASS_INSTANCE_CREATION));
        assertTreeStringCorrectness();
        assertSourceRangeCorrectness();
    }

    @Test
    public void qualifiedAllocationExpressionShouldBeTransformed() throws Exception {
        fStatement = "foo.new Bar();";
        prepareCompilation();
        transform();
        assertThat(getFirstLeaf().getLabel(), is(EntityType.CLASS_INSTANCE_CREATION));
        assertTreeStringCorrectness();
        assertSourceRangeCorrectness();
    }

    @Test
    public void assertStatementWithoutExceptionArgumentShouldBeTransformed() throws Exception {
        fStatement = "assert list.isEmpty();";
        prepareCompilation();
        transform();
        assertThat(getFirstLeaf().getLabel(), is(EntityType.ASSERT_STATEMENT));
        assertThat(getTreeString(), is("method { list.isEmpty() }"));
        assertSourceRangeCorrectness();
    }

    @Test
    public void assertStatementWithExceptionArgumentShouldBeTransformed() throws Exception {
        fStatement = "assert list.isEmpty(): \"list not empty\";";
        prepareCompilation();
        transform();
        assertThat(getFirstLeaf().getLabel(), is(EntityType.ASSERT_STATEMENT));
        assertThat(getTreeString(), is("method { list.isEmpty():\"list not empty\" }"));
        assertSourceRangeCorrectness();
    }

    @Test
    public void breakStatementWithoutLabelShouldBeTransformed() throws Exception {
        fStatement = "break;";
        prepareCompilation();
        transform();
        assertThat(getFirstLeaf().getLabel(), is(EntityType.BREAK_STATEMENT));
        assertThat(getTreeString(), is("method {  }"));
        assertSourceRangeCorrectness();
    }

    @Test
    public void breakStatementWithLabelShouldBeTransformed() throws Exception {
        fStatement = "break foo;";
        prepareCompilation();
        transform();
        assertThat(getFirstLeaf().getLabel(), is(EntityType.BREAK_STATEMENT));
        assertThat(getTreeString(), is("method { foo }"));
        assertSourceRangeCorrectness();
    }

    @Test
    public void explicitConstructorCallShouldBeTransformed() throws Exception {
        fStatement = "this(a);";
        prepareCompilation();
        transform();
        assertThat(getFirstLeaf().getLabel(), is(EntityType.CONSTRUCTOR_INVOCATION));
        assertTreeStringCorrectness();
        assertSourceRangeCorrectness();
    }

    @Test
    public void continueStatementWithoutLabelShouldBeTransformed() throws Exception {
        fStatement = "continue;";
        prepareCompilation();
        transform();
        assertThat(getFirstLeaf().getLabel(), is(EntityType.CONTINUE_STATEMENT));
        assertThat(getTreeString(), is("method {  }"));
        assertSourceRangeCorrectness();
    }

    @Test
    public void continueStatementWithLabelShouldBeTransformed() throws Exception {
        fStatement = "continue foo;";
        prepareCompilation();
        transform();
        assertThat(getFirstLeaf().getLabel(), is(EntityType.CONTINUE_STATEMENT));
        assertThat(getTreeString(), is("method { foo }"));
        assertSourceRangeCorrectness();
    }

    @Test
    public void doStatementShouldBeTransformed() throws Exception {
        fStatement = "do { System.out.print('.'); } while (!list.isEmpty());";
        prepareCompilation();
        transform();
        assertThat(getFirstChild().getLabel(), is(EntityType.DO_STATEMENT));
        assertThat(getTreeString(), is("method { (! list.isEmpty()) { System.out.print('.'); } }"));
        assertSourceRangeCorrectness(getFirstChild());
    }

    @Test
    public void foreachStatemenShouldBeTransformed() throws Exception {
        fStatement = "for (String st : list) { System.out.print('.'); }";
        prepareCompilation();
        transform();
        assertThat(getFirstChild().getLabel(), is(EntityType.FOREACH_STATEMENT));
        assertThat(getTreeString(), is("method { String st:list { System.out.print('.'); } }"));
        assertSourceRangeCorrectness(getFirstChild());
    }

    @Test
    public void forStatementWithConditionShouldBeTransformed() throws Exception {
        fStatement = "for (int i = 0; i < list.size(); i++) { System.out.print('.'); }";
        prepareCompilation();
        transform();
        assertThat(getFirstChild().getLabel(), is(EntityType.FOR_STATEMENT));
        assertThat(getTreeString(), is("method { (i < list.size()) { System.out.print('.'); } }"));
        assertSourceRangeCorrectness(getFirstChild());
    }

    @Test
    public void forStatementWithoutConditionShouldBeTransformed() throws Exception {
        fStatement = "for (;;) { System.out.print('.'); }";
        prepareCompilation();
        transform();
        assertThat(getFirstChild().getLabel(), is(EntityType.FOR_STATEMENT));
        assertThat(getTreeString(), is("method {  { System.out.print('.'); } }"));
        assertSourceRangeCorrectness(getFirstChild());
    }

    @Test
    public void ifStatementShouldBeTransformed() throws Exception {
        fStatement = "if (list.isEmpty()) { System.out.print(\"empty\"); } else { System.out.print(\"not empty\"); }";
        prepareCompilation();
        transform();
        assertThat(getFirstChild().getLabel(), is(EntityType.IF_STATEMENT));
        assertThat(((Node) getFirstChild().getFirstChild()).getLabel(), is(EntityType.THEN_STATEMENT));
        assertThat(((Node) getFirstChild().getLastChild()).getLabel(), is(EntityType.ELSE_STATEMENT));
        assertThat(
                getTreeString(),
                is("method { list.isEmpty() { list.isEmpty() { System.out.print(\"empty\"); },list.isEmpty() { System.out.print(\"not empty\"); } } }"));
        assertSourceRangeCorrectness(getFirstChild());
    }

    @Test
    public void ifStatementWithoutElseShouldBeTransformed() throws Exception {
        fStatement = "if (list.isEmpty()) { System.out.print(\"empty\"); }";
        prepareCompilation();
        transform();
        assertThat(getFirstChild().getLabel(), is(EntityType.IF_STATEMENT));
        assertThat(((Node) getFirstChild().getFirstChild()).getLabel(), is(EntityType.THEN_STATEMENT));
        assertThat(getTreeString(), is("method { list.isEmpty() { list.isEmpty() { System.out.print(\"empty\"); } } }"));
        assertSourceRangeCorrectness(getFirstChild());
    }

    @Test
    public void labeledStatementShouldBeTransformed() throws Exception {
        fStatement = "label: a = 24;";
        prepareCompilation();
        transform();
        assertThat(getFirstChild().getLabel(), is(EntityType.LABELED_STATEMENT));
        assertThat(getTreeString(), is("method { label { a = 24; } }"));
        assertSourceRangeCorrectness(getFirstChild());
    }

    @Test
    public void localDeclarationShouldBeTransformed() throws Exception {
        fStatement = "float a = 24.0f;";
        prepareCompilation();
        transform();
        assertThat(getFirstLeaf().getLabel(), is(EntityType.VARIABLE_DECLARATION_STATEMENT));
        assertTreeStringCorrectness();
        assertSourceRangeCorrectness();
    }

    @Test
    public void messageSendShouldBeTransformed() throws Exception {
        fStatement = "foo.bar(anInteger);";
        prepareCompilation();
        transform();
        assertThat(getFirstLeaf().getLabel(), is(EntityType.METHOD_INVOCATION));
        assertTreeStringCorrectness();
        assertSourceRangeCorrectness();
    }

    @Test
    public void emptyReturnStatementShouldBeTransformed() throws Exception {
        fStatement = "return;";
        prepareCompilation();
        transform();
        assertThat(getFirstLeaf().getLabel(), is(EntityType.RETURN_STATEMENT));
        assertThat(getTreeString(), is("method {  }"));
        assertSourceRangeCorrectness();
    }

    @Test
    public void returnStatementShouldBeTransformed() throws Exception {
        fStatement = "return Math.min(a, b);";
        prepareCompilation();
        transform();
        assertThat(getFirstLeaf().getLabel(), is(EntityType.RETURN_STATEMENT));
        assertThat(getTreeString(), is("method { Math.min(a, b); }"));
        assertSourceRangeCorrectness();
    }

    @Test
    public void switchStatementShouldBeTransformed() throws Exception {
        fStatement = "switch (foo) { case ONE: a = 1; break; default: a = 2; }";
        prepareCompilation();
        transform();
        assertThat(getFirstChild().getLabel(), is(EntityType.SWITCH_STATEMENT));
        assertThat(getFirstLeaf().getLabel(), is(EntityType.SWITCH_CASE));
        assertThat(getTreeString(), is("method { foo { ONE,a = 1;,,default,a = 2; } }"));
        assertSourceRangeCorrectness(getFirstChild());
    }

    @Test
    public void synchronizedStatementShouldBeTransformed() throws Exception {
        fStatement = "synchronized(foo) { foo.bar(b); }";
        prepareCompilation();
        transform();
        assertThat(getFirstChild().getLabel(), is(EntityType.SYNCHRONIZED_STATEMENT));
        assertThat(getTreeString(), is("method { foo { foo.bar(b); } }"));
        assertSourceRangeCorrectness(getFirstChild());
    }

    @Test
    public void throwStatementShouldBeTransformed() throws Exception {
        fStatement = "throw new RuntimeException(e);";
        prepareCompilation();
        transform();
        assertThat(getFirstChild().getLabel(), is(EntityType.THROW_STATEMENT));
        assertThat(getTreeString(), is("method { new RuntimeException(e); }"));
        assertSourceRangeCorrectness(getFirstChild());
    }

    @Test
    public void tryStatementShouldBeTransformed() throws Exception {
        fStatement =
                "try { foo.bar(e); } catch (IOException e) { return 2; } catch (Exception e) { return 3; } finally { cleanup(); }";
        prepareCompilation();
        transform();
        assertThat(getFirstChild().getLabel(), is(EntityType.TRY_STATEMENT));
        assertThat(
                ((Node) ((Node) getFirstChild().getFirstChild()).getNextSibling()).getLabel(),
                is(EntityType.CATCH_CLAUSES));
        assertThat(
                ((Node) ((Node) getFirstChild().getFirstChild()).getNextSibling().getFirstChild()).getLabel(),
                is(EntityType.CATCH_CLAUSE));
        assertThat(((Node) getFirstChild().getLastChild()).getLabel(), is(EntityType.FINALLY));
        assertThat(
                getTreeString(),
                is("method {  {  { foo.bar(e); }, { IOException { 2; },Exception { 3; } }, { cleanup(); } } }"));
        assertSourceRangeCorrectness(getFirstChild());
    }

    @Test
    public void tryStatementWithoutCatchClausesShouldBeTransformed() throws Exception {
        fStatement = "try { foo.bar(e); } finally { cleanup(); }";
        prepareCompilation();
        transform();
        assertThat(((Node) getFirstChild().getLastChild()).getLabel(), is(EntityType.FINALLY));
        assertThat(getTreeString(), is("method {  {  { foo.bar(e); }, { cleanup(); } } }"));
        assertSourceRangeCorrectness(getFirstChild());
    }

    @Test
    public void tryStatementWithoutFinallyShouldBeTransformed() throws Exception {
        fStatement = "try { foo.bar(e); } catch (IOException e) { return 2; } catch (Exception e) { return 3; }";
        prepareCompilation();
        transform();
        assertThat(
                ((Node) ((Node) getFirstChild().getFirstChild()).getNextSibling()).getLabel(),
                is(EntityType.CATCH_CLAUSES));
        assertThat(
                ((Node) ((Node) getFirstChild().getFirstChild()).getNextSibling().getFirstChild()).getLabel(),
                is(EntityType.CATCH_CLAUSE));
        assertThat(getTreeString(), is("method {  {  { foo.bar(e); }, { IOException { 2; },Exception { 3; } } } }"));
        assertSourceRangeCorrectness(getFirstChild());
    }

    @Test
    public void whileStatementWithoutFinallyShouldBeTransformed() throws Exception {
        fStatement = "while (i < a.length) { System.out.print('.'); }";
        prepareCompilation();
        transform();
        assertThat(getFirstChild().getLabel(), is(EntityType.WHILE_STATEMENT));
        assertThat(getTreeString(), is("method { (i < a.length) { System.out.print('.'); } }"));
        assertSourceRangeCorrectness(getFirstChild());
    }

    private void assertTreeStringCorrectness() {
        assertThat(getTreeString(), is(getMethodString()));
    }

    private void prepareCompilation() {
        fCompilation = CompilationUtils.compileSource(getSourceCodeWithStatements(fStatement));
    }

    private String getTreeString() {
        return fRoot.print(new StringBuilder()).toString();
    }

    private void assertSourceRangeCorrectness() {
        assertSourceRangeCorrectness(getFirstLeaf());
    }

    private void assertSourceRangeCorrectness(Node node) {
        SourceCodeEntity entity = node.getEntity();
        String source =
                fCompilation.getSource().substring(
                        entity.getSourceRange().getStart(),
                        entity.getSourceRange().getEnd() + 1);
        assertThat(source, is(fStatement));
    }

    private String getMethodString() {
        return "method { " + fStatement + " }";
    }

    private Node getFirstLeaf() {
        return ((Node) fRoot.getFirstLeaf());
    }

    private Node getFirstChild() {
        return (Node) fRoot.getFirstChild();
    }

    private void transform() {
        fRoot = new Node(EntityType.METHOD, "method", null);
        AbstractMethodDeclaration method = CompilationUtils.findMethod(fCompilation.getCompilationUnit(), "method");
        JavaASTBodyTransformer bodyT =
                new JavaASTBodyTransformer(fRoot, method, null, fCompilation.getScanner(), new JavaASTHelper());
        method.traverse(bodyT, (ClassScope) null);
    }

    private String getSourceCodeWithStatements(String... statements) {
        StringBuilder src = new StringBuilder("public class Foo { ");
        src.append("public void method() { ");
        for (String statement : statements) {
            src.append(statement).append(' ');
        }
        src.append("} }");
        return src.toString();
    }

}
