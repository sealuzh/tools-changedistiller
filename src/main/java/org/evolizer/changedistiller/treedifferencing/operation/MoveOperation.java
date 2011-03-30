package org.evolizer.changedistiller.treedifferencing.operation;

import org.evolizer.changedistiller.treedifferencing.Node;
import org.evolizer.changedistiller.treedifferencing.TreeEditOperation;

/**
 * Representation of the move basic {@link TreeEditOperation}.
 * 
 * @author Beat Fluri
 * 
 */
public class MoveOperation implements TreeEditOperation {

    private static final String LEFT_PARENTHESIS = " (";
    private static final String RIGHT_PARENTHESIS = ")";
    private Node fNodeToMove;
    private Node fOldParent;
    private Node fNewParent;
    private Node fNewNode;
    private int fPosition = -1;
    private boolean fApplied;

    /**
     * Creates a new move operation.
     * 
     * @param nodeToMove
     *            the node to move
     * @param newNode
     *            the node the moved node becomes
     * @param parent
     *            the parent node in which the node becomes a child after move
     * @param position
     *            the position of the node to move
     */
    public MoveOperation(Node nodeToMove, Node newNode, Node parent, int position) {
        fNodeToMove = nodeToMove;
        fNewNode = newNode;
        fOldParent = (Node) nodeToMove.getParent();
        fNewParent = parent;
        fPosition = position;
    }

    @Override
    public void apply() {
        if (!fApplied) {
            if (fNewParent.getChildCount() <= fPosition) {
                fNewParent.add(fNodeToMove);
            } else {
                fNewParent.insert(fNodeToMove, fPosition);
            }
            fApplied = true;
        }
    }

    public Node getNodeToMove() {
        return fNodeToMove;
    }

    public Node getOldParent() {
        return fOldParent;
    }

    public Node getNewParent() {
        return fNewParent;
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.MOVE;
    }

    public Node getNewNode() {
        return fNewNode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("--Move operation--\n");
        sb.append("Node value to move: ");
        sb.append(fNodeToMove.toString() + LEFT_PARENTHESIS + fNodeToMove.getLabel() + RIGHT_PARENTHESIS);
        sb.append("\nas child of: ");
        sb.append(fNewParent.toString() + LEFT_PARENTHESIS + fNewParent.getLabel() + RIGHT_PARENTHESIS);
        sb.append("\nat position: ");
        sb.append(fPosition);
        return sb.toString();
    }
}
