package ch.uzh.ifi.seal.changedistiller.distilling;

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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ch.uzh.ifi.seal.changedistiller.ast.ASTHelper;
import ch.uzh.ifi.seal.changedistiller.distilling.refactoring.RefactoringCandidate;
import ch.uzh.ifi.seal.changedistiller.distilling.refactoring.RefactoringCandidateContainer;
import ch.uzh.ifi.seal.changedistiller.distilling.refactoring.RefactoringCandidateProcessor;
import ch.uzh.ifi.seal.changedistiller.model.entities.ClassHistory;
import ch.uzh.ifi.seal.changedistiller.model.entities.Delete;
import ch.uzh.ifi.seal.changedistiller.model.entities.Insert;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.model.entities.StructureEntityVersion;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureDiffNode;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureNode;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;

/**
 * Extracts changes from a class {@link StructureDiffNode}.
 * 
 * @author Beat Fluri
 * @author Giacomo Ghezzi
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
    private String fVersion;

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
     * @param version
     *            the number or ID of the version associated to the changes being distilled
     */
    public ClassDistiller(
            StructureDiffNode classNode,
            ClassHistory classHistory,
            ASTHelper<StructureNode> leftASTHelper,
            ASTHelper<StructureNode> rightASTHelper,
            RefactoringCandidateProcessor refactoringProcessor,
            DistillerFactory distillerFactory,
            String version) {
        fClassDiffNode = classNode;
        fClassHistory = classHistory;
        fLeftASTHelper = leftASTHelper;
        fRightASTHelper = rightASTHelper;
        fRefactoringProcessor = refactoringProcessor;
        fDistillerFactory = distillerFactory;
        fChanges = new LinkedList<SourceCodeChange>();
        fRefactoringContainer = new RefactoringCandidateContainer();
        fVersion = version;
    }

    /**
     * Extract the {@link SourceCodeChange}s of the {@link StructureDiffNode} with which the class distiller was
     * initialized.
     */
    public void extractChanges() {
        fParentEntity = fLeftASTHelper.createSourceCodeEntity(fClassDiffNode.getLeft());
        if (fVersion != null) {
        	fRootEntity = fLeftASTHelper.createStructureEntityVersion(fClassDiffNode.getLeft(), fVersion);
        } else {
        	fRootEntity = fLeftASTHelper.createStructureEntityVersion(fClassDiffNode.getLeft());
        }
        processDeclarationChanges(fClassDiffNode, fRootEntity);
        fChanges.addAll(fRootEntity.getSourceCodeChanges());
        processChildren();
        
        if (fVersion != null) {
        	fRefactoringProcessor.processRefactoringCandidates(fClassHistory,
					fLeftASTHelper, fRightASTHelper, fRefactoringContainer, fVersion);
        } else {
			fRefactoringProcessor.processRefactoringCandidates(fClassHistory,
					fLeftASTHelper, fRightASTHelper, fRefactoringContainer);
        }
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
    	ClassDistiller classDistiller;
    	if (fVersion != null) {
    		ClassHistory classHistory = fClassHistory.createInnerClassHistory(fLeftASTHelper.createStructureEntityVersion(diffNode.getLeft(), fVersion));
    		classDistiller =
                new ClassDistiller(
                        diffNode,
                        classHistory,
                        fLeftASTHelper,
                        fRightASTHelper,
                        fRefactoringProcessor,
                        fDistillerFactory,
                        fVersion);
    	} else {
    		ClassHistory classHistory = fClassHistory.createInnerClassHistory(fLeftASTHelper.createStructureEntityVersion(diffNode.getLeft()));
    		classDistiller =
                new ClassDistiller(
                        diffNode,
                        classHistory,
                        fLeftASTHelper,
                        fRightASTHelper,
                        fRefactoringProcessor,
                        fDistillerFactory);
    	}
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
    	StructureEntityVersion entity;
    	if (fVersion != null) {
    		entity = fRightASTHelper.createStructureEntityVersion(diffNode.getRight(), fVersion);
    	} else {
    		entity = fRightASTHelper.createStructureEntityVersion(diffNode.getRight());
    	}
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
    	if (fVersion != null) {
    		return fRightASTHelper.createInnerClassInClassHistory(fClassHistory, diffNode.getRight(), fVersion);
    	} else {
    		return fRightASTHelper.createInnerClassInClassHistory(fClassHistory, diffNode.getRight());
    	}
    }

    private StructureEntityVersion createFieldStructureEntity(StructureDiffNode diffNode) {
    	if (fVersion != null) {
    		return fRightASTHelper.createFieldInClassHistory(fClassHistory, diffNode.getRight(), fVersion);
    	} else {
    		return fRightASTHelper.createFieldInClassHistory(fClassHistory, diffNode.getRight());
    	}
    }

    private StructureEntityVersion createMethodStructureEntity(StructureDiffNode diffNode) {
    	if (fVersion != null) {
    		return fRightASTHelper.createMethodInClassHistory(fClassHistory, diffNode.getRight(), fVersion);
    	} else {
    		return fRightASTHelper.createMethodInClassHistory(fClassHistory, diffNode.getRight());
    	}
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
