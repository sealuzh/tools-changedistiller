package ch.uzh.ifi.seal.changedistiller.treedifferencing.operation;

import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.TreeEditOperation;

/**
 * Representation of the update basic {@link TreeEditOperation}.
 * 
 * @author Beat Fluri
 * 
 */
public class UpdateOperation implements TreeEditOperation {

    private Node fNodeToUpdate;
    private String fValue;
    private String fOldValue;
    private boolean fApplied;
    private Node fNewNode;

    /**
     * Creates a new update operation.
     * 
     * @param nodeToUpdate
     *            the node to update
     * @param newNode
     *            the node the updated node becomes
     * @param value
     *            the new value of the node to be updated
     */
    public UpdateOperation(Node nodeToUpdate, Node newNode, String value) {
        fNodeToUpdate = nodeToUpdate;
        fNewNode = newNode;
        fOldValue = fNodeToUpdate.getValue();
        // fNodeToUpdate.getEntity().setUniqueName(value);
        fValue = value;
    }

    @Override
    public void apply() {
        if (!fApplied) {
            // fNodeToUpdate.setValue(fValue);
            fApplied = true;
        }
    }

    public String getOldValue() {
        return fOldValue;
    }

    public Node getNodeToUpdate() {
        return fNodeToUpdate;
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.UPDATE;
    }

    public Node getNewNode() {
        return fNewNode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("--Update operation--\n");
        sb.append("Node value to update: ");
        sb.append(fOldValue);
        sb.append("\nwith value: ");
        sb.append(fValue);
        return sb.toString();
    }
}
