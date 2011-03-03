package org.evolizer.changedistiller.model.entities;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * Source code entity's unique name.
     */
    private String fUniqueName;

    /**
     * Type code of source code entity.
     */
    private EntityType fType;

    /**
     * Modifiers of source code entity.
     */
    private Integer fModifiers = 0;

    private List<SourceCodeEntity> fAssociatedEntities = new ArrayList<SourceCodeEntity>();
    private SourceRange fRange = new SourceRange();

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
    public SourceCodeEntity(String uniqueName, EntityType type, Integer modifiers, SourceRange range) {
        setUniqueName(uniqueName);
        setType(type);
        setModifiers(modifiers);
        setSourceRange(range);
    }

    /**
     * Returns the unique name.
     * 
     * @return unique name of source code entity.
     */
    public String getUniqueName() {
        return fUniqueName;
    }

    /**
     * Set unique name of source code entity.
     * 
     * @param uniqueName
     *            the unique name
     */
    public void setUniqueName(String uniqueName) {
        fUniqueName = uniqueName;
    }

    /**
     * Returns the type.
     * 
     * @return source code entity's type code.
     */
    public EntityType getType() {
        return fType;
    }

    /**
     * Sets source code entity's type code.
     * 
     * @param type
     *            the type
     */
    public void setType(EntityType type) {
        fType = type;
    }

    /**
     * Returns the modifiers.
     * 
     * @return source code entity's modifiers
     */
    public int getModifiers() {
        return fModifiers;
    }

    /**
     * Set source code entity's modifiers.
     * 
     * @param modifiers
     *            the modifiers
     */
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

    /**
     * Checks if it's private.
     * 
     * @return true, if this entity is private
     */
    public boolean isPrivate() {
        return ChangeModifier.isPrivate(fModifiers);
    }

    /**
     * Checks if it's protected.
     * 
     * @return true, if this entity is protected
     */
    public boolean isProtected() {
        return ChangeModifier.isProtected(fModifiers);
    }

    /**
     * Checks if it's public.
     * 
     * @return true, if this entity is public
     */
    public boolean isPublic() {
        return ChangeModifier.isPublic(fModifiers);
    }

    /**
     * Returns the associated entities.
     * 
     * @return the associated entities
     */
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

    /**
     * Sets the associated entities.
     * 
     * @param associatedEntities
     *            the new associated entities
     */
    public void setAssociatedEntities(List<SourceCodeEntity> associatedEntities) {
        fAssociatedEntities = associatedEntities;
    }

    /**
     * Sets the source range.
     * 
     * @param range
     *            the new source range
     */
    public void setSourceRange(SourceRange range) {
        fRange = range;
    }

    /**
     * Returns the source range.
     * 
     * @return the source range
     */
    public SourceRange getSourceRange() {
        return fRange;
    }

    /**
     * Used for persisting the length by Hibernate.
     * 
     * @return the length
     */
    int getLength() {
        return fRange.getEnd();
    }

    /**
     * Sets the length.
     * 
     * @param length
     *            the new length
     */
    @SuppressWarnings("unused")
    private void setLength(int length) {
        fRange.setEnd(length);
    }

    /**
     * Used for persisting the offset by Hibernate.
     * 
     * @param offset
     *            the new offset
     */
    void setOffset(int offset) {
        fRange.setStart(offset);
    }

    /**
     * Returns the offset.
     * 
     * @return the offset
     */
    @SuppressWarnings("unused")
    private int getOffset() {
        return fRange.getStart();
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

    /**
     * Returns label of the {@link EntityType} this source code entity represents.
     * 
     * @return label for this entity.
     */
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

}
