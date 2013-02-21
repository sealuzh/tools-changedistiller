package ch.uzh.ifi.seal.changedistiller.treedifferencing;

import org.junit.Before;

import ch.uzh.ifi.seal.changedistiller.model.classifiers.EntityType;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;

public abstract class TreeDifferencingTestCase {

    protected Node fRootLeft;
    protected Node fRootRight;

    @Before
    public void setup() throws Exception {
        fRootLeft = new Node(JavaEntityType.ROOT_NODE, "method()");
        fRootRight = new Node(JavaEntityType.ROOT_NODE, "method()");
    }

    protected Node addToLeft(EntityType label, String value) {
        return addToNode(fRootLeft, label, value);
    }

    protected Node addToRight(EntityType label, String value) {
        return addToNode(fRootRight, label, value);
    }

    protected Node addToNode(Node root, EntityType label, String value) {
        Node node = new Node(label, value);
        root.add(node);
        return node;
    }

}
