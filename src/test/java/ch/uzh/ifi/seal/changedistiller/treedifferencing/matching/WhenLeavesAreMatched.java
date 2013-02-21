package ch.uzh.ifi.seal.changedistiller.treedifferencing.matching;

import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.ASSIGNMENT;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.CONSTRUCTOR_INVOCATION;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.IF_STATEMENT;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.METHOD_INVOCATION;
import static ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType.RETURN_STATEMENT;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.LeafPair;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;

public class WhenLeavesAreMatched extends WhenTreeNodesAreMatched {

    @Test
    public void unchangedLeavesShouldMatch() throws Exception {
        Node methodInvocationLeft = addToLeft(METHOD_INVOCATION, "foo.bar();");
        Node methodInvocationRight = addToRight(METHOD_INVOCATION, "foo.bar();");
        createMatchSet();
        assertLeavesAreMatched(methodInvocationLeft, methodInvocationRight);
    }

    @Test
    public void leavesWithDifferentLabelsShoudlNotMatch() throws Exception {
        Node methodInvocationLeft = addToLeft(CONSTRUCTOR_INVOCATION, "foo.bar();");
        Node methodInvocationRight = addToRight(METHOD_INVOCATION, "foo.bar();");
        createMatchSet();
        assertLeavesAreNotMatched(methodInvocationLeft, methodInvocationRight);
    }

    @Test
    public void unchangedLeavesOnDifferentPositionsShouldMatch() throws Exception {
        Node methodInvocationLeft = addToLeft(METHOD_INVOCATION, "foo.bar();");
        Node ifStatement = addToRight(IF_STATEMENT, "true");
        Node methodInvocationRight = addToNode(ifStatement, METHOD_INVOCATION, "foo.bar();");
        createMatchSet();
        assertLeavesAreMatched(methodInvocationLeft, methodInvocationRight);
    }

    @Test
    public void unchangedLeavesAmongManyLeavesShouldMatch() throws Exception {
        Node methodInvocationLeft = addToLeft(METHOD_INVOCATION, "foo.bar();");
        addToLeft(ASSIGNMENT, "a = b;");
        addToRight(RETURN_STATEMENT, "return b;");
        Node methodInvocationRight = addToRight(METHOD_INVOCATION, "foo.bar();");
        createMatchSet();
        assertLeavesAreMatched(methodInvocationLeft, methodInvocationRight);
    }

    @Test
    public void changedButSimilarLeavesShouldMatch() throws Exception {
        Node methodInvocationLeft = addToLeft(METHOD_INVOCATION, "foo.bar();");
        Node methodInvocationRight = addToRight(METHOD_INVOCATION, "foo.bear();");
        createMatchSet();
        assertLeavesAreMatched(methodInvocationLeft, methodInvocationRight);
    }

    @Test
    public void changedButSimilarLeavesAmongManyLeavesShouldMatch() throws Exception {
        Node methodInvocationLeft = addToLeft(METHOD_INVOCATION, "foo.bar();");
        addToLeft(JavaEntityType.ASSIGNMENT, "a = b;");
        addToRight(JavaEntityType.RETURN_STATEMENT, "return b;");
        Node methodInvocationRight = addToRight(JavaEntityType.METHOD_INVOCATION, "foo.bear();");
        createMatchSet();
        assertLeavesAreMatched(methodInvocationLeft, methodInvocationRight);
    }

    private void assertLeavesAreMatched(Node left, Node right) {
        assertThat(fMatchSet, hasItem(new LeafPair(left, right)));
    }

    private void assertLeavesAreNotMatched(Node left, Node right) {
        assertThat(fMatchSet, not(hasItem(new LeafPair(left, right))));
    }

}
