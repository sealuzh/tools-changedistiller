package org.evolizer.changedistiller.treedifferencing;

import static org.evolizer.changedistiller.model.classifiers.java.JavaEntityType.ASSIGNMENT;
import static org.evolizer.changedistiller.model.classifiers.java.JavaEntityType.METHOD_INVOCATION;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.evolizer.changedistiller.model.classifiers.java.JavaEntityType;
import org.evolizer.changedistiller.treedifferencing.TreeEditOperation.OperationType;
import org.evolizer.changedistiller.treedifferencing.operation.DeleteOperation;
import org.evolizer.changedistiller.treedifferencing.operation.InsertOperation;
import org.evolizer.changedistiller.treedifferencing.operation.MoveOperation;
import org.evolizer.changedistiller.treedifferencing.operation.UpdateOperation;
import org.junit.Before;
import org.junit.Test;

public class WhenEditScriptsAreCalculated extends TreeDifferencingTestCase {

    private TreeDifferencer fDifferencer;
    private List<TreeEditOperation> fEditScript;

    @Before
    @Override
    public void setup() throws Exception {
        fRootLeft = new Node(JavaEntityType.ROOT_NODE, "method()");
        fRootRight = new Node(JavaEntityType.ROOT_NODE, "method()");
        fDifferencer = new TreeDifferencer();
    }

    @Test
    public void unchangedTreesShouldProduceEmptyEditScript() throws Exception {
        addToLeft(METHOD_INVOCATION, "foo.bar();");
        addToRight(METHOD_INVOCATION, "foo.bar();");
        createEditScript();
        assertThat(fEditScript.isEmpty(), is(true));
    }

    @Test
    public void insertedNodeShouldProduceInsertOperation() throws Exception {
        Node methodInvocation = addToRight(METHOD_INVOCATION, "foo.bar();");
        createEditScript();
        assertThat(fEditScript.size(), is(1));
        TreeEditOperation operation = fEditScript.get(0);
        assertThat(operation.getOperationType(), is(OperationType.INSERT));
        InsertOperation insert = (InsertOperation) operation;
        assertThat(insert.getParentNode(), is(fRootLeft));
        assertThat(insert.getNodeToInsert(), is(methodInvocation));
    }

    @Test
    public void deletedNodeShouldProduceDeleteOperation() throws Exception {
        Node methodInvocation = addToLeft(METHOD_INVOCATION, "foo.bar();");
        createEditScript();
        assertThat(fEditScript.size(), is(1));
        TreeEditOperation operation = fEditScript.get(0);
        assertThat(operation.getOperationType(), is(OperationType.DELETE));
        DeleteOperation delete = (DeleteOperation) operation;
        assertThat(delete.getParentNode(), is(fRootLeft));
        assertThat(delete.getNodeToDelete(), is(methodInvocation));
    }

    @Test
    public void movedNodeShouldProduceMoveOperation() throws Exception {
        Node methodInvocation = addToLeft(METHOD_INVOCATION, "foo.bar();");
        Node ifStatementLeft = addToLeft(JavaEntityType.IF_STATEMENT, "foo != null");
        addToNode(ifStatementLeft, ASSIGNMENT, "b = a;");
        Node ifStatementRight = addToRight(JavaEntityType.IF_STATEMENT, "foo != null");
        addToNode(ifStatementRight, METHOD_INVOCATION, "foo.bar();");
        addToNode(ifStatementRight, ASSIGNMENT, "b = a;");
        createEditScript();
        assertThat(fEditScript.size(), is(1));
        TreeEditOperation operation = fEditScript.get(0);
        assertThat(operation.getOperationType(), is(OperationType.MOVE));
        MoveOperation move = (MoveOperation) operation;
        assertThat(move.getOldParent(), is(fRootLeft));
        assertThat(move.getNewParent(), is(ifStatementRight));
        assertThat(move.getNodeToMove(), is(methodInvocation));
        assertThat(move.getNewNode(), is(methodInvocation));
    }

    @Test
    public void changedNodeShouldProduceUpdateOperation() throws Exception {
        Node methodInvocationLeft = addToLeft(METHOD_INVOCATION, "foo.bar();");
        Node methodInvocationRight = addToRight(METHOD_INVOCATION, "foo.beer();");
        createEditScript();
        assertThat(fEditScript.size(), is(1));
        TreeEditOperation operation = fEditScript.get(0);
        assertThat(operation.getOperationType(), is(OperationType.UPDATE));
        UpdateOperation update = (UpdateOperation) operation;
        assertThat(update.getNodeToUpdate(), is(methodInvocationLeft));
        assertThat(update.getNewNode(), is(methodInvocationRight));
        assertThat(update.getOldValue(), is("foo.bar();"));

    }

    private void createEditScript() {
        fDifferencer.calculateEditScript(fRootLeft, fRootRight);
        fEditScript = fDifferencer.getEditScript();
    }

}
