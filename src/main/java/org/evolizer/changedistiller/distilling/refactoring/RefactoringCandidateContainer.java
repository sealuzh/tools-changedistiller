package org.evolizer.changedistiller.distilling.refactoring;

import java.util.LinkedList;
import java.util.List;

import org.evolizer.changedistiller.structuredifferencing.StructureDiffNode;

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
