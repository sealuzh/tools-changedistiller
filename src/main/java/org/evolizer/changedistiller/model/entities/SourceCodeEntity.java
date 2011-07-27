package org.evolizer.changedistiller.model.entities;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.evolizer.changedistiller.model.classifiers.ChangeModifier;
import org.evolizer.changedistiller.model.classifiers.EntityType;
import org.evolizer.changedistiller.model.classifiers.SourceRange;

/**
 * Source code entity representing one particular AST node.
 * <p>
 * Each source code entity has a {@link EntityType} describing the type of the source code entity. The unique name of a
 * source code entity depends on its {@link EntityType}.
 * 
 * @author Beat Fluri
 * @author zubi
 */
public class SourceCodeEntity {

    private String fUniqueName;
    private EntityType fType;
    private int fModifiers;
    private List<SourceCodeEntity> fAssociatedEntities;
    private SourceRange fRange;

    /**
     * Constructor to initialize a source code entity with a unique name and a type.
     * 
     * @param uniqueName
     *            the name
     * @param type
     *            the type
     * @param range
     *            the range
     */
    public SourceCodeEntity(String uniqueName, EntityType type, SourceRange range) {
        this(uniqueName, type, 0, range);
    }

    /**
     * Constructor to initialize a source code entity with a unique name, a name, a type, and modifiers.
     * 
     * @param uniqueName
     *            the unique name
     * @param type
     *            the type
     * @param modifiers
     *            the modifiers
     * @param range
     *            the range
     */
    public SourceCodeEntity(String uniqueName, EntityType type, int modifiers, SourceRange range) {
        setUniqueName(uniqueName);
        setType(type);
        setModifiers(modifiers);
        setSourceRange(range);
        setAssociatedEntities(new LinkedList<SourceCodeEntity>());
    }

    public String getUniqueName() {
        return fUniqueName;
    }

    public void setUniqueName(String uniqueName) {
        fUniqueName = uniqueName;
    }

    public EntityType getType() {
        return fType;
    }

    public void setType(EntityType type) {
        fType = type;
    }

    public int getModifiers() {
        return fModifiers;
    }

    public void setModifiers(int modifiers) {
        fModifiers = modifiers;
    }

    /**
     * Checks if it's final.
     * 
     * @return true, if this entity is final
     */
    public boolean isFinal() {
        return ChangeModifier.isFinal(fModifiers);
    }

    public boolean isPrivate() {
        return ChangeModifier.isPrivate(fModifiers);
    }

    public boolean isProtected() {
        return ChangeModifier.isProtected(fModifiers);
    }

    public boolean isPublic() {
        return ChangeModifier.isPublic(fModifiers);
    }

    public List<SourceCodeEntity> getAssociatedEntities() {
        return fAssociatedEntities;
    }

    /**
     * Adds an associated entity.
     * 
     * @param entity
     *            the entity to add
     */
    public void addAssociatedEntity(SourceCodeEntity entity) {
        fAssociatedEntities.add(entity);
    }

    public void setAssociatedEntities(List<SourceCodeEntity> associatedEntities) {
        fAssociatedEntities = associatedEntities;
    }

    public void setSourceRange(SourceRange range) {
        fRange = range;
    }

    public SourceRange getSourceRange() {
        return fRange;
    }

    /**
     * Returns string representation of this entity, i.e. the type and unique name of it.
     * <p>
     * For debugging purposes only.
     * 
     * @return string representation
     * @see #getType()
     * @see #getUniqueName()
     */
    @Override
    public String toString() {
        return getType().toString() + ": " + getUniqueName();
    }

    public String getLabel() {
        return getType().toString();
    }

    public int getStartPosition() {
        return fRange.getStart();
    }

    /**
     * Sets the start position of this {@link SourceCodeEntity}.
     * 
     * @param start
     *            to set
     */
    public void setStartPosition(int start) {
        fRange.setStart(start);
    }

    public int getEndPosition() {
        return fRange.getEnd();
    }

    /**
     * Sets the end position of this {@link SourceCodeEntity}.
     * 
     * @param end
     *            to set
     */
    public void setEndPosition(int end) {
        fRange.setEnd(end);
    }

    @Override
    public int hashCode() {
    	HashCodeBuilder b = new HashCodeBuilder(17, 37);
    	for (SourceCodeEntity e : getAssociatedEntities()) {
    		b.append(e.getUniqueName());
    		b.append(e.getSourceRange());
    		b.append(e.getModifiers());
    		b.append(e.getType());
    	}
        return new HashCodeBuilder(17, 37).append(getUniqueName()).append(getType()).append(getModifiers())
                .append(getSourceRange()).append(b.toHashCode()).toHashCode();
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
        SourceCodeEntity other = (SourceCodeEntity) obj;
        return new EqualsBuilder().append(getUniqueName(), other.getUniqueName()).append(getType(), other.getType())
                .append(getModifiers(), other.getModifiers()).append(getSourceRange(), other.getSourceRange())
                .isEquals();
    }

}
