package org.evolizer.changedistiller.model.classifiers;

/**
 * Interface to facility the work with {@link EntityType}.
 * 
 * @param <T>
 *            entity type
 * 
 * @author Beat Fluri
 */
public interface EntityTypeUtils<T extends EntityType> {

    /**
     * Returns number of defined entity types.
     * 
     * @return number of entity types.
     */
    int getNumberOfEntityTypes();

    /**
     * Returns whether the given entity type is a type of a type declaration or not.
     * 
     * @param type
     *            to analyze
     * @return <code>true</code> if given entity type is a type, <code>false</code> otherwise.
     */
    boolean isType(T type);

    /**
     * Returns whether the given entity type is a statement or not.
     * 
     * @param type
     *            to analyze
     * @return <code>true</code> if given entity type is a statement, <code>false</code> otherwise.
     */
    boolean isAtStatementLevel(T type);

}
