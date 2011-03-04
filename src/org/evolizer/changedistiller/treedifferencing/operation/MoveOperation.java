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
 * Representation of the move basic {@link ITreeEditOperation}.
 * 
 * @author fluri
 * 
 */
public class MoveOperation implements ITreeEditOperation {

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

    /**
     * {@inheritDoc}
     */
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

    /**
     * Returns the {@link Node} to move.
     * 
     * @return the node to move
     */
    public Node getNodeToMove() {
        return fNodeToMove;
    }

    /**
     * Returns the parent {@link Node} of the node to move before the node was moved
     * 
     * @return the old parent of the node to move
     */
    public Node getOldParent() {
        return fOldParent;
    }

    /**
     * Return the parent {@link Node} of the node to move after the node was moved
     * 
     * @return the new parent of the node to move
     */
    public Node getNewParent() {
        return fNewParent;
    }

    /**
     * {@inheritDoc}
     */
    public int getOperationType() {
        return ITreeEditOperation.MOVE;
    }

    /**
     * Returns the {@link Node} the node to move becomes after it was moved
     * 
     * @return the new node
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
