package org.evolizer.changedistiller.model.entities;

import static org.evolizer.changedistiller.model.classifiers.SignificanceLevel.CRUCIAL;
import static org.evolizer.changedistiller.model.classifiers.SignificanceLevel.HIGH;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.evolizer.changedistiller.model.classifiers.ChangeType;
import org.evolizer.changedistiller.model.classifiers.SignificanceLevel;

/**
 * Source code change that represents the update of an entity.
 * <p>
 * In addition to the involved entities described in {@link SourceCodeChange} an update has a
 * <ul>
 * <li>{@link SourceCodeEntity} <code>newEntity</code> that describes which entity <code>changedEntity</code> becomes
 * after the update. <code>newEntity</code> is taken from the right AST, i.e., new version.</li>
 * <li>{@link SourceCodeEntity} <code>parentEntity</code> that describes in which entity <code>changedEntity</code> is
 * updated. Speaking of an AST, <code>parentEntity</code> describes the node in which <code>changedEntity</code> is a
 * child before and after the update. <code>parentEntity</code> is taken from the left AST, i.e., old version. In case
 * <code>parentEntity</code> was inserted (see {@link SourceCodeChange}), it is taken from the right AST.</li>
 * </ul>
 * 
 * @author fluri, zubi
 * @see SourceCodeChange
 */
public class Update extends SourceCodeChange {

    /**
     * Source code entity that the updated entity becomes when applying the change.
     */
    private SourceCodeEntity fNewEntity;

    /**
     * Default constructor. Only used by Hiberante.
     */
    @SuppressWarnings("unused")
    private Update() {}

    /**
     * Constructor to initialize an update operation. The updated entity is updated to new entity as a child of parent
     * entity in structure entity.
     * 
     * @param changeType
     *            the change type
     * @param rootEntity
     *            the root entity
     * @param updatedEntity
     *            the updated entity
     * @param newEntity
     *            the new entity
     * @param parentEntity
     *            the parent entity
     */
    public Update(
            ChangeType changeType,
            StructureEntityVersion rootEntity,
            SourceCodeEntity updatedEntity,
            SourceCodeEntity newEntity,
            SourceCodeEntity parentEntity) {
        this(rootEntity, updatedEntity, newEntity, parentEntity);
        setChangeType(changeType);
    }

    /**
     * Instantiates a new update.
     * 
     * @param rootEntity
     *            the root entity
     * @param updatedEntity
     *            the updated entity
     * @param newEntity
     *            the new entity
     * @param parentEntity
     *            the parent entity
     */
    public Update(
            StructureEntityVersion rootEntity,
            SourceCodeEntity updatedEntity,
            SourceCodeEntity newEntity,
            SourceCodeEntity parentEntity) {
        super(updatedEntity, parentEntity, rootEntity);
        setNewEntity(newEntity);
    }

    /**
     * Returns the new entity.
     * 
     * @return source code entity that updated entity becomes after update.
     */
    public SourceCodeEntity getNewEntity() {
        return fNewEntity;
    }

    /**
     * Set source code entity that updated entity becomes after update.
     * 
     * @param newEntity
     *            the new entity
     */
    public void setNewEntity(SourceCodeEntity newEntity) {
        fNewEntity = newEntity;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(13, 31).appendSuper(super.hashCode()).append(getNewEntity())
                .append(getParentEntity()).toHashCode();
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
        Update other = (Update) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(getNewEntity(), other.getNewEntity())
                .append(getParentEntity(), other.getParentEntity()).isEquals();
    }

    @Override
    protected SignificanceLevel liftSignificanceLevel() {
        switch (getChangeType()) {
            case CLASS_RENAMING:
                return checkRootEntitySignificance(HIGH);
            case DECREASING_ACCESSIBILITY_CHANGE:
                return checkChangedEntitySignificance(CRUCIAL);
            case ATTRIBUTE_RENAMING:
            case METHOD_RENAMING:
                if (getNewEntity().isProtected() || getNewEntity().isPublic()) {
                    return HIGH;
                }
            case ATTRIBUTE_TYPE_CHANGE:
            case PARAMETER_TYPE_CHANGE:
            case RETURN_TYPE_CHANGE:
                return checkRootEntitySignificance(CRUCIAL);
            default:
                return getChangeType().getSignificance();
        }
    }
}
