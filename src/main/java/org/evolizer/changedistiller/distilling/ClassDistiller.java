package org.evolizer.changedistiller.distilling;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.evolizer.changedistiller.ast.ASTHelper;
import org.evolizer.changedistiller.distilling.refactoring.RefactoringCandidate;
import org.evolizer.changedistiller.distilling.refactoring.RefactoringCandidateContainer;
import org.evolizer.changedistiller.distilling.refactoring.RefactoringCandidateProcessor;
import org.evolizer.changedistiller.model.entities.ClassHistory;
import org.evolizer.changedistiller.model.entities.Delete;
import org.evolizer.changedistiller.model.entities.Insert;
import org.evolizer.changedistiller.model.entities.SourceCodeChange;
import org.evolizer.changedistiller.model.entities.SourceCodeEntity;
import org.evolizer.changedistiller.model.entities.StructureEntityVersion;
import org.evolizer.changedistiller.structuredifferencing.StructureDiffNode;
import org.evolizer.changedistiller.structuredifferencing.StructureNode;
import org.evolizer.changedistiller.treedifferencing.Node;

/**
 * Extracts changes from a class {@link StructureDiffNode}.
 * 
 * @author Beat Fluri
 */
public class ClassDistiller {

    private StructureDiffNode fClassDiffNode;
    private ClassHistory fClassHistory;
    private ASTHelper<StructureNode> fLeftASTHelper;
    private ASTHelper<StructureNode> fRightASTHelper;
    private RefactoringCandidateProcessor fRefactoringProcessor;
    private DistillerFactory fDistillerFactory;

    private StructureEntityVersion fRootEntity;
    private SourceCodeEntity fParentEntity;
    private List<SourceCodeChange> fChanges;
    private RefactoringCandidateContainer fRefactoringContainer;

    /**
     * Creates a new class distiller.
     * 
     * @param classNode
     *            of which the changes should be extracted
     * @param classHistory
     *            to which the changes should be attached
     * @param leftASTHelper
     *            aids getting info from the left AST
     * @param rightASTHelper
     *            aids getting info from the right AST
     * @param refactoringProcessor
     *            to process potential refactorings
     * @param distillerFactory
     *            to create distillers
     */
    public ClassDistiller(
            StructureDiffNode classNode,
            ClassHistory classHistory,
            ASTHelper<StructureNode> leftASTHelper,
            ASTHelper<StructureNode> rightASTHelper,
            RefactoringCandidateProcessor refactoringProcessor,
            DistillerFactory distillerFactory) {
        fClassDiffNode = classNode;
        fClassHistory = classHistory;
        fLeftASTHelper = leftASTHelper;
        fRightASTHelper = rightASTHelper;
        fRefactoringProcessor = refactoringProcessor;
        fDistillerFactory = distillerFactory;
        fChanges = new LinkedList<SourceCodeChange>();
        fRefactoringContainer = new RefactoringCandidateContainer();
    }

    /**
     * Extract the {@link SourceCodeChange}s of the {@link StructureDiffNode} with which the class distiller was
     * initialized.
     */
    public void extractChanges() {
        fParentEntity = fLeftASTHelper.createSourceCodeEntity(fClassDiffNode.getLeft());
        fRootEntity = fLeftASTHelper.createStructureEntityVersion(fClassDiffNode.getLeft());
        processDeclarationChanges(fClassDiffNode, fRootEntity);
        fChanges.addAll(fRootEntity.getSourceCodeChanges());
        processChildren();
        fRefactoringProcessor.processRefactoringCandidates(
                fClassHistory,
                fLeftASTHelper,
                fRightASTHelper,
                fRefactoringContainer);
        fChanges.addAll(fRefactoringProcessor.getSourceCodeChanges());
        cleanupInnerClassHistories();
    }

    private void cleanupInnerClassHistories() {
        for (Iterator<ClassHistory> it = fClassHistory.getInnerClassHistories().values().iterator(); it.hasNext();) {
            ClassHistory ch = it.next();
            if (!ch.hasChanges()) {
                it.remove();
            }
        }
    }

    private void processChildren() {
        for (StructureDiffNode child : fClassDiffNode.getChildren()) {
            processChildDiffNode(child);
        }
    }

    private void processChildDiffNode(StructureDiffNode diffNode) {
        if (diffNode.isClassOrInterfaceDiffNode()) {
            if (diffNode.isAddition() || diffNode.isDeletion()) {
                processChanges(diffNode);
            } else {
                processClassDiffNode(diffNode);
            }
        } else if (diffNode.isMethodOrConstructorDiffNode() || diffNode.isFieldDiffNode()) {
            processChanges(diffNode);
        }
    }

    private void processClassDiffNode(StructureDiffNode diffNode) {
        ClassHistory classHistory = fClassHistory.createInnerClassHistory(fRootEntity);
        ClassDistiller classDistiller =
                new ClassDistiller(
                        diffNode,
                        classHistory,
                        fLeftASTHelper,
                        fRightASTHelper,
                        fRefactoringProcessor,
                        fDistillerFactory);
        classDistiller.extractChanges();
        fChanges.addAll(classDistiller.getSourceCodeChanges());
    }

    public List<SourceCodeChange> getSourceCodeChanges() {
        return fChanges;
    }

    private void processChanges(StructureDiffNode diffNode) {
        if (diffNode.isAddition()) {
            Insert insert =
                    new Insert(fRootEntity, fRightASTHelper.createSourceCodeEntity(diffNode.getRight()), fParentEntity);
            fRefactoringContainer.addCandidate(new RefactoringCandidate(insert, diffNode));
        } else if (diffNode.isDeletion()) {
            Delete delete =
                    new Delete(fRootEntity, fLeftASTHelper.createSourceCodeEntity(diffNode.getLeft()), fParentEntity);
            fRefactoringContainer.addCandidate(new RefactoringCandidate(delete, diffNode));
        } else if (diffNode.isChanged()) {
            processFineGrainedChanges(diffNode);
        }
    }

    private void processFineGrainedChanges(StructureDiffNode diffNode) {
        StructureEntityVersion entity = fRightASTHelper.createStructureEntityVersion(diffNode.getRight());
        if (diffNode.isMethodOrConstructorDiffNode()) {
            entity = createMethodStructureEntity(diffNode);
        } else if (diffNode.isFieldDiffNode()) {
            entity = createFieldStructureEntity(diffNode);
        } else if (diffNode.isClassOrInterfaceDiffNode()) {
            entity = createInnerClassStructureEntity(diffNode);
        }
        processBodyChanges(diffNode, entity);
        processDeclarationChanges(diffNode, entity);
        if (!entity.getSourceCodeChanges().isEmpty()) {
            fChanges.addAll(entity.getSourceCodeChanges());
        } else {
            if (diffNode.isMethodOrConstructorDiffNode()) {
                fClassHistory.deleteMethod(entity);
            } else if (diffNode.isFieldDiffNode()) {
                fClassHistory.deleteAttribute(entity);
            }
        }
    }

    private StructureEntityVersion createInnerClassStructureEntity(StructureDiffNode diffNode) {
        return fRightASTHelper.createInnerClassInClassHistory(fClassHistory, diffNode.getRight());
    }

    private StructureEntityVersion createFieldStructureEntity(StructureDiffNode diffNode) {
        return fRightASTHelper.createFieldInClassHistory(fClassHistory, diffNode.getRight());
    }

    private StructureEntityVersion createMethodStructureEntity(StructureDiffNode diffNode) {
        return fRightASTHelper.createMethodInClassHistory(fClassHistory, diffNode.getRight());
    }

    private void processDeclarationChanges(StructureDiffNode diffNode, StructureEntityVersion rootEntity) {
        extractChanges(
                fLeftASTHelper.createDeclarationTree(diffNode.getLeft()),
                fRightASTHelper.createDeclarationTree(diffNode.getRight()),
                rootEntity);
    }

    private void processBodyChanges(StructureDiffNode diffNode, StructureEntityVersion rootEntity) {
        extractChanges(
                fLeftASTHelper.createMethodBodyTree(diffNode.getLeft()),
                fRightASTHelper.createMethodBodyTree(diffNode.getRight()),
                rootEntity);
    }

    private void extractChanges(Node left, Node right, StructureEntityVersion rootEntity) {
        Distiller distiller = fDistillerFactory.create(rootEntity);
        distiller.extractClassifiedSourceCodeChanges(left, right);
    }

}
