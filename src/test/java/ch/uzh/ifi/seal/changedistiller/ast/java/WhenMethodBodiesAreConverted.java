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

import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.junit.Test;

import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import ch.uzh.ifi.seal.changedistiller.util.CompilationUtils;

public class WhenMethodBodiesAreConverted extends WhenASTsAreConverted {

    @Test
    public void assignmentShouldBeConverted() throws Exception {
        fSnippet = "b = foo.bar();";
        prepareCompilation();
        convert();
        assertThat(getFirstLeaf().getLabel(), is(JavaEntityType.ASSIGNMENT));
        assertTreeStringCorrectness();
        assertSourceRangeCorrectness();
    }

    @Test
    public void compoundAssignmentShouldBeConverted() throws Exception {
        fSnippet = "b += foo.bar();";
        prepareCompilation();
        convert();
        assertThat(getFirstLeaf().getLabel(), is(JavaEntityType.ASSIGNMENT));
        assertTreeStringCorrectness();
        assertSourceRangeCorrectness();
    }

    @Test
    public void postfixExpressionShouldBeConverted() throws Exception {
        fSnippet = "b ++;";
        prepareCompilation();
        convert();
        assertThat(getFirstLeaf().getLabel(), is(JavaEntityType.POSTFIX_EXPRESSION));
        assertTreeStringCorrectness();
        assertSourceRangeCorrectness();
    }

    @Test
    public void prefixExpressionShouldBeConverted() throws Exception {
        fSnippet = "++ b;";
        prepareCompilation();
        convert();
        assertThat(getFirstLeaf().getLabel(), is(JavaEntityType.PREFIX_EXPRESSION));
        assertTreeStringCorrectness();
        assertSourceRangeCorrectness();
    }

    @Test
    public void allocationExpressionShouldBeConverted() throws Exception {
        fSnippet = "new Foo(bar);";
        prepareCompilation();
        convert();
        assertThat(getFirstLeaf().getLabel(), is(JavaEntityType.CLASS_INSTANCE_CREATION));
        assertTreeStringCorrectness();
        assertSourceRangeCorrectness();
    }

    @Test
    public void qualifiedAllocationExpressionShouldBeConverted() throws Exception {
        fSnippet = "foo.new Bar();";
        prepareCompilation();
        convert();
        assertThat(getFirstLeaf().getLabel(), is(JavaEntityType.CLASS_INSTANCE_CREATION));
        assertTreeStringCorrectness();
        assertSourceRangeCorrectness();
    }

    @Test
    public void assertStatementWithoutExceptionArgumentShouldBeConverted() throws Exception {
        fSnippet = "assert list.isEmpty();";
        prepareCompilation();
        convert();
        assertThat(getFirstLeaf().getLabel(), is(JavaEntityType.ASSERT_STATEMENT));
        assertThat(getTreeString(), is("method { list.isEmpty() }"));
        assertSourceRangeCorrectness();
    }

    @Test
    public void assertStatementWithExceptionArgumentShouldBeConverted() throws Exception {
        fSnippet = "assert list.isEmpty(): \"list not empty\";";
        prepareCompilation();
        convert();
        assertThat(getFirstLeaf().getLabel(), is(JavaEntityType.ASSERT_STATEMENT));
        assertThat(getTreeString(), is("method { list.isEmpty():\"list not empty\" }"));
        assertSourceRangeCorrectness();
    }

    @Test
    public void breakStatementWithoutLabelShouldBeConverted() throws Exception {
        fSnippet = "break;";
        prepareCompilation();
        convert();
        assertThat(getFirstLeaf().getLabel(), is(JavaEntityType.BREAK_STATEMENT));
        assertThat(getTreeString(), is("method {  }"));
        assertSourceRangeCorrectness();
    }

    @Test
    public void breakStatementWithLabelShouldBeConverted() throws Exception {
        fSnippet = "break foo;";
        prepareCompilation();
        convert();
        assertThat(getFirstLeaf().getLabel(), is(JavaEntityType.BREAK_STATEMENT));
        assertThat(getTreeString(), is("method { foo }"));
        assertSourceRangeCorrectness();
    }

    @Test
    public void explicitConstructorCallShouldBeConverted() throws Exception {
        fSnippet = "this(a);";
        prepareCompilation();
        convert();
        assertThat(getFirstLeaf().getLabel(), is(JavaEntityType.CONSTRUCTOR_INVOCATION));
        assertTreeStringCorrectness();
        assertSourceRangeCorrectness();
    }

    @Test
    public void continueStatementWithoutLabelShouldBeConverted() throws Exception {
        fSnippet = "continue;";
        prepareCompilation();
        convert();
        assertThat(getFirstLeaf().getLabel(), is(JavaEntityType.CONTINUE_STATEMENT));
        assertThat(getTreeString(), is("method {  }"));
        assertSourceRangeCorrectness();
    }

    @Test
    public void continueStatementWithLabelShouldBeConverted() throws Exception {
        fSnippet = "continue foo;";
        prepareCompilation();
        convert();
        assertThat(getFirstLeaf().getLabel(), is(JavaEntityType.CONTINUE_STATEMENT));
        assertThat(getTreeString(), is("method { foo }"));
        assertSourceRangeCorrectness();
    }

    @Test
    public void doStatementShouldBeConverted() throws Exception {
        fSnippet = "do { System.out.print('.'); } while (!list.isEmpty());";
        prepareCompilation();
        convert();
        assertThat(getFirstChild().getLabel(), is(JavaEntityType.DO_STATEMENT));
        assertThat(getTreeString(), is("method { (! list.isEmpty()) { System.out.print('.'); } }"));
        assertSourceRangeCorrectness(getFirstChild());
    }

    @Test
    public void foreachStatemenShouldBeConverted() throws Exception {
        fSnippet = "for (String st : list) { System.out.print('.'); }";
        prepareCompilation();
        convert();
        assertThat(getFirstChild().getLabel(), is(JavaEntityType.FOREACH_STATEMENT));
        assertThat(getTreeString(), is("method { String st:list { System.out.print('.'); } }"));
        assertSourceRangeCorrectness(getFirstChild());
    }

    @Test
    public void forStatementWithConditionShouldBeConverted() throws Exception {
        fSnippet = "for (int i = 0; i < list.size(); i++) { System.out.print('.'); }";
        prepareCompilation();
        convert();
        assertThat(getFirstChild().getLabel(), is(JavaEntityType.FOR_STATEMENT));
        assertThat(getTreeString(), is("method { (i < list.size()) { System.out.print('.');,int i = 0; { int i = 0; },i ++ { i ++; } } }"));
        assertSourceRangeCorrectness(getFirstChild());
    }

    @Test
    public void forStatementWithoutConditionShouldBeConverted() throws Exception {
        fSnippet = "for (;;) { System.out.print('.'); }";
        prepareCompilation();
        convert();
        assertThat(getFirstChild().getLabel(), is(JavaEntityType.FOR_STATEMENT));
        assertThat(getTreeString(), is("method {  { System.out.print('.'); } }"));
        assertSourceRangeCorrectness(getFirstChild());
    }

    @Test
    public void ifStatementShouldBeConverted() throws Exception {
        fSnippet = "if (list.isEmpty()) { System.out.print(\"empty\"); } else { System.out.print(\"not empty\"); }";
        prepareCompilation();
        convert();
        assertThat(getFirstChild().getLabel(), is(JavaEntityType.IF_STATEMENT));
        assertThat(((Node) getFirstChild().getFirstChild()).getLabel(), is(JavaEntityType.THEN_STATEMENT));
        assertThat(((Node) getFirstChild().getLastChild()).getLabel(), is(JavaEntityType.ELSE_STATEMENT));
        assertThat(
                getTreeString(),
                is("method { list.isEmpty() { list.isEmpty() { System.out.print(\"empty\"); },list.isEmpty() { System.out.print(\"not empty\"); } } }"));
        assertSourceRangeCorrectness(getFirstChild());
    }

    @Test
    public void ifStatementWithoutElseShouldBeConverted() throws Exception {
        fSnippet = "if (list.isEmpty()) { System.out.print(\"empty\"); }";
        prepareCompilation();
        convert();
        assertThat(getFirstChild().getLabel(), is(JavaEntityType.IF_STATEMENT));
        assertThat(((Node) getFirstChild().getFirstChild()).getLabel(), is(JavaEntityType.THEN_STATEMENT));
        assertThat(getTreeString(), is("method { list.isEmpty() { list.isEmpty() { System.out.print(\"empty\"); } } }"));
        assertSourceRangeCorrectness(getFirstChild());
    }

    @Test
    public void labeledStatementShouldBeConverted() throws Exception {
        fSnippet = "label: a = 24;";
        prepareCompilation();
        convert();
        assertThat(getFirstChild().getLabel(), is(JavaEntityType.LABELED_STATEMENT));
        assertThat(getTreeString(), is("method { label { a = 24; } }"));
        assertSourceRangeCorrectness(getFirstChild());
    }

    @Test
    public void localDeclarationShouldBeConverted() throws Exception {
        fSnippet = "float a = 24.0f;";
        prepareCompilation();
        convert();
        assertThat(getFirstLeaf().getLabel(), is(JavaEntityType.VARIABLE_DECLARATION_STATEMENT));
        assertTreeStringCorrectness();
        assertSourceRangeCorrectness();
    }

    @Test
    public void messageSendShouldBeConverted() throws Exception {
        fSnippet = "foo.bar(anInteger);";
        prepareCompilation();
        convert();
        assertThat(getFirstLeaf().getLabel(), is(JavaEntityType.METHOD_INVOCATION));
        assertTreeStringCorrectness();
        assertSourceRangeCorrectness();
    }

    @Test
    public void emptyReturnStatementShouldBeConverted() throws Exception {
        fSnippet = "return;";
        prepareCompilation();
        convert();
        assertThat(getFirstLeaf().getLabel(), is(JavaEntityType.RETURN_STATEMENT));
        assertThat(getTreeString(), is("method {  }"));
        assertSourceRangeCorrectness();
    }

    @Test
    public void returnStatementShouldBeConverted() throws Exception {
        fSnippet = "return Math.min(a, b);";
        prepareCompilation();
        convert();
        assertThat(getFirstLeaf().getLabel(), is(JavaEntityType.RETURN_STATEMENT));
        assertThat(getTreeString(), is("method { Math.min(a, b); }"));
        assertSourceRangeCorrectness();
    }

    @Test
    public void switchStatementShouldBeConverted() throws Exception {
        fSnippet = "switch (foo) { case ONE: a = 1; break; default: a = 2; }";
        prepareCompilation();
        convert();
        assertThat(getFirstChild().getLabel(), is(JavaEntityType.SWITCH_STATEMENT));
        assertThat(getFirstLeaf().getLabel(), is(JavaEntityType.SWITCH_CASE));
        assertThat(getTreeString(), is("method { foo { ONE,a = 1;,,default,a = 2; } }"));
        assertSourceRangeCorrectness(getFirstChild());
    }

    @Test
    public void synchronizedStatementShouldBeConverted() throws Exception {
        fSnippet = "synchronized(foo) { foo.bar(b); }";
        prepareCompilation();
        convert();
        assertThat(getFirstChild().getLabel(), is(JavaEntityType.SYNCHRONIZED_STATEMENT));
        assertThat(getTreeString(), is("method { foo { foo.bar(b); } }"));
        assertSourceRangeCorrectness(getFirstChild());
    }

    @Test
    public void throwStatementShouldBeConverted() throws Exception {
        fSnippet = "throw new RuntimeException(e);";
        prepareCompilation();
        convert();
        assertThat(getFirstChild().getLabel(), is(JavaEntityType.THROW_STATEMENT));
        assertThat(getTreeString(), is("method { new RuntimeException(e); }"));
        assertSourceRangeCorrectness(getFirstChild());
    }

    @Test
    public void tryStatementShouldBeConverted() throws Exception {
        fSnippet =
                "try { foo.bar(e); } catch (IOException e) { return 2; } catch (Exception e) { return 3; } finally { cleanup(); }";
        prepareCompilation();
        convert();
        assertThat(getFirstChild().getLabel(), is(JavaEntityType.TRY_STATEMENT));
        assertThat(
                ((Node) ((Node) getFirstChild().getFirstChild()).getNextSibling()).getLabel(),
                is(JavaEntityType.CATCH_CLAUSES));
        assertThat(
                ((Node) ((Node) getFirstChild().getFirstChild()).getNextSibling().getFirstChild()).getLabel(),
                is(JavaEntityType.CATCH_CLAUSE));
        assertThat(((Node) getFirstChild().getLastChild()).getLabel(), is(JavaEntityType.FINALLY));
        assertThat(
                getTreeString(),
                is("method {  {  { foo.bar(e); }, { IOException { 2; },Exception { 3; } }, { cleanup(); } } }"));
        assertSourceRangeCorrectness(getFirstChild());
    }

    @Test
    public void tryStatementWithoutCatchClausesShouldBeConverted() throws Exception {
        fSnippet = "try { foo.bar(e); } finally { cleanup(); }";
        prepareCompilation();
        convert();
        assertThat(((Node) getFirstChild().getLastChild()).getLabel(), is(JavaEntityType.FINALLY));
        assertThat(getTreeString(), is("method {  {  { foo.bar(e); }, { cleanup(); } } }"));
        assertSourceRangeCorrectness(getFirstChild());
    }

    @Test
    public void tryStatementWithoutFinallyShouldBeConverted() throws Exception {
        fSnippet = "try { foo.bar(e); } catch (IOException e) { return 2; } catch (Exception e) { return 3; }";
        prepareCompilation();
        convert();
        assertThat(
                ((Node) ((Node) getFirstChild().getFirstChild()).getNextSibling()).getLabel(),
                is(JavaEntityType.CATCH_CLAUSES));
        assertThat(
                ((Node) ((Node) getFirstChild().getFirstChild()).getNextSibling().getFirstChild()).getLabel(),
                is(JavaEntityType.CATCH_CLAUSE));
        assertThat(getTreeString(), is("method {  {  { foo.bar(e); }, { IOException { 2; },Exception { 3; } } } }"));
        assertSourceRangeCorrectness(getFirstChild());
    }

    @Test
    public void whileStatementWithoutFinallyShouldBeConverted() throws Exception {
        fSnippet = "while (i < a.length) { System.out.print('.'); }";
        prepareCompilation();
        convert();
        assertThat(getFirstChild().getLabel(), is(JavaEntityType.WHILE_STATEMENT));
        assertThat(getTreeString(), is("method { (i < a.length) { System.out.print('.'); } }"));
        assertSourceRangeCorrectness(getFirstChild());
    }

    private void assertTreeStringCorrectness() {
        assertThat(getTreeString(), is(getMethodString()));
    }

    private void assertSourceRangeCorrectness() {
        assertSourceRangeCorrectness(getFirstLeaf());
    }

    private void assertSourceRangeCorrectness(Node node) {
        SourceCodeEntity entity = node.getEntity();
        String source = fCompilation.getSource().substring(entity.getStartPosition(), entity.getEndPosition() + 1);
        assertThat(source, is(fSnippet));
    }

    private String getMethodString() {
        return "method { " + fSnippet + " }";
    }

    private void convert() {
        createRootNode(JavaEntityType.METHOD, "method");
        AbstractMethodDeclaration method = CompilationUtils.findMethod(fCompilation.getCompilationUnit(), "method");
        sMethodBodyConverter.initialize(fRoot, method, null, fCompilation.getScanner());
        method.traverse(sMethodBodyConverter, (ClassScope) null);
    }

    @Override
    protected String getSourceCodeWithSnippets(String... snippets) {
        StringBuilder src = new StringBuilder("public class Foo { ");
        src.append("public void method() { ");
        for (String statement : snippets) {
            src.append(statement).append(' ');
        }
        src.append("} }");
        return src.toString();
    }

}
