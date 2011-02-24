package org.evolizer.changedistiller.treedifferencing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;

import org.evolizer.changedistiller.model.classifiers.EntityType;
import org.evolizer.changedistiller.model.entities.SourceCodeEntity;

/**
 * General tree node.
 * 
 * <p>
 * {@link TreeDifferencer} can only apply the matching and edit script generation if the two trees are made out of such
 * nodes.
 * 
 * @author fluri
 * @see TreeDifferencer
 */
public class Node extends DefaultMutableTreeNode {

    private static final long serialVersionUID = 42L;

    private boolean fMatched;
    private boolean fOrdered;
    private EntityType fLabel;
    private String fValue;
    private SourceCodeEntity fEntity;
    private List<Node> fAssociatedNodes = new ArrayList<Node>();

    /**
     * Creates a new node.
     */
    public Node() {
        super();
    }

    /**
     * Creates a new node.
     * 
     * @param userObject
     *            the object to attach to the node
     * @param allowsChildren
     *            <code>true</code> if this node accepts children, <code>false</code> otherwise
     */
    public Node(Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
    }

    /**
     * Creates a new node.
     * 
     * @param userObject
     *            the object to attach to the node
     */
    public Node(Object userObject) {
        super(userObject);
    }

    /**
     * Creates a new node.
     * 
     * @param label
     *            the label this node has
     * @param value
     *            the value this node has
     * @param entity
     *            the entity that node represents
     */
    public Node(EntityType label, String value, SourceCodeEntity entity) {
        super();
        fLabel = label;
        fValue = value;
        fEntity = entity;
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

    /**
     * Returns the label of this node.
     * 
     * @return the label of this node
     */
    public EntityType getLabel() {
        return fLabel;
    }

    /**
     * Returns the value of this node.
     * 
     * @return the value of this node
     */
    public String getValue() {
        return fValue;
    }

    /**
     * Sets the value of this node.
     * 
     * @param value
     *            of this node
     */
    public void setValue(String value) {
        fValue = value;
    }

    /**
     * Returns the associated nodes of this node. Normarlly used for comment and source association.
     * 
     * @return the associated nodes of this node
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getValue();
    }

    /**
     * Returns the {@link SourceCodeEntity} that this node represents
     * 
     * @return the source code entity this node represents
     */
    public SourceCodeEntity getEntity() {
        return fEntity;
    }

    /**
     * Prints this {@link Node} and its children with <code>value ['{' child [, child...] '}']</code>.
     * 
     * @param output
     *            to append the node string
     * @return the node string
     */
    public StringBuilder print(StringBuilder output) {
        output.append(getValue());
        if (!isLeaf()) {
            output.append(" { ");
            for (Iterator<?> it = children.iterator(); it.hasNext();) {
                Node child = (Node) it.next();
                child.print(output);
                if (it.hasNext()) {
                    output.append(",");
                }
            }
            output.append(" }");
        }
        return output;
    }
}
