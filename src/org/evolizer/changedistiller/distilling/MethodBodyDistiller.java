package org.evolizer.changedistiller.distilling;

import java.util.List;

import org.evolizer.changedistiller.java.JavaASTHelper;
import org.evolizer.changedistiller.model.entities.StructureEntityVersion;
import org.evolizer.changedistiller.treedifferencing.Node;
import org.evolizer.changedistiller.treedifferencing.TreeDifferencer;
import org.evolizer.changedistiller.treedifferencing.TreeEditOperation;

public class MethodBodyDistiller {

    private StructureEntityVersion fStructureEntity;
    private TreeDifferencer fTreeDifferencer;
    private SourceCodeChangeConverter fChangeConverter;

    public MethodBodyDistiller(StructureEntityVersion structureEntity, TreeDifferencer treeDifferencer) {
        fStructureEntity = structureEntity;
        fChangeConverter =
                new SourceCodeChangeConverter(fStructureEntity, new SourceCodeChangeFactory(new JavaASTHelper()));
        fTreeDifferencer = treeDifferencer;
    }

    public void extractChanges(Node leftRoot, Node rightRoot) {
        if ((leftRoot != null) && (rightRoot != null)) {
            fTreeDifferencer.calculateEditScript(leftRoot, rightRoot);
            List<TreeEditOperation> ops = fTreeDifferencer.getEditScript();
            fChangeConverter.addTreeEditOperationsAsSourceCodeChanges(ops);
            fStructureEntity.addAllSourceCodeChanges(fChangeConverter.getSourceCodeChanges());
        }
    }

}
