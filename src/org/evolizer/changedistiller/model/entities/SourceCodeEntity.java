package org.evolizer.changedistiller.model.entities;

import java.util.ArrayList;
import java.util.List;

import org.evolizer.changedistiller.model.classifiers.ChangeModifier;
import org.evolizer.changedistiller.model.classifiers.EntityType;
import org.evolizer.changedistiller.model.classifiers.SourceRange;

/**
 * Source code entity representing one particular AST node.
 * <p>
 * Each source code entity has a {@link EntityType} describing the type of the source code entity.
 * <p>
 * The unique name of a source code entity depends on its {@link EntityType}:
 * <ul>
 * <li>{@link EntityType#ASSERT_STATEMENT}: <code>Expression[<b>:</b>Message]</code></li>
 * <li>{@link EntityType#ATTRIBUTE}: <code>FullyQualifiedName<b>:</b> AttributeType</code></li>
 * <li>{@link EntityType#BREAK_STATEMENT}: <code>[Label]</code></li>
 * <li>{@link EntityType#CATCH_CLAUSE}: <code>ExceptionType</code></li>
 * <li>{@link EntityType#CLASS}: <code>FullyQualifiedName</code></li>
 * <li>{@link EntityType#CONSTRUCTOR_INVOCATION}: <code>Invocation<b>;</b></code></li>
 * <li>{@link EntityType#CONTINUE_STATEMENT}: <code>[Label]</code></li>
 * <li>{@link EntityType#DO_STATEMENT}: <code>Expression</code></li>
 * <li>{@link EntityType#ELSE_STATEMENT}: <code>Expression of if-statement</code></li>
 * <li>{@link EntityType#FOREACH_STATEMENT}: <code>Parameter<b>:</b>Expression</code></li>
 * <li>{@link EntityType#EXPRESSION_STATEMENT}: <code>Expression<b>;</b></code></li>
 * <li>{@link EntityType#FOR_STATEMENT}: <code>[Expression]</code></li>
 * <li>{@link EntityType#IF_STATEMENT}: <code>Expression</code></li>
 * <li>{@link EntityType#JAVADOC}: <code>Javadoc as is</code></li>
 * <li>{@link EntityType#LABELED_STATEMENT}: <code>Label</code></li>
 * <li>{@link EntityType#METHOD}: <code>FullyQualifiedSignature</code></li>
 * <li>{@link EntityType#PRIMITIVE_TYPE}: inside a MethodDeclaration
 * <code>[ParameterName|MethodSignature]<b>:</b> TypeName</code>, else: <code>TypeName</code>; method signature is used
 * if the type corresponds to the return type of the method</li>
 * <li>{@link EntityType#RETURN_STATEMENT}: <code>[Expression]</code></li>
 * <li>{@link EntityType#SIMPLE_TYPE}: inside a MethodDeclaration
 * <code>[ParameterName|MethodSignature]<b>:</b> TypeName</code>, else: <code>TypeName</code>; method signature is used
 * if the type corresponds to the return type of the method</li>
 * <li>{@link EntityType#SINGLE_VARIABLE_DECLARATION}: <code>Identifier</code></li>
 * <li>{@link EntityType#SUPER_CONSTRUCTOR_INVOCATION}: <code>Invocation<b>;</b></code></li>
 * <li>{@link EntityType#SWITCH_CASE}: <code>[Expression|<b>default</b>]</code></li>
 * <li>{@link EntityType#SWITCH_STATEMENT}: <code>Expression</code></li>
 * <li>{@link EntityType#SYNCHRONIZED_STATEMENT}: <code>Expression</code></li>
 * <li>{@link EntityType#THEN_STATEMENT}: <code>Expression of if-statement</code></li>
 * <li>{@link EntityType#THROW_STATEMENT}: <code>Expression</code></li>
 * <li>{@link EntityType#TYPE_PARAMETER}: <code>FullyQualifiedName of TypeVariable</code></li>
 * <li>{@link EntityType#VARIABLE_DECLARATION_FRAGMENT}: <code>FullyQualifiedName of Identifier<b>;</b></code></li>
 * <li>{@link EntityType#VARIABLE_DECLARATION_STATEMENT}: <code>Declaration<b>;</b></code></li>
 * <li>{@link EntityType#WHILE_STATEMENT}: <code>Expression</code></li>
 * <li>{@link EntityType#WILDCARD_TYPE}: <code>[<b>extends</b>|<b>super</b>]</code></li>
 * </ul>
 * Entity types that are not mentioned here, have an empty string as unique name.
 * 
 * @author fluri, zubi
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
