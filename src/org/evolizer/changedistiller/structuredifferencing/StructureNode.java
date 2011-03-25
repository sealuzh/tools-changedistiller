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
     * @return the the name of the node
     */
    String getName();

}
