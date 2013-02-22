package ch.uzh.ifi.seal.changedistiller.treedifferencing.operation;

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
