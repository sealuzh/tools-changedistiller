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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    public final void setVersions(List<StructureEntityVersion> versions) {
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
        label = label.substring(0, label.lastIndexOf('@'));
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
