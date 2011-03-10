package org.evolizer.changedistiller.distilling;

import java.util.List;

import org.evolizer.changedistiller.java.JavaASTHelper;
import org.evolizer.changedistiller.model.entities.SourceCodeChange;
import org.evolizer.changedistiller.model.entities.StructureEntityVersion;
import org.evolizer.changedistiller.treedifferencing.Node;
import org.evolizer.changedistiller.treedifferencing.TreeDifferencer;
import org.evolizer.changedistiller.treedifferencing.TreeEditOperation;

/**
 * Distills and classifies {@link SourceCodeChange} between two {@link Node} trees. Changes are attached to the
 * {@link StructureEntityVersion} in which the changes happened.
 * 
 * @author Beat Fluri
 */
public class Distiller {

    private StructureEntityVersion fStructureEntity;
    private TreeDifferencer fTreeDifferencer;
    private SourceCodeChangeConverter fChangeConverter;
    private SourceCodeChangeClassifier fClassifier;

    /**
     * Creates a new distiller.
     * 
     * @param structureEntity
     *            in which the distiller will extract changes
     * @param treeDifferencer
     *            with which the distiller will extract changes
     * @param classifier
     *            with which the distiller will classify source code changes
     */
    public Distiller(
            StructureEntityVersion structureEntity,
            TreeDifferencer treeDifferencer,
            SourceCodeChangeClassifier classifier) {
        fStructureEntity = structureEntity;
        fChangeConverter =
                new SourceCodeChangeConverter(fStructureEntity, new SourceCodeChangeFactory(new JavaASTHelper()));
        fTreeDifferencer = treeDifferencer;
        fClassifier = classifier;
    }

    /**
     * Extracts and classifies {@link SourceCodeChange}s between the left and the right {@link Node} tree. The
     * {@link SourceCodeChange}s are attached to the {@link StructureEntityVersion}.
     * 
     * @param leftRoot
     *            of the node tree
     * @param rightRoot
     *            of the node tree
     */
    public void extractClassifiedSourceCodeChanges(Node leftRoot, Node rightRoot) {
        if ((leftRoot != null) && (rightRoot != null)) {
            fTreeDifferencer.calculateEditScript(leftRoot, rightRoot);
            List<TreeEditOperation> ops = fTreeDifferencer.getEditScript();
            fChangeConverter.addTreeEditOperationsAsSourceCodeChanges(ops);
            List<SourceCodeChange> classifiedChanges =
                    fClassifier.classifySourceCodeChanges(fChangeConverter.getSourceCodeChanges());
            fStructureEntity.addAllSourceCodeChanges(classifiedChanges);
        }
    }

}
