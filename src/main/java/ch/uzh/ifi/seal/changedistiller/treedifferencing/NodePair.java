package ch.uzh.ifi.seal.changedistiller.treedifferencing;

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

/**
 * A pair of nodes.
 * 
 * @author Beat Fluri
 * 
 */
public class NodePair {

    private Node fLeft;
    private Node fRight;

    /**
     * Creates a new node pair
     * 
     * @param left
     *            node of the pair
     * @param right
     *            node of the pair
     */
    public NodePair(Node left, Node right) {
        fLeft = left;
        fRight = right;
    }

    public Node getLeft() {
        return fLeft;
    }

    public Node getRight() {
        return fRight;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fLeft == null) ? 0 : fLeft.hashCode());
        result = prime * result + ((fRight == null) ? 0 : fRight.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        NodePair other = (NodePair) obj;
        if (fLeft == null) {
            if (other.fLeft != null) {
                return false;
            }
        } else if (!fLeft.equals(other.fLeft)) {
            return false;
        }
        if (fRight == null) {
            if (other.fRight != null) {
                return false;
            }
        } else if (!fRight.equals(other.fRight)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getLeft().toString() + " == " + getRight().toString();
    }
}
