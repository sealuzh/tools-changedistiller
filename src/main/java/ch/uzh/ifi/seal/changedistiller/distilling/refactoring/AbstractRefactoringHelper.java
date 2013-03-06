package ch.uzh.ifi.seal.changedistiller.distilling.refactoring;

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

import ch.uzh.ifi.seal.changedistiller.ast.ASTHelper;
import ch.uzh.ifi.seal.changedistiller.distilling.Distiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.ClassHistory;
import ch.uzh.ifi.seal.changedistiller.model.entities.StructureEntityVersion;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureNode;

/**
 * Refactoring helpers support {@link Distiller} in deciding whether an added and a deleted class/field/method represent
 * a refactoring. The helpers for the different {@link StructureNode} have to extend this class to be conform to the
 * {@link Distiller}.
 * 
 * <p>
 * Since the {@link Distiller} assumes that the refactoring helper updates the {@link ClassHistory} in which the
 * refactorings took place, heirs of this class have to fulfill that all {@link StructureEntityVersion} generations are
 * made via the provided {@link ClassHistory}.
 * 
 * @author Beat Fluri
 * @author Giacomo Ghezzi
 * @see ClassRefactoringHelper
 * @see FieldRefactoringHelper
 * @see MethodRefactoringHelper
 */
public abstract class AbstractRefactoringHelper {

    private ClassHistory fClassHistory;
    private double fThreshold = 1.0;
    private ASTHelper<StructureNode> fASTHelper;

    /**
     * Creates a new refactoring helper.
     * 
     * @param classHistory
     *            on which the helper creates new {@link StructureEntityVersion}s
     * @param astHelper
     *            that helps the refactoring helper
     */
    public AbstractRefactoringHelper(ClassHistory classHistory, ASTHelper<StructureNode> astHelper) {
        fClassHistory = classHistory;
        fASTHelper = astHelper;
    }

    /**
     * Creates a {@link StructureEntityVersion} for the given {@link StructureNode} and attaches it to the
     * {@link ClassHistory}.
     * 
     * @param node
     *            to create the structure entity version
     * @return structure entity version for the structure node
     */
    public abstract StructureEntityVersion createStructureEntityVersion(StructureNode node);
    
    /**
     * Creates a {@link StructureEntityVersion} for the given {@link StructureNode} and attaches it to the
     * {@link ClassHistory}.
     * 
     * @param node
     *            to create the structure entity version
     * @param version
     *            the number or ID associated to the structure entity version
     * @return structure entity version for the structure node
     */
    public abstract StructureEntityVersion createStructureEntityVersionWithID(StructureNode node, String version);


    /**
     * Creates a {@link StructureEntityVersion} with newEntityName and replaces the {@link StructureEntityVersion} of
     * the {@link StructureNode} (if exists) with it in the {@link ClassHistory}. took place.
     * 
     * @param node
     *            to create the structure entity version
     * @param newEntityName
     *            of the new structure entity version
     * @return structure entity with newEntityName and the modifiers that is a replacement of the structure entity
     *         version of the structure node
     */
    public abstract StructureEntityVersion createStructureEntityVersion(StructureNode node, String newEntityName);
    
	/**
	 * Creates a {@link StructureEntityVersion} with newEntityName and replaces
	 * the {@link StructureEntityVersion} of the {@link StructureNode} (if
	 * exists) with it in the {@link ClassHistory}. took place.
	 * 
	 * @param node
	 *            to create the structure entity version
	 * @param newEntityName
	 *            of the new structure entity version
     * @param version
     *            the number or ID associated to the structure entity version
	 * @return structure entity with newEntityName and the modifiers that is a
	 *         replacement of the structure entity version of the structure node
	 */
	public abstract StructureEntityVersion createStructureEntityVersionWithID(
			StructureNode node, String newEntityName, String version);

    /**
     * Extracts a short form of the unique name provided.
     * 
     * @param uniqueName
     *            to shorten
     * @return short form of unique name
     */
    public abstract String extractShortName(String uniqueName);

    /**
     * Checks whether an old and a new entity are subject of a refactoring. Whether it is a refactoring or not is
     * decided according to the similarity implementation and the given threshold.
     * 
     * @param left
     *            the old entity
     * @param right
     *            the new entity
     * @return <code>true</code> if the two entities are subject of a refactoring, <code>false</code> otherwise
     */
    public final boolean isRefactoring(StructureNode left, StructureNode right) {
        return similarity(left, right) >= getThreshold();
    }

    /**
     * Sets the threshold for the similarity calculation.
     * 
     * @param threshold
     *            for the similarity calculation
     */
    public void setThreshold(double threshold) {
        fThreshold = threshold;
    }

    /**
     * Calculates the similarity between two entities represented by their names and the string representation of them.
     * 
     * @param left
     *            the old entity
     * @param right
     *            the new entity
     * @return similarity value the two entities; <code>1.0</code> is the highest, <code>0.0</code> the lowest
     *         similarity value
     */
    public abstract double similarity(StructureNode left, StructureNode right);

    protected ClassHistory getClassHistory() {
        return fClassHistory;
    }

    protected double getThreshold() {
        return fThreshold;
    }

    protected ASTHelper<StructureNode> getASTHelper() {
        return fASTHelper;
    }

}
