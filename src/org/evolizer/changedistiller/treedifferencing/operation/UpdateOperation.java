/*
 * Copyright 2009 University of Zurich, Switzerland
 *
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
 */
package org.evolizer.changedistiller.treedifferencing.operation;

import org.evolizer.changedistiller.treedifferencing.ITreeEditOperation;
import org.evolizer.changedistiller.treedifferencing.Node;

/**
 * Representation of the update basic {@link ITreeEditOperation}.
 * 
 * @author fluri
 * 
 */
public class UpdateOperation implements ITreeEditOperation {

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
        fNodeToUpdate.getEntity().setUniqueName(value);
        fValue = value;
    }

    /**
     * {@inheritDoc}
     */
    public void apply() {
        if (!fApplied) {
            // fNodeToUpdate.setValue(fValue);
            fApplied = true;
        }
    }

    /**
     * Returns the old value of the {@link Node} before it was updated.
     * 
     * @return the old value of the node
     */
    public String getOldValue() {
        return fOldValue;
    }

    /**
     * Returns the {@link Node} to update.
     * 
     * @return the node to update
     */
    public Node getNodeToUpdate() {
        return fNodeToUpdate;
    }

    /**
     * {@inheritDoc}
     */
    public int getOperationType() {
        return ITreeEditOperation.UPDATE;
    }

    /**
     * Returns the updated {@link Node}.
     * 
     * @return the updated node
     */
    public Node getNewNode() {
        return fNewNode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("--Update operation--\n");
        sb.append("Node value to update: ");
        sb.append(fOldValue);
        sb.append("\nwith value: ");
        sb.append(fValue);
        return sb.toString();
    }
}
