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

import java.util.LinkedList;
import java.util.List;

import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureDiffNode;

/**
 * Container for {@link RefactoringCandidate}s.
 * 
 * @author Beat Fluri
 */
public final class RefactoringCandidateContainer {

    private List<RefactoringCandidate> fAddedFields;
    private List<RefactoringCandidate> fDeletedFields;
    private List<RefactoringCandidate> fAddedInnerClasses;
    private List<RefactoringCandidate> fDeletedInnerClasses;
    private List<RefactoringCandidate> fAddedMethods;
    private List<RefactoringCandidate> fDeletedMethods;

    /**
     * Creates a new {@link RefactoringCandidate} container.
     */
    public RefactoringCandidateContainer() {
        fAddedFields = new LinkedList<RefactoringCandidate>();
        fDeletedFields = new LinkedList<RefactoringCandidate>();
        fAddedInnerClasses = new LinkedList<RefactoringCandidate>();
        fDeletedInnerClasses = new LinkedList<RefactoringCandidate>();
        fAddedMethods = new LinkedList<RefactoringCandidate>();
        fDeletedMethods = new LinkedList<RefactoringCandidate>();
    }

    /**
     * Adds the given candidate to the container.
     * 
     * @param candidate
     *            to add to the container
     */
    public void addCandidate(RefactoringCandidate candidate) {
        StructureDiffNode node = candidate.getDiffNode();
        if (node.isAddition()) {
            if (node.getRight().isClassOrInterface()) {
                fAddedInnerClasses.add(candidate);
            } else if (node.getRight().isMethodOrConstructor()) {
                fAddedMethods.add(candidate);
            } else if (node.getRight().isField()) {
                fAddedFields.add(candidate);
            }
        } else if (node.isDeletion()) {
            if (node.getLeft().isClassOrInterface()) {
                fDeletedInnerClasses.add(candidate);
            } else if (node.getLeft().isMethodOrConstructor()) {
                fDeletedMethods.add(candidate);
            } else if (node.getLeft().isField()) {
                fDeletedFields.add(candidate);
            }
        }
    }

    public List<RefactoringCandidate> getAddedFields() {
        return fAddedFields;
    }

    public List<RefactoringCandidate> getDeletedFields() {
        return fDeletedFields;
    }

    public List<RefactoringCandidate> getAddedInnerClasses() {
        return fAddedInnerClasses;
    }

    public List<RefactoringCandidate> getDeletedInnerClasses() {
        return fDeletedInnerClasses;
    }

    public List<RefactoringCandidate> getAddedMethods() {
        return fAddedMethods;
    }

    public List<RefactoringCandidate> getDeletedMethods() {
        return fDeletedMethods;
    }

}
