/*
 * Copyright 2009 University of Zurich, Switzerland
 *
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
 */
package org.evolizer.changedistiller.model.entities;

import java.util.LinkedList;
import java.util.List;

import org.evolizer.changedistiller.model.classifiers.ChangeModifier;
import org.evolizer.changedistiller.model.classifiers.EntityType;

/**
 * A structure entity version consists of all {@link SourceCodeChange}s applied in a version of an attribute, a class,
 * or a method.
 * <p>
 * For each new version of an attribute, a class, or a method, a new structure entity version is created.
 * 
 * @author fluri, zubi
 */
public class StructureEntityVersion {

    /**
     * Type code of structure entity.
     */
    private EntityType fType;

    /**
     * Structure entity's unique name.
     */
    private String fUniqueName;

    /**
     * Significance level of structure entity. Value is calculated not saved.
     */
    private Integer fSignificanceLevel = -1;

    /**
     * Modifiers of this entity.
     */
    private Integer fModifiers = 0;

    /**
     * {@link List} of {@link SourceCodeChange}s applied on structure entity.
     */
    private List<SourceCodeChange> fSourceCodeChanges;

    /**
     * Default constructor. Only used by Hibernate.
     */
    private StructureEntityVersion() {
        setSourceCodeChanges(new LinkedList<SourceCodeChange>());
    }

    /**
     * Constructor to initialize structure entity version with an entity and a version.
     * 
     * @param type
     *            of the structure entity version
     * @param uniqueName
     *            of the structure entity version
     * @param modifiers
     *            the modifiers
     */
    public StructureEntityVersion(EntityType type, String uniqueName, int modifiers) {
        this();
        setType(type);
        setUniqueName(uniqueName);
        setModifiers(modifiers);
    }

    /**
     * Type code of structure entity. One of {@link EntityType#ATTRIBUTE}, {@link EntityType#METHOD} or
     * {@link EntityType#CLASS}.
     * 
     * @return type code
     */
    public EntityType getType() {
        return fType;
    }

    /**
     * Set type code of structure entity. Must be either {@link EntityType#ATTRIBUTE}, {@link EntityType#CLASS} or
     * {@link EntityType#METHOD}.
     * 
     * @param type
     *            of this entity
     * @throws RuntimeException
     *             if illegal type is passed in
     */
    public void setType(EntityType type) {
        if (type.isStructureEntityType()) {
            fType = type;
        } else {
            throw new RuntimeException("Illegal type " + type + ". Must be a structure entity type.");
        }
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
     * Sets source code entity's modifiers.
     * 
     * @param modifiers
     *            the modifiers
     */
    public void setModifiers(int modifiers) {
        fModifiers = modifiers;
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
     * Returns the unique name.
     * 
     * @return unique name of structure entity.
     */
    public String getUniqueName() {
        return fUniqueName;
    }

    /**
     * Sets unique name of structure entity.
     * 
     * @param uniqueName
     *            the unique name
     */
    public void setUniqueName(String uniqueName) {
        fUniqueName = uniqueName;
    }

    /**
     * Returns the source code changes.
     * 
     * @return {@link List} of {@link SourceCodeChange}s of this structure entity.
     */
    public List<SourceCodeChange> getSourceCodeChanges() {
        return fSourceCodeChanges;
    }

    /**
     * Set {@link List} of {@link SourceCodeChange}s of structure entity.
     * 
     * @param sourceCodeChanges
     *            the source code changes
     */
    public void setSourceCodeChanges(List<SourceCodeChange> sourceCodeChanges) {
        fSourceCodeChanges = sourceCodeChanges;
    }

    /**
     * Add a {@link List} of {@link SourceCodeChange}s to structure entity.
     * 
     * @param sourceCodeChanges
     *            the source code changes
     */
    public void addAllSourceCodeChanges(List<SourceCodeChange> sourceCodeChanges) {
        getSourceCodeChanges().addAll(sourceCodeChanges);
    }

    /**
     * Add a {@link SourceCodeChange} to structure entity.
     * 
     * @param sourceCodeChange
     *            the source code change
     */
    public void addSourceCodeChange(SourceCodeChange sourceCodeChange) {
        getSourceCodeChanges().add(sourceCodeChange);
    }

    /**
     * Returns label for this entity:
     * <ul>
     * <li>In case of a class:
     * 
     * <pre>
     * &lt;className&gt;@&lt;revisionNumber&gt;
     * </pre>
     * 
     * </li>
     * <li>In case of an attribute:
     * 
     * <pre>
     * &lt;attributeName&gt;:&lt;attributeType&gt;@&lt;revisionNumber&gt;
     * </pre>
     * 
     * </li>
     * <li>In case of a method:
     * 
     * <pre>
     * &lt;methodSignature&gt;@&lt;revisionNumber&gt;
     * </pre>
     * 
     * </li>
     * </ul>
     * .
     * 
     * @return label for this entity.
     */
    public String getLabel() {
        return getUniqueName();
        // String shortName = "";
        // switch (getType()) {
        // case METHOD:
        // int parameterListBegin = getUniqueName().lastIndexOf('(');
        // String methodNameWithoutParams = getUniqueName().substring(0, parameterListBegin);
        // String methodName = methodNameWithoutParams.substring(methodNameWithoutParams.lastIndexOf('.') + 1);
        // String parameterList = getUniqueName().substring(parameterListBegin);
        // shortName = methodName + parameterList;
        // break;
        // default:
        // shortName = getUniqueName().substring(getUniqueName().lastIndexOf('.') + 1);
        // break;
        // }
        // return shortName.replace(" ", "") + STRING + (getRevision() == null ? NULL : getRevision().getNumber());
    }

    /**
     * Significance level of structure entity. Value is calculated not saved.
     * 
     * @return significance level
     */
    public Integer getSignificanceLevel() {
        if (fSignificanceLevel < 0) {
            fSignificanceLevel = 0;
            addSigLevels(getSourceCodeChanges());
        }
        return fSignificanceLevel;
    }

    /**
     * Add significance levels of {@link List} of source code changes.
     * 
     * @param changes
     */
    private void addSigLevels(List<SourceCodeChange> changes) {
        for (SourceCodeChange scc : changes) {
            setSignificanceLevel(getSignificanceLevel() + scc.getSignificanceLevel().value());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getUniqueName();
    }

    private void setSignificanceLevel(Integer significanceLevel) {
        fSignificanceLevel = significanceLevel;
    }

}
