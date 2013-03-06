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

/**
 * A refacotring pair is a container for storing to {@link RefactoringCandidate}s. Refactoring pairs can be compared
 * according to the similarity between the candidates. This similarity is calculated by the {@link RefactoringExtractor}
 * .
 * 
 * <p>
 * Whenever the {@link RefactoringExtractor} decides that two candidates belong to a refactoring, it generates a
 * refactoring pair.
 * 
 * @author fluri
 * @see ch.uzh.ifi.seal.changedistiller.distilling.Distiller
 * @see RefactoringExtractor
 */
public class RefactoringPair implements Comparable<RefactoringPair> {

    private RefactoringCandidate fDeletedEntity;
    private RefactoringCandidate fInsertedEntity;
    private double fSimilarity;

    /**
     * Creates a new refactoring pair.
     * 
     * @param deletedEntity
     *            entity that was deleted
     * @param insertedEntity
     *            entity that was inserted
     * @param similarity
     *            between the two candidates
     */
    public RefactoringPair(RefactoringCandidate deletedEntity, RefactoringCandidate insertedEntity, double similarity) {
        fDeletedEntity = deletedEntity;
        fInsertedEntity = insertedEntity;
        fSimilarity = similarity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
	public int compareTo(RefactoringPair other) {
        // Double.compare sorts ascending, we'd like to have it descending, so we return the negative result
        return -Double.compare(fSimilarity, (other).getSimilarity());
    }

    /**
     * Returns the deleted entity of this pair.
     * 
     * @return the deleted entity
     */
    public RefactoringCandidate getDeletedEntity() {
        return fDeletedEntity;
    }

    /**
     * Returns the inserted entity of this pair.
     * 
     * @return the inserted entity
     */
    public RefactoringCandidate getInsertedEntity() {
        return fInsertedEntity;
    }

    /**
     * Returns the similarity between the two {@link RefactoringCandidate}s.
     * 
     * @return the similarity betweent the two refactoring candidates.
     */
    public double getSimilarity() {
        return fSimilarity;
    }
}
