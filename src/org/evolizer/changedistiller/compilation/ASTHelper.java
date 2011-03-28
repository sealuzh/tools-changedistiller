package org.evolizer.changedistiller.compilation;

import org.evolizer.changedistiller.model.classifiers.EntityType;
import org.evolizer.changedistiller.model.entities.SourceCodeEntity;
import org.evolizer.changedistiller.model.entities.StructureEntityVersion;
import org.evolizer.changedistiller.structuredifferencing.StructureNode;
import org.evolizer.changedistiller.treedifferencing.Node;

/**
 * Handles language specific ASTs and provides access to their content. An AST helper is associated to a file.
 * 
 * @param <T>
 *            subtype of {@link StructureNode} with which the AST helper works
 * 
 * @author Beat Fluri
 */
public interface ASTHelper<T extends StructureNode> {

    /**
     * Creates and returns a {@link StructureNode} tree of the associated file.
     * 
     * @return the structure node tree of the associated file
     */
    T createStructureTree();

    /**
     * Creates and returns the declaration {@link Node} tree for the {@link StructureNode}.
     * 
     * @param node
     *            to create the declaration tree
     * @return the declaration tree for the structure node
     */
    Node createDeclarationTree(T node);

    /**
     * Creates and returns the method body {@link Node} tree for the {@link StructureNode}.
     * 
     * @param node
     *            to create the method body tree
     * @return the method body tree for the structure node
     */
    Node createMethodBodyTree(T node);

    /**
     * Converts and returns the type of the AST entity that is associated to the {@link StructureNode} to an
     * {@link EntityType}.
     * 
     * @param node
     *            to convert its type
     * @return the entity type of the AST entity associated to the structure node
     */
    EntityType convertType(T node);

    /**
     * Creates and returns the {@link SourceCodeEntity} for the {@link StructureNode}.
     * 
     * @param node
     *            the node to create the source code entity for
     * @return the source code entity for the node
     */
    SourceCodeEntity createSourceCodeEntity(T node);

    /**
     * Creates and returns the {@link StructureEntityVersion} for the {@link StructureNode}.
     * 
     * @param node
     *            the node to create the structure entity version for
     * @return the structure entity version for the node
     */
    StructureEntityVersion createStructureEntityVersion(T node);

}
