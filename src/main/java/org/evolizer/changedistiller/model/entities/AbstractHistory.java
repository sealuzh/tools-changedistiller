package org.evolizer.changedistiller.model.entities;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Abstract class for aggregating versions ({@link StructureEntityVersion}) of a source code entity (i.e. a class, a
 * method or an attribute).
 * 
 * @author Beat Fluri
 * @author zubi
 * @see ClassHistory
 * @see AttributeHistory
 * @see MethodHistory
 */
public abstract class AbstractHistory {

    private List<StructureEntityVersion> fVersions;

    AbstractHistory() {
        setVersions(new LinkedList<StructureEntityVersion>());
    }

    /**
     * Creates a new history for a given version.
     * 
     * @param version
     *            to be added to this history
     */
    public AbstractHistory(StructureEntityVersion version) {
        this();
        addVersion(version);
    }

    @Override
    public final int hashCode() {
        return new HashCodeBuilder(11, 19).append(getUniqueName())
                .append(new ArrayList<StructureEntityVersion>(getVersions())).toHashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        AbstractHistory other = (AbstractHistory) obj;
        return new EqualsBuilder()
                .append(getUniqueName(), other.getUniqueName())
                .append(
                        new ArrayList<StructureEntityVersion>(getVersions()),
                        new ArrayList<StructureEntityVersion>(other.getVersions())).isEquals();
    }

    public List<StructureEntityVersion> getVersions() {
        return fVersions;
    }

    public void setVersions(List<StructureEntityVersion> versions) {
        fVersions = versions;
    }

    public String getUniqueName() {
        return getVersions().get(getVersions().size() - 1).getUniqueName();
    }

    /**
     * Returns label for this history.
     * 
     * @return label for this history.
     */
    public String getLabel() {
        String label = getVersions().get(getVersions().size() - 1).getLabel();
        label = label.substring(0, label.lastIndexOf("@"));
        return getClass().getSimpleName() + ":" + label;
    }

    /**
     * Adds version to this history. Version must be of correct type.
     * 
     * @param version
     *            to add
     */
    public abstract void addVersion(StructureEntityVersion version);

    /**
     * Checks whether this history contains any changes or not.
     * 
     * @return <code>true</code> if this history contains changes, <code>false</code> otherwise.
     */
    public boolean hasChanges() {
        return !getVersions().isEmpty();
    }

    @Override
    public String toString() {
        return getUniqueName();
    }
}
