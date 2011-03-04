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
 * Representation of the insert basic {@link ITreeEditOperation}.
 * 
 * @author fluri
 * 
 */
public class InsertOperation implements ITreeEditOperation {

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

    /**
     * {@inheritDoc}
     */
    public void apply() {
        if (!fApplied) {
            fParent.insert(fNodeToInsert, fPosition);
            fApplied = true;
        }
    }

    /**
     * Returns the {@link Node} to insert.
     * 
     * @return the node to insert
     */
    public Node getNodeToInsert() {
        return fNodeToInsert;
    }

    /**
     * Returns the parent {@link Node} of the {@link Node} to insert.
     * 
     * @return the parent node of the node to insert
     */
    public Node getParentNode() {
        return fParent;
    }

    /**
     * {@inheritDoc}
     */
    public int getOperationType() {
        return ITreeEditOperation.INSERT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
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
