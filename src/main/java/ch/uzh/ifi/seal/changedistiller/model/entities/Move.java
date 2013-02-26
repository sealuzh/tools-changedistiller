package ch.uzh.ifi.seal.changedistiller.model.entities;

/*
 * #%L
 * ChangeDistiller
 * %%
 * Copyright (C) 2011 - 2013 Software Architecture and Evolution Lab, Department of Informatics, UZH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static ch.uzh.ifi.seal.changedistiller.model.classifiers.SignificanceLevel.CRUCIAL;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ch.uzh.ifi.seal.changedistiller.model.classifiers.ChangeType;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.SignificanceLevel;

/**
 * Source code change that represents the move of an entity.
 * <p>
 * In addition to the involved entities described in {@link SourceCodeChange} an insert has a
 * <ul>
 * <li>{@link SourceCodeEntity} <code>oldParentEntity</code> that describes from which entity <code>changedEntity</code>
 * is deleted. Speaking of an AST, <code>parentEntity</code> describes the node in which <code>changedEntity</code> was
 * a child before move. <code>parentEntity</code> is taken from the left AST, i.e., old version.</li>
 * <li>{@link SourceCodeEntity} <code>newParentEntity</code> that describes in which entity <code>changedEntity</code>
 * is inserted. Speaking of an AST, <code>parentEntity</code> describes the node in which <code>changedEntity</code>
 * will be a child after the move. <code>parentEntity</code> is taken from the left AST, i.e., old version. In case
 * <code>parentEntity</code> was inserted (see {@link SourceCodeChange}), it is taken from the right AST.</li>
 * <li>{@link SourceCodeEntity} <code>newEntity</code> that describes which entity <code>changedEntity</code> becomes
 * after the move. <code>newEntity</code> is taken from the right AST, i.e., new version.</li>
 * </ul>
 * 
 * @author Beat Fluri
 * @author zubi
 * @see SourceCodeChange
 */
public class Move extends SourceCodeChange {

    /**
     * Source code entity that becomes the parent entity when the change is applied.
     */
    private SourceCodeEntity fNewParentEntity;

    private SourceCodeEntity fNewEntity;

    /**
     * Creates a new move operation.
     * <p>
     * The moved entity is moved from the old to the new parent entity inside the structure entity.
     * 
     * @param changeType
     *            the change type
     * @param rootNode
     *            the root node
     * @param movedEntity
     *            the moved entity
     * @param newEntity
     *            the new entity
     * @param oldParentEntity
     *            the old parent entity
     * @param newParentEntity
     *            the new parent entity
     */
    public Move(
            ChangeType changeType,
            StructureEntityVersion rootNode,
            SourceCodeEntity movedEntity,
            SourceCodeEntity newEntity,
            SourceCodeEntity oldParentEntity,
            SourceCodeEntity newParentEntity) {
        this(rootNode, movedEntity, newEntity, oldParentEntity, newParentEntity);
        setChangeType(changeType);
    }

    /**
     * Creates a new move operation.
     * 
     * @param rootNode
     *            the root node
     * @param movedEntity
     *            the moved entity
     * @param newEntity
     *            the new entity
     * @param oldParentEntity
     *            the old parent entity
     * @param newParentEntity
     *            the new parent entity
     */
    public Move(
            StructureEntityVersion rootNode,
            SourceCodeEntity movedEntity,
            SourceCodeEntity newEntity,
            SourceCodeEntity oldParentEntity,
            SourceCodeEntity newParentEntity) {
        super(movedEntity, oldParentEntity, rootNode);
        setNewEntity(newEntity);
        setNewParentEntity(newParentEntity);
    }

    public SourceCodeEntity getNewParentEntity() {
        return fNewParentEntity;
    }

    public final void setNewParentEntity(SourceCodeEntity newParentEntity) {
        fNewParentEntity = newParentEntity;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).appendSuper(super.hashCode()).append(getNewEntity())
                .append(getNewParentEntity()).toHashCode();
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
        Move other = (Move) obj;
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(getNewEntity(), other.getNewEntity())
                .append(getNewParentEntity(), other.getNewParentEntity()).isEquals();
    }

    public final void setNewEntity(SourceCodeEntity newEntity) {
        fNewEntity = newEntity;
    }

    public SourceCodeEntity getNewEntity() {
        return fNewEntity;
    }

    @Override
    protected SignificanceLevel liftSignificanceLevel() {
        switch (getChangeType()) {
            case PARAMETER_ORDERING_CHANGE:
                return checkRootEntitySignificance(CRUCIAL);
            default:
                return getChangeType().getSignificance();
        }
    }
}
