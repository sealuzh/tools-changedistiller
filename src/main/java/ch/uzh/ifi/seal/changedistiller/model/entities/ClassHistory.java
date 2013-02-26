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

import java.util.HashMap;
import java.util.Map;

/**
 * Class histories store change data about the history of a class. It contains its attribute, inner classe, and method
 * histories. Top most element for histories.
 * <p>
 * Label:
 * 
 * <pre>
 * ClassHistory:&lt;className&gt;
 * </pre>
 * <p>
 * Unique name:
 * 
 * <pre>
 * qualified class name
 * </pre>
 * 
 * @author Beat Fluri
 * @author zubi
 * @author Giacomo Ghezzi
 * @see AbstractHistory
 * @see AttributeHistory
 * @see MethodHistory
 */
public class ClassHistory extends AbstractHistory {

    /**
     * {@link Map} of all method histories belonging to this class history. Key is the unique name of method history.
     */
    private Map<String, MethodHistory> fMethodHistories;

    /**
     * {@link Map} of all attribute histories belonging to this class history. Key is the unique name of attribute
     * history.
     */
    private Map<String, AttributeHistory> fAttributeHistories;

    /**
     * {@link Map} of all inner class histories belonging to this class history. Key is the unique name of inner class
     * history.
     */
    private Map<String, ClassHistory> fInnerClassHistories;

    /**
     * Createa a new class history.
     * 
     * @param clazz
     *            the clazz to add to this history
     */
    public ClassHistory(StructureEntityVersion clazz) {
    	addVersion(clazz);
        initHistories();
    }

    ClassHistory() {
        initHistories();
    }

    /**
     * Adds class version to this history.
     * 
     * @param version
     *            a class version
     */
    @Override
    public final void addVersion(StructureEntityVersion version) {
        if (version.getType().isClass()) {
            getVersions().add(version);
        }
    }

    /**
     * Create an inner class history for the unique name of the given class version if it does not exist. The history is
     * attached to the class history of this history.
     * 
     * @param clazz
     *            the class version for which a history is to be created
     * 
     * @return class history for the given class version
     */
    public ClassHistory createInnerClassHistory(StructureEntityVersion clazz) {
        ClassHistory ch = null;
        if (getInnerClassHistories().containsKey(clazz.getUniqueName())) {
            ch = getInnerClassHistories().get(clazz.getUniqueName());
        } else {
            ch = new ClassHistory(clazz);
            getInnerClassHistories().put(clazz.getUniqueName(), ch);
        }
        return ch;
    }

    /**
     * Returns {@link Map} of attribute histories belonging to this class history. Key is the unique name of attribute
     * history.
     * 
     * @return the attribute histories
     */
    public Map<String, AttributeHistory> getAttributeHistories() {
        return fAttributeHistories;
    }

    /**
     * Returns {@link Map} of inner class histories belonging to this class history. Key is the unique name of inner
     * class history.
     * 
     * @return the inner class histories
     */
    public Map<String, ClassHistory> getInnerClassHistories() {
        return fInnerClassHistories;
    }

    /**
     * Returns {@link Map} of method histories belonging to this class history. Key is the unique name of method
     * history.
     * 
     * @return the method histories
     */
    public Map<String, MethodHistory> getMethodHistories() {
        return fMethodHistories;
    }

    @Override
    public boolean hasChanges() {
        boolean hasChanges = false;
        hasChanges |= !getAttributeHistories().isEmpty();
        hasChanges |= !getMethodHistories().isEmpty();
        hasChanges |= !getInnerClassHistories().isEmpty();
        for (int i = 0; !hasChanges && (i < getVersions().size()); i++) {
            hasChanges |= !getVersions().get(i).getSourceCodeChanges().isEmpty();
        }
        return hasChanges;
    }

    /**
     * Override unique name (key) of attribute history in attribute histories {@link Map}.
     * 
     * @param oldUniqueName
     *            name in {@link Map}
     * @param newUniqueName
     *            new name in {@link Map}
     */
    public void overrideAttributeHistory(String oldUniqueName, String newUniqueName) {
        AttributeHistory tmp = getAttributeHistories().get(oldUniqueName);
        getAttributeHistories().remove(oldUniqueName);
        getAttributeHistories().put(newUniqueName, tmp);
    }

    /**
     * Override unique name (key) of inner class history in inner class histories {@link Map}.
     * 
     * @param oldUniqueName
     *            name in {@link Map}
     * @param newUniqueName
     *            new name in {@link Map}
     */
    public void overrideClassHistory(String oldUniqueName, String newUniqueName) {
        ClassHistory tmp = getInnerClassHistories().get(oldUniqueName);
        getInnerClassHistories().remove(oldUniqueName);
        getInnerClassHistories().put(newUniqueName, tmp);
    }

    /**
     * Override unique name (key) of method history in method histories {@link Map}.
     * 
     * @param oldUniqueName
     *            name in {@link Map}
     * @param newUniqueName
     *            new name in {@link Map}
     */
    public void overrideMethodHistory(String oldUniqueName, String newUniqueName) {
        MethodHistory tmp = getMethodHistories().get(oldUniqueName);
        getMethodHistories().remove(oldUniqueName);
        getMethodHistories().put(newUniqueName, tmp);
    }

    /**
     * Set {@link Map} of attribute histories belonging to this class history.
     * 
     * @param attributeHistories
     *            the attribute histories
     */
    public void setAttributeHistories(Map<String, AttributeHistory> attributeHistories) {
        fAttributeHistories = attributeHistories;
    }

    /**
     * Set {@link Map} of inner class histories belonging to this class history.
     * 
     * @param innerClassHistories
     *            the inner class histories
     */
    public void setInnerClassHistories(Map<String, ClassHistory> innerClassHistories) {
        fInnerClassHistories = innerClassHistories;
    }

    /**
     * Set {@link Map} of method histories belonging to class history.
     * 
     * @param methodHistories
     *            the method histories
     */
    public void setMethodHistories(Map<String, MethodHistory> methodHistories) {
        fMethodHistories = methodHistories;
    }

    private void initHistories() {
        setMethodHistories(new HashMap<String, MethodHistory>());
        setAttributeHistories(new HashMap<String, AttributeHistory>());
        setInnerClassHistories(new HashMap<String, ClassHistory>());
    }

    /**
     * Deletes the provided {@link StructureEntityVersion} from the corresponding {@link MethodHistory}.
     * 
     * <p>
     * If the corresponding history has not any versions left, it is also deleted.
     * 
     * @param method
     *            the structure entity version to delete from the corresponding method history
     */
    public void deleteMethod(StructureEntityVersion method) {
        if ((method.getType().isMethod()) || getMethodHistories().isEmpty()) {
            return;
        }
        MethodHistory methodHistory = getMethodHistories().get(method.getUniqueName());
        if (methodHistory != null) {
            boolean wasMethodRemoved = methodHistory.getVersions().remove(method);
            if (wasMethodRemoved && methodHistory.getVersions().isEmpty()) {
                getMethodHistories().remove(method.getUniqueName());
            }
        }
    }

    /**
     * Deletes the provided {@link StructureEntityVersion} from the corresponding {@link AttributeHistory}.
     * 
     * <p>
     * If the corresponding history has not any versions left, it is also deleted.
     * 
     * @param attribute
     *            the structure entity version to delete from the corresponding attribute history
     */
    public void deleteAttribute(StructureEntityVersion attribute) {
        if ((attribute.getType().isField()) || getAttributeHistories().isEmpty()) {
            return;
        }
        AttributeHistory attributeHistory = getAttributeHistories().get(attribute.getUniqueName());
        if (attributeHistory != null) {
            boolean wasAttributeRemoved = attributeHistory.getVersions().remove(attribute);
            if (wasAttributeRemoved && attributeHistory.getVersions().isEmpty()) {
                getAttributeHistories().remove(attribute.getUniqueName());
            }
        }
    }
}
