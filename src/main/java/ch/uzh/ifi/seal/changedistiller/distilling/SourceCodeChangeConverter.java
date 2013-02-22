package ch.uzh.ifi.seal.changedistiller.distilling;

/*
 * #%L
 * ChangeDistiller
 * %%
 * Copyright (C) 2011 - 2013 Software Architecture and Evolution Lab, Department of Informatics, UZH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.LinkedList;
import java.util.List;

import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.model.entities.StructureEntityVersion;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.TreeEditOperation;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.operation.DeleteOperation;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.operation.InsertOperation;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.operation.MoveOperation;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.operation.UpdateOperation;

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
     */
    public SourceCodeChangeConverter(StructureEntityVersion structureEntity) {
        fStructureEntity = structureEntity;
        fSourceCodeChangeFactory = new SourceCodeChangeFactory();
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
