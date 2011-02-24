package org.evolizer.changedistiller.model.classifiers;

import org.evolizer.changedistiller.model.entities.SourceCodeEntity;

/**
 * All types for source code entities that are used by ChangeDistiller to build up the AST (abstract syntax tree). Most
 * are taken from {@link org.eclipse.jdt.core.dom.ASTNode}.
 * 
 * @author zubi
 * @see SourceCodeEntity
 */
public interface EntityType {

    /**
     * Returns whether changes occurred on this source code entity type are extracted by ChangeDistiller or not (e.g.
     * changes in the <code>finally</code> clause are ignored).
     * 
     * @return <code>true</code> if changes on this entity type are considered and extracted, <code>false</code>
     *         otherwise.
     */
    boolean isValidChange();

    /**
     * Returns the name of the entity type.
     * 
     * @return the name of the entity type
     */
    String name();

}
