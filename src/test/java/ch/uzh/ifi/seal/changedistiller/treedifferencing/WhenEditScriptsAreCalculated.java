package ch.uzh.ifi.seal.changedistiller.treedifferencing;

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

import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.ASSIGNMENT;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.METHOD_INVOCATION;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.TreeDifferencer;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.TreeEditOperation;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.TreeEditOperation.OperationType;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.operation.DeleteOperation;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.operation.InsertOperation;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.operation.MoveOperation;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.operation.UpdateOperation;

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
        assertThat(insert.getNodeToInsert().getLabel(), is(methodInvocation.getLabel()));
        assertThat(insert.getNodeToInsert().getValue(), is(methodInvocation.getValue()));
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
        assertThat(move.getNewParent().getLabel(), is(ifStatementRight.getLabel()));
        assertThat(move.getNewParent().getValue(), is(ifStatementRight.getValue()));
        assertThat(move.getNodeToMove(), is(methodInvocation));
        assertThat(move.getNewNode().getLabel(), is(methodInvocation.getLabel()));
        assertThat(move.getNewNode().getValue(), is(methodInvocation.getValue()));
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
    
    
    @Test
	public void insertShouldWork() throws Exception {
    	/* test case for https://bitbucket.org/sealuzh/tools-changedistiller/issue/1 */
		Node outerTryRight = addToRight(JavaEntityType.TRY_STATEMENT, "");
		Node innerTryRight = addToNode(outerTryRight,
				JavaEntityType.TRY_STATEMENT, "");
		createEditScript();
		
		assertThat(fEditScript.size(), is(2));
		TreeEditOperation firstOperation = fEditScript.get(0);
		assertThat(firstOperation.getOperationType(), is(OperationType.INSERT));
		InsertOperation firstInsert = (InsertOperation) firstOperation;
		assertThat(firstInsert.getNodeToInsert().getLabel(), is(outerTryRight.getLabel()));
		assertThat(firstInsert.getNodeToInsert().getValue(), is(outerTryRight.getValue()));

		TreeEditOperation secondOperation = fEditScript.get(1);
		assertThat(secondOperation.getOperationType(), is(OperationType.INSERT));
		InsertOperation insert = (InsertOperation) secondOperation;
		assertThat(insert.getNodeToInsert().getLabel(), is(innerTryRight.getLabel()));
		assertThat(insert.getNodeToInsert().getValue(), is(innerTryRight.getValue()));
		
		assertThat(((Node)insert.getNodeToInsert().getParent()).getLabel(), is(innerTryRight.getLabel()));
		assertThat(((Node)insert.getNodeToInsert().getParent()).getValue(), is(innerTryRight.getValue()));
	}

    private void createEditScript() {
        fDifferencer.calculateEditScript(fRootLeft, fRootRight);
        fEditScript = fDifferencer.getEditScript();
    }

}
