package org.evolizer.changedistiller.treedifferencing.operation;

import org.evolizer.changedistiller.treedifferencing.Node;
import org.evolizer.changedistiller.treedifferencing.TreeEditOperation;

/**
 * Representation of the insert basic {@link TreeEditOperation}.
 * 
 * @author Beat Fluri
 * 
 */
public class InsertOperation implements TreeEditOperation {

    private static final String LEFT_PARENTHESIS = " (";
    private static final String RIGHT_PARENTHESIS = ")";
    private Node fNodeToInsert;
    private Node fParent;
    private int fPosition = -1;
    private boolean fApplied;

    /**
     * Creates a new insert operation.
     * 
     * @param nodeToInsert
     *            the node to insert
     * @param parent
     *            the parent in which the node is inserted
     * @param position
     *            the position of the node to insert
     */
    public InsertOperation(Node nodeToInsert, Node parent, int position) {
        fNodeToInsert = nodeToInsert;
        fParent = parent;
        fPosition = position;
    }

    @Override
    public void apply() {
        if (!fApplied) {
            fParent.insert(fNodeToInsert, fPosition);
            fApplied = true;
        }
    }

    public Node getNodeToInsert() {
        return fNodeToInsert;
    }

    public Node getParentNode() {
        return fParent;
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.INSERT;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("--Insert operation--\n");
        sb.append("Node value to insert: ");
        sb.append(fNodeToInsert.toString() + LEFT_PARENTHESIS + fNodeToInsert.getLabel() + RIGHT_PARENTHESIS);
        sb.append("\nas child of: ");
        sb.append(fParent.toString() + LEFT_PARENTHESIS + fParent.getLabel() + RIGHT_PARENTHESIS);
        sb.append("\nat position: ");
        sb.append(fPosition);
        return sb.toString();
    }
}
