package org.evolizer.changedistiller.treedifferencing;

/**
 * Interface for basic tree edit operations.
 * 
 * @author Beat Fluri
 * @see Node
 */
public interface TreeEditOperation {

    /**
     * Applies the tree edit operation on the {@link Node} that is involved.
     */
    void apply();

    /**
     * Returns the {@link OperationType} of the tree edit operation.
     * 
     * @return the operation type of the tree edit operation
     */
    OperationType getOperationType();

    /**
     * Type of {@link TreeEditOperation}.
     * 
     * @author Beat Fluri
     */
    enum OperationType {
        INSERT,
        DELETE,
        MOVE,
        UPDATE;
    }
}
