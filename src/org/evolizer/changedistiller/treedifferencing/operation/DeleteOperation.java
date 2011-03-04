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
 * Representation of the delete basic {@link ITreeEditOperation}.
 * 
 * @author fluri
 * 
 */
public class DeleteOperation implements ITreeEditOperation {

    private Node fNodeToDelete;
    private Node fParent;
    private boolean fApplied;

    /**
     * Creates a new delete operation.
     * 
     * @param nodeToDelete
     *            the node to delete
     */
    public DeleteOperation(Node nodeToDelete) {
        fNodeToDelete = nodeToDelete;
        fParent = (Node) fNodeToDelete.getParent();
    }

    /**
     * {@inheritDoc}
     */
    public void apply() {
        if (!fApplied) {
            fNodeToDelete.removeFromParent();
            fApplied = true;
        }
    }

    /**
     * Returns the {@link Node} to delete.
     * 
     * @return the node to delete
     */
    public Node getNodeToDelete() {
        return fNodeToDelete;
    }

    /**
     * Returns the parent {@link Node} of the {@link Node} to delete.
     * 
     * @return the parent node of the node to delete
     */
    public Node getParentNode() {
        return fParent;
    }

    /**
     * {@inheritDoc}
     */
    public int getOperationType() {
        return ITreeEditOperation.DELETE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("--Delete operation--\n");
        sb.append("Node to delete: ");
        sb.append(fNodeToDelete.toString() + " (" + fNodeToDelete.getLabel() + ")");
        return sb.toString();
    }
}
