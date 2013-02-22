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
 * Representation of the move basic {@link TreeEditOperation}.
 * 
 * @author Beat Fluri
 * 
 */
public class MoveOperation implements TreeEditOperation {

    private static final String LEFT_PARENTHESIS = " (";
    private static final String RIGHT_PARENTHESIS = ")";
    private Node fNodeToMove;
    private Node fOldParent;
    private Node fNewParent;
    private Node fNewNode;
    private int fPosition = -1;
    private boolean fApplied;

    /**
     * Creates a new move operation.
     * 
     * @param nodeToMove
     *            the node to move
     * @param newNode
     *            the node the moved node becomes
     * @param parent
     *            the parent node in which the node becomes a child after move
     * @param position
     *            the position of the node to move
     */
    public MoveOperation(Node nodeToMove, Node newNode, Node parent, int position) {
        fNodeToMove = nodeToMove;
        fNewNode = newNode;
        fOldParent = (Node) nodeToMove.getParent();
        fNewParent = parent;
        fPosition = position;
    }

    @Override
    public void apply() {
        if (!fApplied) {
            if (fNewParent.getChildCount() <= fPosition) {
                fNewParent.add(fNodeToMove);
            } else {
                fNewParent.insert(fNodeToMove, fPosition);
            }
            fApplied = true;
        }
    }

    public Node getNodeToMove() {
        return fNodeToMove;
    }

    public Node getOldParent() {
        return fOldParent;
    }

    public Node getNewParent() {
        return fNewParent;
    }

    @Override
    public OperationType getOperationType() {
        return OperationType.MOVE;
    }

    public Node getNewNode() {
        return fNewNode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("--Move operation--\n");
        sb.append("Node value to move: ");
        sb.append(fNodeToMove.toString() + LEFT_PARENTHESIS + fNodeToMove.getLabel() + RIGHT_PARENTHESIS);
        sb.append("\nas child of: ");
        sb.append(fNewParent.toString() + LEFT_PARENTHESIS + fNewParent.getLabel() + RIGHT_PARENTHESIS);
        sb.append("\nat position: ");
        sb.append(fPosition);
        return sb.toString();
    }
}
