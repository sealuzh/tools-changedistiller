package ch.uzh.ifi.seal.changedistiller.ast;

import ch.uzh.ifi.seal.changedistiller.model.classifiers.EntityType;

/**
 * AST node type converters should extend this class to facilitate conversion of AST node types.
 * 
 * @author Beat Fluri
 */
public interface ASTNodeTypeConverter {

    /**
     * Converts and returns the given node into an {@link EntityType}.
     * 
     * @param node
     *            to convert
     * @return entity type of the node
     */
    EntityType convertNode(Object node);

}
