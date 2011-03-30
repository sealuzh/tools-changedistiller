package org.evolizer.changedistiller.treedifferencing;

import org.evolizer.changedistiller.model.classifiers.EntityType;
import org.evolizer.changedistiller.model.classifiers.java.JavaEntityType;
import org.junit.Before;

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
