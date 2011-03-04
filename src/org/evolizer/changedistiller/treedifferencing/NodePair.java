package org.evolizer.changedistiller.treedifferencing;

/**
 * A pair of nodes.
 * 
 * @author Beat Fluri
 * 
 */
public class NodePair {

    private Node fLeft;
    private Node fRight;

    /**
     * Creates a new node pair
     * 
     * @param left
     *            node of the pair
     * @param right
     *            node of the pair
     */
    public NodePair(Node left, Node right) {
        fLeft = left;
        fRight = right;
    }

    public Node getLeft() {
        return fLeft;
    }

    public Node getRight() {
        return fRight;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fLeft == null) ? 0 : fLeft.hashCode());
        result = prime * result + ((fRight == null) ? 0 : fRight.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        NodePair other = (NodePair) obj;
        if (fLeft == null) {
            if (other.fLeft != null) {
                return false;
            }
        } else if (!fLeft.equals(other.fLeft)) {
            return false;
        }
        if (fRight == null) {
            if (other.fRight != null) {
                return false;
            }
        } else if (!fRight.equals(other.fRight)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getLeft().toString() + " == " + getRight().toString();
    }
}
