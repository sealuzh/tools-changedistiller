package org.evolizer.changedistiller.treedifferencing.matching;

import java.util.HashSet;
import java.util.Set;

import org.evolizer.changedistiller.model.classifiers.EntityType;
import org.evolizer.changedistiller.model.classifiers.java.JavaEntityType;
import org.evolizer.changedistiller.treedifferencing.ITreeMatcher;
import org.evolizer.changedistiller.treedifferencing.Node;
import org.evolizer.changedistiller.treedifferencing.NodePair;
import org.junit.Before;


public abstract class WhenTreeNodesAreMatched {

    protected Node fRootLeft;
    protected Node fRootRight;
    protected Set<NodePair> fMatchSet;
    protected ITreeMatcher fMatcher;

    @Before
    public void setup() {
        fRootLeft = new Node(JavaEntityType.ROOT_NODE, "method()");
        fRootRight = new Node(JavaEntityType.ROOT_NODE, "method()");
        fMatchSet = new HashSet<NodePair>();
        fMatcher = MatchingFactory.getMatcher(fMatchSet);
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

    protected void createMatchSet() {
        fMatcher.match(fRootLeft, fRootRight);
    }

}
