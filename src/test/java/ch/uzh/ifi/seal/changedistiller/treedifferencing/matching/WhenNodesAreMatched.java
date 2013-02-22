package ch.uzh.ifi.seal.changedistiller.treedifferencing.matching;

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
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.CONSTRUCTOR_INVOCATION;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.FOREACH_STATEMENT;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.FOR_STATEMENT;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.METHOD_INVOCATION;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.VARIABLE_DECLARATION_STATEMENT;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.WHILE_STATEMENT;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.NodePair;

public class WhenNodesAreMatched extends WhenTreeNodesAreMatched {

    @Test
    public void unchangedNodesShouldMatch() throws Exception {
        Node whileStatementLeft = addToLeft(WHILE_STATEMENT, "i < length");
        Node whileStatementRight = addToRight(WHILE_STATEMENT, "i < length");
        addToNode(whileStatementLeft, METHOD_INVOCATION, "foo.bar();");
        addToNode(whileStatementLeft, ASSIGNMENT, "aInt = 24;");
        addToNode(whileStatementRight, METHOD_INVOCATION, "foo.bar();");
        addToNode(whileStatementRight, ASSIGNMENT, "aInt = 24;");
        createMatchSet();
        assertNodesAreMatched(whileStatementLeft, whileStatementRight);
    }

    @Test
    public void nodesWithDifferentLabelsShouldNotMatch() throws Exception {
        Node whileStatementLeft = addToLeft(WHILE_STATEMENT, "i < length");
        Node whileStatementRight = addToRight(FOR_STATEMENT, "i < length");
        addToNode(whileStatementLeft, METHOD_INVOCATION, "foo.bar();");
        addToNode(whileStatementLeft, ASSIGNMENT, "aInt = 24;");
        addToNode(whileStatementRight, METHOD_INVOCATION, "foo.bar();");
        addToNode(whileStatementRight, ASSIGNMENT, "aInt = 24;");
        createMatchSet();
        assertNodesAreNotMatched(whileStatementLeft, whileStatementRight);
    }

    @Test
    public void unchangedNodesOnDifferentPositionsShouldMatch() throws Exception {
        Node whileStatementLeft = addToLeft(WHILE_STATEMENT, "i < length");
        Node forStatementRight = addToRight(FOR_STATEMENT, "j < length");
        Node whileStatementRight = addToNode(forStatementRight, WHILE_STATEMENT, "i < length");
        addToNode(whileStatementLeft, METHOD_INVOCATION, "foo.bar();");
        addToNode(whileStatementLeft, ASSIGNMENT, "aInt = 24;");
        addToNode(whileStatementRight, METHOD_INVOCATION, "foo.bar();");
        addToNode(whileStatementRight, ASSIGNMENT, "aInt = 24;");
        createMatchSet();
        assertNodesAreMatched(whileStatementLeft, whileStatementRight);
    }

    @Test
    public void unchangedNodesButDifferentLeavesShouldNotMatch() throws Exception {
        Node whileStatementLeft = addToLeft(WHILE_STATEMENT, "i < length");
        Node whileStatementRight = addToRight(WHILE_STATEMENT, "i < length");
        addToNode(whileStatementLeft, CONSTRUCTOR_INVOCATION, "foo.bar();");
        addToNode(whileStatementLeft, ASSIGNMENT, "aInt = 24;");
        addToNode(whileStatementRight, METHOD_INVOCATION, "foo.bar();");
        addToNode(whileStatementRight, VARIABLE_DECLARATION_STATEMENT, "int aInt = 24;");
        createMatchSet();
        assertNodesAreNotMatched(whileStatementLeft, whileStatementRight);
    }

    @Test
    public void changedNodesShouldMatch() throws Exception {
        Node whileStatementLeft = addToLeft(WHILE_STATEMENT, "i < length");
        Node whileStatementRight = addToRight(WHILE_STATEMENT, "i < size");
        addToNode(whileStatementLeft, METHOD_INVOCATION, "foo.bar();");
        addToNode(whileStatementLeft, ASSIGNMENT, "aInt = 24;");
        addToNode(whileStatementRight, METHOD_INVOCATION, "foo.bar();");
        addToNode(whileStatementRight, ASSIGNMENT, "aInt = 24;");
        createMatchSet();
        assertNodesAreMatched(whileStatementLeft, whileStatementRight);
    }

    @Test
    public void unchangedNodesAmongManyNodesShouldMatch() throws Exception {
        Node whileStatementLeft = addToLeft(WHILE_STATEMENT, "i < length");
        addToLeft(FOR_STATEMENT, "j < length");
        addToRight(FOREACH_STATEMENT, "j < size");
        Node whileStatementRight = addToRight(WHILE_STATEMENT, "i < length");
        addToNode(whileStatementLeft, METHOD_INVOCATION, "foo.bar();");
        addToNode(whileStatementLeft, ASSIGNMENT, "aInt = 24;");
        addToNode(whileStatementRight, METHOD_INVOCATION, "foo.bar();");
        addToNode(whileStatementRight, ASSIGNMENT, "aInt = 24;");
        createMatchSet();
        assertNodesAreMatched(whileStatementLeft, whileStatementRight);
    }

    private void assertNodesAreMatched(Node left, Node right) {
        assertThat(fMatchSet, hasItem(new NodePair(left, right)));
    }

    private void assertNodesAreNotMatched(Node left, Node right) {
        assertThat(fMatchSet, not(hasItem(new NodePair(left, right))));
    }

}
