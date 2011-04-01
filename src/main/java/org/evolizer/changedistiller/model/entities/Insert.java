package org.evolizer.changedistiller.model.entities;

import static org.evolizer.changedistiller.model.classifiers.SignificanceLevel.CRUCIAL;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.evolizer.changedistiller.model.classifiers.ChangeType;
import org.evolizer.changedistiller.model.classifiers.SignificanceLevel;

/**
 * Source code change that represents the insert of an entity.
 * 
 * <p>
 * In addition to the involved entities described in {@link SourceCodeChange} an insert has a
 * <ul>
 * <li>{@link SourceCodeEntity} <code>parentEntity</code> that describes in which entity <code>changedEntity</code> is
 * inserted. Speaking of an AST, <code>parentEntity</code> describes the node in which <code>changedEntity</code> will
 * be a child after the insert. <code>parentEntity</code> is taken from the left AST, i.e., old version. In case
 * <code>parentEntity</code> was inserted (see {@link SourceCodeChange}), it is taken from the right AST.</li>
 * </ul>
 * 
 * @author Beat Fluri
 * @author zubi
 * @see SourceCodeChange
 */
public class Insert extends SourceCodeChange {

    /**
     * Creates a new insert operation.
     * <p>
     * The inserted entity is inserted as a child of parent entity inside the structure entity.
     * 
     * @param changeType
     *            the change type
     * @param rootEntity
     *            the root entity
     * @param insertedEntity
     *            the inserted entity
     * @param parentEntity
     *            the parent entity
     */
    public Insert(
            ChangeType changeType,
            StructureEntityVersion rootEntity,
            SourceCodeEntity insertedEntity,
            SourceCodeEntity parentEntity) {
        this(rootEntity, insertedEntity, parentEntity);
        setChangeType(changeType);
    }

    /**
     * Creates a new insert operation.
     * 
     * @param insertedEntity
     *            the inserted entity
     * @param parentEntity
     *            the parent entity
     * @param rootEntity
     *            the root entity
     */
    public Insert(StructureEntityVersion rootEntity, SourceCodeEntity insertedEntity, SourceCodeEntity parentEntity) {
        super(insertedEntity, parentEntity, rootEntity);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(19, 31).appendSuper(super.hashCode()).append(getParentEntity()).toHashCode();
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
        Insert other = (Insert) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(getParentEntity(), other.getParentEntity())
                .isEquals();
    }

    @Override
    protected SignificanceLevel liftSignificanceLevel() {
        switch (getChangeType()) {
            case RETURN_TYPE_INSERT:
            case REMOVING_ATTRIBUTE_MODIFIABILITY:
            case PARAMETER_INSERT:
                return checkRootEntitySignificance(CRUCIAL);
            case DECREASING_ACCESSIBILITY_CHANGE:
                return checkChangedEntitySignificance(CRUCIAL);
            default:
                return getChangeType().getSignificance();
        }
    }
}
