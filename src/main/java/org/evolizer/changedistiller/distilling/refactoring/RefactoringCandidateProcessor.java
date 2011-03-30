package org.evolizer.changedistiller.distilling.refactoring;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.evolizer.changedistiller.compilation.ASTHelper;
import org.evolizer.changedistiller.distilling.Distiller;
import org.evolizer.changedistiller.distilling.DistillerFactory;
import org.evolizer.changedistiller.distilling.SourceCodeChangeClassifier;
import org.evolizer.changedistiller.model.entities.ClassHistory;
import org.evolizer.changedistiller.model.entities.SourceCodeChange;
import org.evolizer.changedistiller.model.entities.StructureEntityVersion;
import org.evolizer.changedistiller.model.entities.Update;
import org.evolizer.changedistiller.structuredifferencing.StructureNode;
import org.evolizer.changedistiller.treedifferencing.Node;

import com.google.inject.Inject;

/**
 * Processes {@link RefactoringCandidate}s from a {@link RefactoringCandidateContainer} and adds resulting changes to
 * the {@link ClassHistory}, and its member histories respectively.
 * 
 * @author Beat Fluri
 */
public class RefactoringCandidateProcessor {

    private ClassHistory fClassHistory;
    private ASTHelper<StructureNode> fLeftASTHelper;
    private ASTHelper<StructureNode> fRightASTHelper;
    private DistillerFactory fDistillerFactory;
    private SourceCodeChangeClassifier fChangeClassifier;
    private List<SourceCodeChange> fChanges;

    @Inject
    RefactoringCandidateProcessor(DistillerFactory distillerFactory, SourceCodeChangeClassifier changeClassifier) {
        fDistillerFactory = distillerFactory;
        fChangeClassifier = changeClassifier;
    }

    /**
     * Processes all {@link RefactoringCandidate}s in the {@link RefactoringCandidateContainer} and stores all resulting
     * changes to the {@link ClassHistory}.
     * 
     * @param classHistory
     *            in which the refactorings took place
     * @param leftHelper
     *            to access the AST of the left side of the refactoring
     * @param rightHelper
     *            to access the AST of the right side of the refactoring
     * @param candidates
     *            to process
     */
    public void processRefactoringCandidates(
            ClassHistory classHistory,
            ASTHelper<StructureNode> leftHelper,
            ASTHelper<StructureNode> rightHelper,
            RefactoringCandidateContainer candidates) {
        fClassHistory = classHistory;
        fLeftASTHelper = leftHelper;
        fRightASTHelper = rightHelper;
        fChanges = new LinkedList<SourceCodeChange>();
        processMethodRefactoringCandidates(candidates);
        processFieldRefactoringCandidates(candidates);
        processInnerClassesRefactoringCandidates(candidates);
    }

    private void processInnerClassesRefactoringCandidates(RefactoringCandidateContainer candidates) {
        checkRefactorings(
                candidates.getAddedInnerClasses(),
                candidates.getDeletedInnerClasses(),
                new ClassRefactoringHelper(fClassHistory, fRightASTHelper));
    }

    private void processFieldRefactoringCandidates(RefactoringCandidateContainer candidates) {
        checkRefactorings(candidates.getAddedFields(), candidates.getDeletedFields(), new FieldRefactoringHelper(
                fClassHistory,
                fRightASTHelper));
    }

    private void processMethodRefactoringCandidates(RefactoringCandidateContainer candidates) {
        checkRefactorings(candidates.getAddedMethods(), candidates.getDeletedMethods(), new MethodRefactoringHelper(
                fClassHistory,
                fRightASTHelper));
    }

    private void checkRefactorings(
            List<RefactoringCandidate> added,
            List<RefactoringCandidate> deleted,
            AbstractRefactoringHelper refactoringHelper) {
        StructureEntityVersion clazz = getCurrentClass();
        processRefactorings(refactoringHelper, clazz.getUniqueName(), added, deleted);
        processRemainingDiffs(clazz, added, refactoringHelper, fRightASTHelper);
        processRemainingDiffs(clazz, deleted, refactoringHelper, fLeftASTHelper);
    }

    private StructureEntityVersion getCurrentClass() {
        return fClassHistory.getVersions().get(fClassHistory.getVersions().size() - 1);
    }

    private void processRefactorings(
            AbstractRefactoringHelper refactoringHelper,
            String className,
            List<RefactoringCandidate> added,
            List<RefactoringCandidate> deleted) {
        List<RefactoringPair> refactorings =
                RefactoringExtractor.extractRefactorings(added, deleted, refactoringHelper);
        for (RefactoringPair pair : refactorings) {
            StructureNode leftDrn = pair.getDeletedEntity().getDiffNode().getLeft();
            StructureNode rightDrn = pair.getInsertedEntity().getDiffNode().getRight();

            StructureEntityVersion structureEntityVersion = refactoringHelper.createStructureEntityVersion(rightDrn);

            Node rightRoot = fRightASTHelper.createDeclarationTree(rightDrn);
            // use the new qualified name for the method; otherwise TreeDifferencer detects a return type change
            Node leftRoot = fLeftASTHelper.createDeclarationTree(leftDrn, rightRoot.getValue());
            if (isRenaming(leftDrn, rightDrn, refactoringHelper)) {
                structureEntityVersion =
                        refactoringHelper.createStructureEntityVersion(leftDrn, rightDrn.getFullyQualifiedName());
                Update upd =
                        new Update(
                                structureEntityVersion,
                                fLeftASTHelper.createSourceCodeEntity(leftDrn),
                                fRightASTHelper.createSourceCodeEntity(rightDrn),
                                leftRoot.getEntity());
                structureEntityVersion.addAllSourceCodeChanges(fChangeClassifier.classifySourceCodeChanges(Arrays
                        .asList(upd)));
            }
            extractChanges(leftRoot, rightRoot, structureEntityVersion);
            leftRoot = fLeftASTHelper.createMethodBodyTree(leftDrn);
            rightRoot = fRightASTHelper.createMethodBodyTree(rightDrn);
            extractChanges(leftRoot, rightRoot, structureEntityVersion);
            fChanges.addAll(structureEntityVersion.getSourceCodeChanges());
        }
    }

    private boolean isRenaming(
            StructureNode leftDrn,
            StructureNode rightDrn,
            AbstractRefactoringHelper refactoringHelper) {
        String nameL = refactoringHelper.extractShortName(leftDrn.getName());
        String nameR = refactoringHelper.extractShortName(rightDrn.getName());
        return !nameL.equals(nameR);
    }

    private void extractChanges(Node left, Node right, StructureEntityVersion rootEntity) {
        Distiller distiller = fDistillerFactory.create(rootEntity);
        distiller.extractClassifiedSourceCodeChanges(left, right);
    }

    private void processRemainingDiffs(
            StructureEntityVersion clazz,
            List<RefactoringCandidate> candidates,
            AbstractRefactoringHelper helper,
            ASTHelper<StructureNode> astHelper) {
        for (RefactoringCandidate candidate : candidates) {
            if (!candidate.isRefactoring()) {
                List<SourceCodeChange> classifiedChanges =
                        fChangeClassifier.classifySourceCodeChanges(Arrays.asList(candidate.getSourceCodeChange()));
                clazz.addAllSourceCodeChanges(classifiedChanges);
                fChanges.addAll(classifiedChanges);
            }
        }
    }

    public List<SourceCodeChange> getSourceCodeChanges() {
        return fChanges;
    }

}
