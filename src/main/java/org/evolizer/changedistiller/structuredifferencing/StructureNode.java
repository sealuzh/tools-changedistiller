package org.evolizer.changedistiller.structuredifferencing;

import java.util.List;

/**
 * Node for structure differencing.
 * 
 * @author Beat Fluri
 * @see WhenStructureDifferencesAreExtracted
 */
public interface StructureNode {

    /**
     * Returns the children of this structure node.
     * 
     * @return the children of the node
     */
    List<? extends StructureNode> getChildren();

    /**
     * Returns the content of this structure node.
     * 
     * @return the content of the node
     */
    String getContent();

    /**
     * Returns the name of this structure node.
     * 
     * @return the name of the node
     */
    String getName();

    /**
     * Retruns the fully qualified name of this structure node.
     * 
     * @return the fully qualified name of the node
     */
    String getFullyQualifiedName();

    /**
     * Returns whether or not the node is a class or interface.
     * 
     * @return <code>true</code> if the node is a class or interface, <code>false</code> otherwise
     */
    boolean isClassOrInterface();

    /**
     * Returns whether of not the node is a method or constructor.
     * 
     * @return <code>true</code> if the node is a method or constructor, <code>false</code> otherwise
     */
    boolean isMethodOrConstructor();

    /**
     * Returns whether of not the node is a field.
     * 
     * @return <code>true</code> if the node is a field, <code>false</code> otherwise
     */
    boolean isField();

    /**
     * Returns whether or not the node is of same type as the other .
     * 
     * @param other
     *            to check the type with
     * @return <code>true</code> if the node is of same type as the other, <code>false</code> otherwise
     */
    boolean isOfSameTypeAs(StructureNode other);

}
