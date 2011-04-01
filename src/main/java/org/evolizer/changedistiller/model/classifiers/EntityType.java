package org.evolizer.changedistiller.model.classifiers;

import org.evolizer.changedistiller.model.entities.SourceCodeEntity;

/**
 * All types for source code entities that are used by ChangeDistiller to build up the AST (abstract syntax tree).
 * 
 * @author zubi
 * @see SourceCodeEntity
 */
public interface EntityType {

    /**
     * Returns whether or not ChangeDistiller should extract changes occurred on this source code entity type (e.g.,
     * changes in the <code>finally</code> clause are ignored).
     * 
     * @return <code>true</code> if ChangeDistiller should extract changes on this entity type, <code>false</code>
     *         otherwise.
     */
    boolean isUsableForChangeExtraction();

    /**
     * Returns the name of the entity type.
     * 
     * @return the name of the entity type
     */
    String name();

    /**
     * Returns whether or not the entity type describes a comment.
     * 
     * @return <code>true</code> if the entity type describes a comment, <code>false</code> otherwise
     */
    boolean isComment();

    /**
     * Returns whether or not the entity type describes a structure entity, e.g., a class, field, or method.
     * 
     * @return <code>true</code> if the entity type describes a structure entity, <code>false</code> otherwise
     */
    boolean isStructureEntityType();

    /**
     * Returns whether or not this entity type is a type.
     * 
     * @return <code>true</code> if this entity type is a type, <code>false</code> otherwise
     */
    boolean isType();

    /**
     * Returns whether or not this entity type is at statement level.
     * 
     * @return <code>true</code> if this entity type is at statement level, <code>false</code> otherwise
     */
    boolean isStatement();

    /**
     * Returns whether or not this entity type is a field.
     * 
     * @return <code>true</code> if the entity is a field, <code>false</code> otherwise
     */
    boolean isField();

    /**
     * Returns whether or not this entity type is a class.
     * 
     * @return <code>true</code> if the entity is a class, <code>false</code> otherwise
     */
    boolean isClass();

    /**
     * Returns whether or not this entity type is a method.
     * 
     * @return <code>true</code> if the entity is a method, <code>false</code> otherwise
     */
    boolean isMethod();

}
