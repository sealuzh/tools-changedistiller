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
 * Source code change that represents the delete of an entity.
 * 
 * <p>
 * In addition to the involved entities described in {@link SourceCodeChange} a delete has a
 * <ul>
 * <li>{@link SourceCodeEntity} <code>parentEntity</code> that describes from which entity <code>changedEntity</code> is
 * deleted. Speaking of an AST, <code>parentEntity</code> describes the node in which <code>changedEntity</code> was a
 * child before the delete. <code>parentEntity</code> is taken from the left AST, i.e., old version.</li>
 * </ul>
 * 
 * @author Beat Fluri
 * @author zubi
 * @see SourceCodeChange
 */
public class Delete extends SourceCodeChange {

    /**
     * Creates a new delete operation.
     * <p>
     * The deleted entity is deleted as child of parent entity inside the structure entity.
     * 
     * @param changeType
     *            the change type
     * @param deletedEntity
     *            the deleted entity
     * @param parentEntity
     *            the parent entity
     * @param rootEntity
     *            the root entity
     */
    public Delete(
            ChangeType changeType,
            StructureEntityVersion rootEntity,
            SourceCodeEntity deletedEntity,
            SourceCodeEntity parentEntity) {
        this(rootEntity, deletedEntity, parentEntity);
        setChangeType(changeType);
    }

    /**
     * Creates a new delete operation.
     * 
     * @param deletedEntity
     *            the deleted entity
     * @param parentEntity
     *            the parent entity
     * @param rootEntity
     *            the root entity
     */
    public Delete(StructureEntityVersion rootEntity, SourceCodeEntity deletedEntity, SourceCodeEntity parentEntity) {
        super(deletedEntity, parentEntity, rootEntity);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).appendSuper(super.hashCode()).append(getParentEntity()).toHashCode();
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
        return new EqualsBuilder().appendSuper(super.equals(obj)).append(getParentEntity(), other.getParentEntity())
                .isEquals();
    }

    @Override
    protected SignificanceLevel liftSignificanceLevel() {
        switch (getChangeType()) {
            case DECREASING_ACCESSIBILITY_CHANGE:
            case REMOVED_CLASS:
            case REMOVED_FUNCTIONALITY:
            case REMOVED_OBJECT_STATE:
                return checkChangedEntitySignificance(CRUCIAL);
            case PARAMETER_DELETE:
            case RETURN_TYPE_DELETE:
                return checkRootEntitySignificance(CRUCIAL);
            default:
                return getChangeType().getSignificance();
        }
    }
}
