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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureNode;

/**
 * Provides a method to extract refactorings from a list of added and a list of deleted entities.
 * 
 * @author Beat Fluri
 * @see AbstractRefactoringHelper
 */
public final class RefactoringExtractor {

    private RefactoringExtractor() {}

    /**
     * Extracts all refactorings that result from add and delete operations of entities.
     * 
     * <p>
     * For instance, if a method was deleted from a class body and a new one was inserted, this method checks whether
     * the two operations reflect a refactoring.
     * 
     * @param addedEntities
     *            list of added entities
     * @param deletedEntities
     *            list of deleted entities
     * @param refactoringHelper
     *            that knows how to deal with a possible refactoring. It corresponds to type of added/deleted entities
     * @return list of refactoring pairs extracted from the added/deleted entity lists
     */
    public static List<RefactoringPair> extractRefactorings(
            List<RefactoringCandidate> addedEntities,
            List<RefactoringCandidate> deletedEntities,
            AbstractRefactoringHelper refactoringHelper) {
        List<RefactoringPair> refactorings = new ArrayList<RefactoringPair>();
        List<RefactoringPair> refactoringCandidates = new ArrayList<RefactoringPair>();
        for (RefactoringCandidate rightCandidate : addedEntities) {
            StructureNode right = rightCandidate.getDiffNode().getRight();

            for (RefactoringCandidate leftCandidate : deletedEntities) {
                StructureNode left = leftCandidate.getDiffNode().getLeft();
                if (left.isOfSameTypeAs(right) && refactoringHelper.isRefactoring(left, right)) {
                    double similarity = refactoringHelper.similarity(left, right);
                    refactoringCandidates.add(new RefactoringPair(leftCandidate, rightCandidate, similarity));
                }
            }
        }

        Collections.sort(refactoringCandidates);

        for (RefactoringPair pair : refactoringCandidates) {
            RefactoringCandidate left = pair.getDeletedEntity();
            RefactoringCandidate right = pair.getInsertedEntity();
            if (!(left.isRefactoring() || right.isRefactoring())) {
                refactorings.add(pair);
                left.enableRefactoring();
                right.enableRefactoring();
            }
        }

        return refactorings;
    }

}
