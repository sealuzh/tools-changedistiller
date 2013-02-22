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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


import ch.uzh.ifi.seal.changedistiller.ast.ASTHelper;
import ch.uzh.ifi.seal.changedistiller.distilling.Distiller;
import ch.uzh.ifi.seal.changedistiller.distilling.DistillerFactory;
import ch.uzh.ifi.seal.changedistiller.distilling.SourceCodeChangeClassifier;
import ch.uzh.ifi.seal.changedistiller.model.entities.ClassHistory;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.model.entities.StructureEntityVersion;
import ch.uzh.ifi.seal.changedistiller.model.entities.Update;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureNode;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;

import com.google.inject.Inject;

/**
 * Processes {@link RefactoringCandidate}s from a {@link RefactoringCandidateContainer} and adds resulting changes to
 * the {@link ClassHistory}, and its member histories respectively.
 * 
 * @author Beat Fluri
 * @author Giacomo Ghezzi
 */
public class RefactoringCandidateProcessor {

    private ClassHistory fClassHistory;
    private ASTHelper<StructureNode> fLeftASTHelper;
    private ASTHelper<StructureNode> fRightASTHelper;
    private DistillerFactory fDistillerFactory;
    private SourceCodeChangeClassifier fChangeClassifier;
    private List<SourceCodeChange> fChanges;
    private String fVersion;

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
     * @param version
     *            the number/ID of the version being distilled
     */
    public void processRefactoringCandidates(
            ClassHistory classHistory,
            ASTHelper<StructureNode> leftHelper,
            ASTHelper<StructureNode> rightHelper,
            RefactoringCandidateContainer candidates,
            String version) {
    	fVersion = version;
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
        processRefactorings(refactoringHelper, added, deleted);
        processRemainingDiffs(clazz, added);
        processRemainingDiffs(clazz, deleted);
    }

    private StructureEntityVersion getCurrentClass() {
        return fClassHistory.getVersions().get(fClassHistory.getVersions().size() - 1);
    }

    private void processRefactorings(
            AbstractRefactoringHelper refactoringHelper,
            List<RefactoringCandidate> added,
            List<RefactoringCandidate> deleted) {
        List<RefactoringPair> refactorings =
                RefactoringExtractor.extractRefactorings(added, deleted, refactoringHelper);
        for (RefactoringPair pair : refactorings) {
            StructureNode leftDrn = pair.getDeletedEntity().getDiffNode().getLeft();
            StructureNode rightDrn = pair.getInsertedEntity().getDiffNode().getRight();

            StructureEntityVersion structureEntityVersion;
            if (fVersion != null) {
            	structureEntityVersion = refactoringHelper.createStructureEntityVersionWithID(rightDrn, fVersion);
            } else {
            	structureEntityVersion = refactoringHelper.createStructureEntityVersion(rightDrn);
            }

            Node rightRoot = fRightASTHelper.createDeclarationTree(rightDrn);
            // use the new qualified name for the method; otherwise TreeDifferencer detects a return type change
            Node leftRoot = fLeftASTHelper.createDeclarationTree(leftDrn, rightRoot.getValue());
            if (isRenaming(leftDrn, rightDrn, refactoringHelper)) {
            	if (fVersion != null) {
            		structureEntityVersion =
                        refactoringHelper.createStructureEntityVersionWithID(leftDrn, rightDrn.getFullyQualifiedName(), fVersion);
            	} else {
            		structureEntityVersion =
                        refactoringHelper.createStructureEntityVersion(leftDrn, rightDrn.getFullyQualifiedName());
            	}
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

    private void processRemainingDiffs(StructureEntityVersion clazz, List<RefactoringCandidate> candidates) {
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
