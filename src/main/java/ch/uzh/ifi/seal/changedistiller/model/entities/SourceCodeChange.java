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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import ch.uzh.ifi.seal.changedistiller.model.classifiers.ChangeType;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.SignificanceLevel;

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
 * @author Beat Fluri
 * @author Giacomo Ghezzi
 * @author zubi
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

    private SourceCodeEntity fChangedEntity;

    /**
     * Source code entity that becomes the parent entity when the change is applied.
     */
    private SourceCodeEntity fParentEntity;

    SourceCodeChange() {}

    /**
     * Creates a new source code change.
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
     * Creates a new source code change.
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

    public final void setChangedEntity(SourceCodeEntity changedEntity) {
        fChangedEntity = changedEntity;
    }

    public SourceCodeEntity getChangedEntity() {
        return fChangedEntity;
    }

    public final void setRootEntity(StructureEntityVersion rootEntity) {
        fRootEntity = rootEntity;
    }

    public StructureEntityVersion getRootEntity() {
        return fRootEntity;
    }

    public ChangeType getChangeType() {
        return fChangeType;
    }

    public final void setChangeType(ChangeType changeType) {
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

    public String getLabel() {
        return getChangeType().toString();
    }

    /**
     * Hook for subclasses to lift significance level for certain {@link ChangeType}s.
     * 
     * @return the significance level
     */
    protected SignificanceLevel liftSignificanceLevel() {
        return null;
    }

    public SourceCodeEntity getParentEntity() {
        return fParentEntity;
    }

    public final void setParentEntity(SourceCodeEntity parentEntity) {
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
