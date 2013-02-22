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
