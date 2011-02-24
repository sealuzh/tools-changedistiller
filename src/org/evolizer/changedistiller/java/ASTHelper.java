package org.evolizer.changedistiller.java;

import org.evolizer.changedistiller.model.classifiers.EntityType;

/**
 * AST helper should extend this class to facilitate receiving AST information.
 * 
 * @author Beat Fluri
 */
public interface ASTHelper {

    /**
     * Converts given node into an {@link EntityType}.
     * 
     * @param node
     *            to convert
     * @return entity type of the node
     */
    EntityType convertNode(Object node);

}
