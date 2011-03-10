package org.evolizer.changedistiller.distilling;

import java.util.LinkedList;
import java.util.List;

import org.evolizer.changedistiller.model.entities.SourceCodeChange;
import org.evolizer.changedistiller.model.entities.StructureEntityVersion;
import org.evolizer.changedistiller.treedifferencing.TreeEditOperation;
import org.evolizer.changedistiller.treedifferencing.operation.DeleteOperation;
import org.evolizer.changedistiller.treedifferencing.operation.InsertOperation;
import org.evolizer.changedistiller.treedifferencing.operation.MoveOperation;
import org.evolizer.changedistiller.treedifferencing.operation.UpdateOperation;

/**
 * Converts {@link TreeEditOperation}s to {@link SourceCodeChange}s and attaches them to a
 * {@link StructureEntityVersion} in which the changes happened.
 * 
 * @author Beat Fluri
 */
public final class SourceCodeChangeConverter {

    private StructureEntityVersion fStructureEntity;
    private List<SourceCodeChange> fSourceCodeChanges;
    private SourceCodeChangeFactory fSourceCodeChangeFactory;

    /**
     * Creates a new {@link SourceCodeChangeConverter}.
     * 
     * @param structureEntity
     *            in which the {@link SourceCodeChange}s happened
     * @param factory
     *            create the source code changes
     */
    public SourceCodeChangeConverter(StructureEntityVersion structureEntity, SourceCodeChangeFactory factory) {
        fStructureEntity = structureEntity;
        fSourceCodeChangeFactory = factory;
        fSourceCodeChanges = new LinkedList<SourceCodeChange>();
    }

    /**
     * Converts a {@link TreeEditOperation}s to a {@link SourceCodeChange}s and adds it to the
     * {@link StructureEntityVersion}.
     * <p>
     * A {@link SourceCodeChange} is added if the conversion didn't result in a <code>null</code> value.
     * 
     * @param operation
     *            to convert
     */
    public void addTreeEditOperationAsSourceCodeChange(TreeEditOperation operation) {
        SourceCodeChange scc = null;
        switch (operation.getOperationType()) {
            case INSERT:
                scc = fSourceCodeChangeFactory.createInsertOperation(fStructureEntity, (InsertOperation) operation);
                break;
            case DELETE:
                scc = fSourceCodeChangeFactory.createDeleteOperation(fStructureEntity, (DeleteOperation) operation);
                break;
            case MOVE:
                scc = fSourceCodeChangeFactory.createMoveOperation(fStructureEntity, (MoveOperation) operation);
                break;
            case UPDATE:
                scc = fSourceCodeChangeFactory.createUpdateOperation(fStructureEntity, (UpdateOperation) operation);
                break;
            default:
                throw new RuntimeException("Unkown operation type: " + operation);
        }
        if (scc != null) {
            fSourceCodeChanges.add(scc);
        }
    }

    /**
     * Converts a {@link List} of {@link TreeEditOperation}s into a {@link List} of {@link SourceCodeChange}s and adds
     * them to the {@link StructureEntityVersion}.
     * <p>
     * A {@link SourceCodeChange} is added if the conversion didn't result in a <code>null</code> value.
     * 
     * @param operations
     *            to convert
     */
    public void addTreeEditOperationsAsSourceCodeChanges(List<TreeEditOperation> operations) {
        if (operations != null) {
            for (TreeEditOperation op : operations) {
                addTreeEditOperationAsSourceCodeChange(op);
            }
        }
    }

    public List<SourceCodeChange> getSourceCodeChanges() {
        return fSourceCodeChanges;
    }

}
