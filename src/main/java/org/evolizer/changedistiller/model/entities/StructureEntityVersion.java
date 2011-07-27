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
 * @author Beat Fluri
 * @author zubi
 */
public class StructureEntityVersion {

    private EntityType fType;
    private String fUniqueName;
    private Integer fSignificanceLevel = -1;
    private Integer fModifiers = 0;
    private String fVersion;
    private List<SourceCodeChange> fSourceCodeChanges;

    StructureEntityVersion() {
        setSourceCodeChanges(new LinkedList<SourceCodeChange>());
    }

    /**
     * Creates a new structure entity version.
     * 
     * @param type
     *            of the structure entity version
     * @param uniqueName
     *            of the structure entity version
     * @param modifiers
     *            the modifiers
     * @param version
     *            the number or id of this version
     */
    public StructureEntityVersion(EntityType type, String uniqueName, int modifiers, String version) {
        this();
        setType(type);
        setUniqueName(uniqueName);
        setModifiers(modifiers);
        setVersion(version);
    }

    /**
     * Creates a new structure entity version.
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
    
    public EntityType getType() {
        return fType;
    }

    /**
     * Set type code of structure entity. Must be either an attribute, class, or method.
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

    public void setModifiers(int modifiers) {
        fModifiers = modifiers;
    }

    public int getModifiers() {
        return fModifiers;
    }

    public String getVersion() {
		return fVersion;
	}

	public void setVersion(String version) {
		fVersion = version;
	}

	public String getUniqueName() {
        return fUniqueName;
    }

    public void setUniqueName(String uniqueName) {
        fUniqueName = uniqueName;
    }

    public List<SourceCodeChange> getSourceCodeChanges() {
        return fSourceCodeChanges;
    }

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
    	if (fVersion != null) {
    		return getUniqueName() + "@" + fVersion;
    	} else {
    		return getUniqueName();
    	}
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

    @Override
    public String toString() {
        return getUniqueName();
    }

    private void setSignificanceLevel(Integer significanceLevel) {
        fSignificanceLevel = significanceLevel;
    }

}
