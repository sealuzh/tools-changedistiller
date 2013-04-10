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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import ch.uzh.ifi.seal.changedistiller.model.classifiers.EntityType;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;

/**
 * General tree node.
 * 
 * <p>
 * {@link TreeDifferencer} can only apply the matching and edit script generation if the two trees are made out of such
 * nodes.
 * 
 * @author Beat Fluri
 * @see TreeDifferencer
 */
public class Node extends DefaultMutableTreeNode {

    private static final long serialVersionUID = 42L;

    private EntityType fLabel;
    private String fValue;

    private boolean fMatched;
    private boolean fOrdered;

    private SourceCodeEntity fEntity;
    private List<Node> fAssociatedNodes = new ArrayList<Node>();

    /**
     * Creates a new node.
     * 
     * @param label
     *            of the node
     * @param value
     *            of the node
     */
    public Node(EntityType label, String value) {
        super();
        fLabel = label;
        fValue = value;
    }

    /**
     * The node is not matched with another node.
     */
    public void disableMatched() {
        fMatched = false;
    }

    /**
     * The node is matched with another node.
     */
    public void enableMatched() {
        fMatched = true;
    }

    /**
     * Returns whether this node is matched with another node.
     * 
     * @return <code>true</code> if this node is match with another node, <code>false</code> otherwise
     */
    public boolean isMatched() {
        return fMatched;
    }

    /**
     * The node is out of order with its siblings.
     */
    public void enableOutOfOrder() {
        fOrdered = false;
    }

    /**
     * The node is in order with its siblings.
     */
    public void enableInOrder() {
        fOrdered = true;
    }
    
    /**
     * Returns whether this node is in order with its siblings.
     * 
     * @return <code>true</code> if this node is in order with its siblings, <code>false</code> otherwise
     */
    public boolean isInOrder() {
        return fOrdered;
    }
    
    public EntityType getLabel() {
        return fLabel;
    }

    public void setLabel(EntityType label) {
        fLabel = label;
    }

    public String getValue() {
        return fValue;
    }

    public void setValue(String value) {
        fValue = value;
    }

    public List<Node> getAssociatedNodes() {
        return fAssociatedNodes;
    }

    /**
     * Adds an associated node to this node.
     * 
     * @param node
     *            to add as associated node
     */
    public void addAssociatedNode(Node node) {
        fAssociatedNodes.add(node);
        getEntity().addAssociatedEntity(node.getEntity());
    }

    @Override
    public String toString() {
    	StringBuilder sb = new StringBuilder();
    	sb.append("(value: ");
    	
    	if(fValue == null || fValue.equals("")) {
    		sb.append("none)");
    	} else {
    		sb.append(fValue)
    		  .append(')');
    	}
    	
    	sb.append("(label: ");
    	
    	if(fLabel == null) {
    		sb.append("none)");
    	} else {
    	  sb.append(fLabel.toString())
    	  .append(')');
    	}
    	
        return sb.toString();
    }

    public SourceCodeEntity getEntity() {
        return fEntity;
    }

    public void setEntity(SourceCodeEntity entity) {
        fEntity = entity;
    }

    /**
     * Prints this {@link Node} and its children with <code>value ['{' child [, child]* '}']</code>.
     * 
     * @param output
     *            to append the node string
     * @return the node string
     */
    @SuppressWarnings("unchecked")
    public StringBuilder print(StringBuilder output) {
        output.append(getValue());
        if (!isLeaf()) {
            output.append(" { ");
            for (Iterator<Node> it = children.iterator(); it.hasNext();) {
                Node child = it.next();
                child.print(output);
                if (it.hasNext()) {
                    output.append(",");
                }
            }
            output.append(" }");
        }
        return output;
    }

// Removing hashCode and equals fixes issues #1, #8, #9. See https://bitbucket.org/sealuzh/tools-changedistiller/issue/1.
    
//    @Override
//    public int hashCode() {
//        final int prime = 31;
//        int result = 1;
//        result = prime * result + ((fLabel == null) ? 0 : fLabel.hashCode());
//        result = prime * result + ((fValue == null) ? 0 : fValue.hashCode());
//        return result;
//    }
//
//    @Override
//    public boolean equals(Object obj) {
//        if (this == obj) {
//            return true;
//        }
//        if (obj == null) {
//            return false;
//        }
//        if (getClass() != obj.getClass()) {
//            return false;
//        }
//        Node other = (Node) obj;
//        if (fLabel == null) {
//            if (other.fLabel != null) {
//                return false;
//            }
//        } else if (!fLabel.equals(other.fLabel)) {
//            return false;
//        }
//        if (fValue == null) {
//            if (other.fValue != null) {
//                return false;
//            }
//        } else if (!fValue.equals(other.fValue)) {
//            return false;
//        }
//        return true;
//    }

    /**
     * Returns true if the leaf candidate is a leaf and is a descendant of this node.
     * 
     * @param candidate
     *            to check for leaf descendant
     * @return <code>true</code> if the candidate is a leaf and a descendant of this node, <code>false</code> otherwise
     */
    public boolean isLeafDescendant(Node candidate) {
        return candidate.isLeaf() && isNodeDescendant(candidate);
    }

}
