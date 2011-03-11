package org.evolizer.changedistiller.distilling;

import java.util.List;

import org.evolizer.changedistiller.model.entities.SourceCodeChange;
import org.evolizer.changedistiller.model.entities.StructureEntityVersion;
import org.evolizer.changedistiller.treedifferencing.Node;
import org.evolizer.changedistiller.treedifferencing.TreeDifferencer;
import org.evolizer.changedistiller.treedifferencing.TreeEditOperation;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

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

    @Inject
    Distiller(
            @Assisted StructureEntityVersion structureEntity,
            TreeDifferencer treeDifferencer,
            SourceCodeChangeClassifier classifier) {
        fStructureEntity = structureEntity;
        fChangeConverter = new SourceCodeChangeConverter(fStructureEntity);
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
