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
package ch.uzh.ifi.seal.changedistiller.model.classifiers;

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

import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;

/**
 * All change types that are extracted by ChangeDistiller and stored as {@link SourceCodeChange}.
 * <p>
 * Each type contains a {@link SignificanceLevel}, is marked whether it is a body change (e.g. found inside a method
 * body) or not and whether its significance level is unstable and may be lifted (e.g. for a method renaming, the
 * significance gets lifted if the method is public or protected).
 * 
 * @author zubi
 * @see SignificanceLevel
 * @see SourceCodeChange
 */
public enum ChangeType {
    ADDING_ATTRIBUTE_MODIFIABILITY(SignificanceLevel.LOW, false, false),
    ADDING_CLASS_DERIVABILITY(SignificanceLevel.LOW, false, false),
    ADDING_METHOD_OVERRIDABILITY(SignificanceLevel.LOW, false, false),
    ADDITIONAL_CLASS(SignificanceLevel.LOW, true, false),
    ADDITIONAL_FUNCTIONALITY(SignificanceLevel.LOW, true, false),
    ADDITIONAL_OBJECT_STATE(SignificanceLevel.LOW, true, false),
    ALTERNATIVE_PART_DELETE(SignificanceLevel.MEDIUM, true, false),
    ALTERNATIVE_PART_INSERT(SignificanceLevel.MEDIUM, true, false),
    ATTRIBUTE_RENAMING(SignificanceLevel.MEDIUM, false, true), // newEntity
    ATTRIBUTE_TYPE_CHANGE(SignificanceLevel.HIGH, false, true), // root
    CLASS_RENAMING(SignificanceLevel.MEDIUM, false, true), // rootEntity
    COMMENT_DELETE(SignificanceLevel.NONE, true, false),
    COMMENT_INSERT(SignificanceLevel.NONE, true, false),
    COMMENT_MOVE(SignificanceLevel.NONE, true, false),
    COMMENT_UPDATE(SignificanceLevel.NONE, true, false),
    CONDITION_EXPRESSION_CHANGE(SignificanceLevel.MEDIUM, true, false),
    DECREASING_ACCESSIBILITY_CHANGE(SignificanceLevel.HIGH, false, true), // changedEntity
    DOC_DELETE(SignificanceLevel.NONE, false, false),
    DOC_INSERT(SignificanceLevel.NONE, false, false),
    DOC_UPDATE(SignificanceLevel.NONE, false, false),
    INCREASING_ACCESSIBILITY_CHANGE(SignificanceLevel.MEDIUM, false, false),
    METHOD_RENAMING(SignificanceLevel.MEDIUM, false, true), // newEntity
    PARAMETER_DELETE(SignificanceLevel.HIGH, false, true), // root
    PARAMETER_INSERT(SignificanceLevel.HIGH, false, true), // root
    PARAMETER_ORDERING_CHANGE(SignificanceLevel.HIGH, false, true), // root
    PARAMETER_RENAMING(SignificanceLevel.MEDIUM, false, false),
    PARAMETER_TYPE_CHANGE(SignificanceLevel.HIGH, false, true), // root
    PARENT_CLASS_CHANGE(SignificanceLevel.CRUCIAL, false, false),
    PARENT_CLASS_DELETE(SignificanceLevel.CRUCIAL, false, false),
    PARENT_CLASS_INSERT(SignificanceLevel.CRUCIAL, false, false),
    PARENT_INTERFACE_CHANGE(SignificanceLevel.CRUCIAL, false, false),
    PARENT_INTERFACE_DELETE(SignificanceLevel.CRUCIAL, false, false),
    PARENT_INTERFACE_INSERT(SignificanceLevel.CRUCIAL, false, false),
    REMOVED_CLASS(SignificanceLevel.HIGH, true, true), // changedEntity
    REMOVED_FUNCTIONALITY(SignificanceLevel.HIGH, true, true), // changedEntity
    REMOVED_OBJECT_STATE(SignificanceLevel.HIGH, true, true), // changedEntity
    REMOVING_ATTRIBUTE_MODIFIABILITY(SignificanceLevel.HIGH, false, true), // root
    REMOVING_CLASS_DERIVABILITY(SignificanceLevel.CRUCIAL, false, false),
    REMOVING_METHOD_OVERRIDABILITY(SignificanceLevel.CRUCIAL, false, false),
    RETURN_TYPE_CHANGE(SignificanceLevel.HIGH, false, true), // root
    RETURN_TYPE_DELETE(SignificanceLevel.HIGH, false, true), // root
    RETURN_TYPE_INSERT(SignificanceLevel.HIGH, false, true), // root
    STATEMENT_DELETE(SignificanceLevel.MEDIUM, true, false),
    STATEMENT_INSERT(SignificanceLevel.LOW, true, false),
    STATEMENT_ORDERING_CHANGE(SignificanceLevel.LOW, true, false),
    STATEMENT_PARENT_CHANGE(SignificanceLevel.MEDIUM, true, false),
    STATEMENT_UPDATE(SignificanceLevel.LOW, true, false),
    UNCLASSIFIED_CHANGE(SignificanceLevel.NONE, true, false);

    private final boolean fIsBodyChange;
    private final boolean fHasUnstableSignificanceLevel;
    private final SignificanceLevel fSignificance;

    private ChangeType(SignificanceLevel level, boolean isBodyChange, boolean hasUnstableSignificanceLevel) {
        fSignificance = level;
        fIsBodyChange = isBodyChange;
        fHasUnstableSignificanceLevel = hasUnstableSignificanceLevel;
    }

    /**
     * Returns number of change types.
     * 
     * @return number of defined and extracted change types.
     */
    public static int getNumberOfChangeTypes() {
        return values().length;
    }

    /**
     * Returns {@link SignificanceLevel} of this change type.
     * 
     * @return {@link SignificanceLevel} of change type.
     */
    public SignificanceLevel getSignificance() {
        return fSignificance;
    }

    /**
     * Returns whether this change is a body change or not.
     * 
     * @return <code>true</code> if this change is a body change, <code>false</code> if it is a declaration change.
     */
    public boolean isBodyChange() {
        return fIsBodyChange;
    }

    /**
     * Returns whether this change is a declaration change or not.
     * 
     * @return <code>true</code> if this change is a declaration change, <code>false</code> if it is a body change.
     */
    public boolean isDeclarationChange() {
        return !fIsBodyChange;
    }

    /**
     * Returns whether the {@link SignificanceLevel} of this change may be lifted or not.
     * 
     * @return <code>true</code> if the significance level is likely to be lifted, <code>false</code> otherwise.
     */
    public boolean hasUnstableSignificanceLevel() {
        return fHasUnstableSignificanceLevel;
    }
}
