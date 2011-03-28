package org.evolizer.changedistiller.distilling;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.evolizer.changedistiller.compilation.ASTHelper;
import org.evolizer.changedistiller.compilation.ASTHelperFactory;
import org.evolizer.changedistiller.model.entities.Delete;
import org.evolizer.changedistiller.model.entities.Insert;
import org.evolizer.changedistiller.model.entities.SourceCodeChange;
import org.evolizer.changedistiller.model.entities.SourceCodeEntity;
import org.evolizer.changedistiller.model.entities.StructureEntityVersion;
import org.evolizer.changedistiller.structuredifferencing.StructureDiffNode;
import org.evolizer.changedistiller.structuredifferencing.StructureDifferencer;
import org.evolizer.changedistiller.structuredifferencing.StructureNode;
import org.evolizer.changedistiller.treedifferencing.Node;

import com.google.inject.Inject;

/**
 * Distills {@link SourceCodeChange}s between two {@link File}.
 * 
 * @author Beat Fluri
 */
public class FileDistiller {

    private DistillerFactory fDistillerFactory;
    private ASTHelperFactory fASTHelperFactory;

    private List<SourceCodeChange> fChanges;
    private ASTHelper<StructureNode> fLeftASTHelper;
    private ASTHelper<StructureNode> fRightASTHelper;
    private List<StructureEntityVersion> fStructureEntityVersions;

    @Inject
    FileDistiller(DistillerFactory distillerFactory, ASTHelperFactory factory) {
        fDistillerFactory = distillerFactory;
        fASTHelperFactory = factory;
        fStructureEntityVersions = new LinkedList<StructureEntityVersion>();
    }

    /**
     * Extracts classified {@link SourceCodeChange}s between two {@link File}s.
     * 
     * @param left
     *            file to extract changes
     * @param right
     *            file to extract changes
     */
    public void extractClassifiedSourceCodeChanges(File left, File right) {
        fLeftASTHelper = fASTHelperFactory.create(left);
        fRightASTHelper = fASTHelperFactory.create(right);
        StructureDifferencer structureDifferencer = new StructureDifferencer();
        structureDifferencer.extractDifferences(
                fLeftASTHelper.createStructureTree(),
                fRightASTHelper.createStructureTree());
        StructureDiffNode structureDiff = structureDifferencer.getDifferences();
        if (structureDiff != null) {
            fChanges = new LinkedList<SourceCodeChange>();
            // first node is (usually) the compilation unit
            processRootChildren(structureDiff);
        }
    }

    private void processRootChildren(StructureDiffNode diffNode) {
        for (StructureDiffNode child : diffNode.getChildren()) {
            if (child.isClassOrInterfaceDiffNode() && mayHaveChanges(child.getLeft(), child.getRight())) {
                processClassDiffNode(child);
            }
        }
    }

    private void processClassDiffNode(StructureDiffNode diffNode) {
        SourceCodeEntity structureEntity = fLeftASTHelper.createSourceCodeEntity(diffNode.getLeft());
        StructureEntityVersion clazz = fLeftASTHelper.createStructureEntityVersion(diffNode.getLeft());
        processDeclarationChanges(diffNode, clazz);
        processChildren(diffNode, clazz, structureEntity);
        fStructureEntityVersions.add(clazz);
    }

    private void processDiffNode(
            StructureDiffNode diffNode,
            StructureEntityVersion rootEntity,
            SourceCodeEntity parentEntity) {
        if (diffNode.isClassOrInterfaceDiffNode()) {
            if (diffNode.isAddition() || diffNode.isDeletion()) {
                processChanges(diffNode, rootEntity, parentEntity);
            } else {
                processClassDiffNode(diffNode);
            }
        } else if (diffNode.isMethodOrConstructorDiffNode() || diffNode.isFieldDiffNode()) {
            processChanges(diffNode, rootEntity, parentEntity);
        }
    }

    private void processChildren(
            StructureDiffNode diffNode,
            StructureEntityVersion rootEntity,
            SourceCodeEntity parentEntity) {
        for (StructureDiffNode child : diffNode.getChildren()) {
            processDiffNode(child, rootEntity, parentEntity);
        }
    }

    private void processChanges(StructureDiffNode node, StructureEntityVersion rootEntity, SourceCodeEntity parentEntity) {
        if (node.isAddition()) {
            Insert insert =
                    new Insert(rootEntity, fRightASTHelper.createSourceCodeEntity(node.getRight()), parentEntity);
            rootEntity.addSourceCodeChange(insert);
            // refactoring
        } else if (node.isDeletion()) {
            Delete delete = new Delete(rootEntity, fLeftASTHelper.createSourceCodeEntity(node.getLeft()), parentEntity);
            rootEntity.addSourceCodeChange(delete);
            // refactoring
        } else if (node.isChanged()) {
            StructureEntityVersion entity = fRightASTHelper.createStructureEntityVersion(node.getRight());
            processBodyChanges(node, entity);
            processDeclarationChanges(node, entity);
            if (!entity.getSourceCodeChanges().isEmpty()) {
                fStructureEntityVersions.add(entity);
            }
        }
    }

    private void processDeclarationChanges(StructureDiffNode diffNode, StructureEntityVersion rootEntity) {
        extractChanges(
                fLeftASTHelper.createDeclarationTree(diffNode.getLeft()),
                fRightASTHelper.createDeclarationTree(diffNode.getRight()),
                rootEntity);
        fChanges.addAll(rootEntity.getSourceCodeChanges());
    }

    private void processBodyChanges(StructureDiffNode diffNode, StructureEntityVersion rootEntity) {
        extractChanges(
                fLeftASTHelper.createMethodBodyTree(diffNode.getLeft()),
                fRightASTHelper.createMethodBodyTree(diffNode.getRight()),
                rootEntity);
        fChanges.addAll(rootEntity.getSourceCodeChanges());
    }

    private void extractChanges(Node left, Node right, StructureEntityVersion rootEntity) {
        Distiller distiller = fDistillerFactory.create(rootEntity);
        distiller.extractClassifiedSourceCodeChanges(left, right);
    }

    private boolean mayHaveChanges(StructureNode left, StructureNode right) {
        return (left != null) && (right != null);
    }

    public List<SourceCodeChange> getSourceCodeChanges() {
        return fChanges;
    }

    public List<StructureEntityVersion> getStructureEntityVersions() {
        return fStructureEntityVersions;
    }

}
