package org.evolizer.changedistiller.model.entities;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.evolizer.changedistiller.model.classifiers.ChangeType;
import org.evolizer.changedistiller.model.classifiers.SignificanceLevel;

/**
 * General (abstract) representation of a source code change.
 * <p>
 * Each source code change has a
 * <ul>
 * <li>{@link StructureEntityVersion} <code>rootEntity</code> that describes in which attribute, class, or method the
 * changes was made. The <code>rootEntity</code> is taken from the right AST, i.e., new version of the
 * {@link StructureEntityVersion}.</li>
 * <li>{@link SourceCodeEntity} <code>changedEntity</code> that describes which source code entity has changed. In case
 * of
 * <ul>
 * <li>{@link Delete}: the entity that was deleted; it is taken from the left AST, i.e., old version.</li>
 * <li>{@link Insert}: the entity that was inserted; it is taken from the right AST, i.e., new version.</li>
 * <li>{@link Move}: the entity that was moved; it is taken from the left AST, i.e., old version.</li>
 * <li>{@link Update}: the entity that was updated; it is taken from the left AST, i.e., old version.</li>
 * </ul>
 * </ul>
 * Other entities involved in a source code change are described in the corresponding concrete implementation.
 * 
 * @author fluri, zubi
 * @see Delete
 * @see Insert
 * @see Move
 * @see Update
 */
public class SourceCodeChange {

    private ChangeType fChangeType = ChangeType.UNCLASSIFIED_CHANGE;

    /**
     * Structure entity in which the change operation happened, e.g., attribute, class, or method.
     */
    private StructureEntityVersion fRootEntity;

    /**
     * Changed entity.
     */
    private SourceCodeEntity fChangedEntity;

    /**
     * Source code entity that becomes the parent entity when the change is applied.
     */
    private SourceCodeEntity fParentEntity;

    /**
     * Default constructor. Used by Hibernate.
     */
    SourceCodeChange() {}

    /**
     * Instantiates a new source code change.
     * 
     * @param changedEntity
     *            the changed entity
     * @param parentEntity
     *            the parent entity
     * @param rootEntity
     *            the root entity
     * @param changeType
     *            the change type
     */
    SourceCodeChange(
            SourceCodeEntity changedEntity,
            SourceCodeEntity parentEntity,
            StructureEntityVersion rootEntity,
            ChangeType changeType) {
        this(changedEntity, parentEntity, rootEntity);
        setChangeType(changeType);
    }

    /**
     * Instantiates a new source code change.
     * 
     * @param changedEntity
     *            the changed entity
     * @param parentEntity
     *            the parent entity
     * @param rootEntity
     *            the root entity
     */
    SourceCodeChange(SourceCodeEntity changedEntity, SourceCodeEntity parentEntity, StructureEntityVersion rootEntity) {
        setChangedEntity(changedEntity);
        setParentEntity(parentEntity);
        setRootEntity(rootEntity);
    }

    /**
     * Sets the changed entity.
     * 
     * @param changedEntity
     *            the changed entity
     */
    public void setChangedEntity(SourceCodeEntity changedEntity) {
        fChangedEntity = changedEntity;
    }

    /**
     * Returns the changed entity.
     * 
     * @return the changed entity
     */
    public SourceCodeEntity getChangedEntity() {
        return fChangedEntity;
    }

    /**
     * Sets the root entity.
     * 
     * @param rootEntity
     *            the rootEntity to set
     */
    public void setRootEntity(StructureEntityVersion rootEntity) {
        fRootEntity = rootEntity;
    }

    /**
     * Returns the root entity.
     * 
     * @return the rootEntity
     */
    public StructureEntityVersion getRootEntity() {
        return fRootEntity;
    }

    /**
     * Returns the change type.
     * 
     * @return change type of this change
     */
    public ChangeType getChangeType() {
        return fChangeType;
    }

    /**
     * Sets the change type.
     * 
     * @param changeType
     *            the change type to set
     */
    public void setChangeType(ChangeType changeType) {
        fChangeType = changeType;
    }

    /**
     * Returns string representation of this source code change, i.e.
     * 
     * <pre>
     * &lt;changeOperation&gt;: &lt;uniqueNameOfChangedEntity&gt;
     * </pre>
     * <p>
     * For debugging purposes only.
     * 
     * @return string representation
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + getChangedEntity().getUniqueName();
    }

    /**
     * Returns the significance level.
     * 
     * @return the significance level of this source code change
     */
    public final SignificanceLevel getSignificanceLevel() {
        if (getChangeType().hasUnstableSignificanceLevel()) {
            return liftSignificanceLevel();
        }
        return getChangeType().getSignificance();
    }

    /**
     * Checks whether the significance level of this change has to be lifted to a higher level or not.
     * 
     * @param newLevel
     *            the new level
     * @return the proper significance level
     */
    protected SignificanceLevel checkRootEntitySignificance(SignificanceLevel newLevel) {
        if (getRootEntity().isProtected() || getRootEntity().isPublic()) {
            return newLevel;
        }
        return getChangeType().getSignificance();
    }

    /**
     * Checks whether the significance level of this change has to be lifted to a higher level or not.
     * 
     * @param newLevel
     *            the new level
     * @return the proper significance level
     */
    protected SignificanceLevel checkChangedEntitySignificance(SignificanceLevel newLevel) {
        if (getChangedEntity().isProtected() || getChangedEntity().isPublic()) {
            return newLevel;
        }
        return getChangeType().getSignificance();
    }

    /**
     * Returns label of the {@link ChangeType} this change represents.
     * 
     * @return label for this change.
     */
    public String getLabel() {
        return getChangeType().toString();
    }

    /**
     * Hook for subclasses to lift significance level for certain {@link ChangeType}s.
     * 
     * @return the significance level
     * @see #getSignificanceLevel()
     */
    protected SignificanceLevel liftSignificanceLevel() {
        return null;
    }

    /**
     * Returns the parent entity.
     * 
     * @return source code entity under which this change was applied.
     */
    public SourceCodeEntity getParentEntity() {
        return fParentEntity;
    }

    /**
     * Sets entity from which the deleted entity is deleted.
     * 
     * @param parentEntity
     *            the parent entity
     */
    public void setParentEntity(SourceCodeEntity parentEntity) {
        fParentEntity = parentEntity;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(77, 103).append(getChangeType()).append(getSignificanceLevel())
                .append(getChangedEntity()).append(getParentEntity()).append(getRootEntity()).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SourceCodeChange other = (SourceCodeChange) obj;
        return new EqualsBuilder().append(getChangeType(), other.getChangeType())
                .append(getSignificanceLevel(), other.getSignificanceLevel())
                .append(getChangedEntity(), other.getChangedEntity())
                .append(getParentEntity(), other.getParentEntity()).append(getRootEntity(), other.getRootEntity())
                .isEquals();
    }

}
