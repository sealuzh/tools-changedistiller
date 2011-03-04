package org.evolizer.changedistiller.treedifferencing.matching.measure;

import java.util.Enumeration;
import java.util.Set;

import org.evolizer.changedistiller.treedifferencing.Node;
import org.evolizer.changedistiller.treedifferencing.NodePair;

/**
 * Implementation of the default inner node similarity calculator proposed by Chawathe.
 * 
 * @author Beat Fluri
 */
public class ChawatheCalculator implements NodeSimilarityCalculator {

    private Set<? extends NodePair> fLeafMatchSet;

    @Override
    public void setLeafMatchSet(Set<? extends NodePair> leafMatchSet) {
        fLeafMatchSet = leafMatchSet;
    }

    @Override
    public double calculateSimilarity(Node left, Node right) {
        int common = 0;
        // common(x, y) = {(w, z) in M | x contains w, and y contains z}
        // |common|
        for (NodePair p : fLeafMatchSet) {
            Node l = p.getLeft();
            Node r = p.getRight();
            if (left.isLeafDescendant(l) && !isComment(l) && right.isLeafDescendant(r) && !isComment(r)) {
                common++;
            }
        }
        int max = maxLeafStatements(left, right);
        return (double) common / (double) max;
    }

    private int maxLeafStatements(Node left, Node right) {
        int leftLeafStatements = left.getLeafCount() - numberOfCommentNodes(left);
        int rightLeafStatements = right.getLeafCount() - numberOfCommentNodes(right);
        return Math.max(leftLeafStatements, rightLeafStatements);
    }

    @SuppressWarnings("unchecked")
    private int numberOfCommentNodes(Node node) {
        int count = 0;

        Enumeration<Node> nodes = node.breadthFirstEnumeration();
        while (nodes.hasMoreElements()) {
            Node child = nodes.nextElement();
            if (isComment(child)) {
                count++;
            }
        }

        return count;
    }

    private boolean isComment(Node node) {
        return node.getLabel().isComment();
    }
}
