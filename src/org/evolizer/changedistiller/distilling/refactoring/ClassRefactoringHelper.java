package org.evolizer.changedistiller.distilling.refactoring;

import org.evolizer.changedistiller.compilation.ASTHelper;
import org.evolizer.changedistiller.model.entities.ClassHistory;
import org.evolizer.changedistiller.model.entities.StructureEntityVersion;
import org.evolizer.changedistiller.structuredifferencing.StructureNode;
import org.evolizer.changedistiller.treedifferencing.matching.measure.LevenshteinSimilarityCalculator;

/**
 * Helps finding refactorings of classes.
 * 
 * @author Beat Fluri
 * @see AbstractRefactoringHelper
 */
public class ClassRefactoringHelper extends AbstractRefactoringHelper {

    /**
     * Creates a new refactoring helper.
     * 
     * @param classHistory
     *            on which the helper creates new {@link StructureEntityVersion}s
     * @param astHelper
     *            to help the refactoring helper
     */
    public ClassRefactoringHelper(ClassHistory classHistory, ASTHelper<StructureNode> astHelper) {
        super(classHistory, astHelper);
        setThreshold(0.65);
    }

    @Override
    public StructureEntityVersion createStructureEntityVersion(StructureNode node) {
        return getASTHelper().createInnerClassInClassHistory(getClassHistory(), node);
    }

    @Override
    public StructureEntityVersion createStructureEntityVersion(StructureNode node, String newEntityName) {
        StructureEntityVersion clazz = createStructureEntityVersion(node);
        if (!node.getFullyQualifiedName().equals(newEntityName)) {
            clazz.setUniqueName(newEntityName);
            getClassHistory().overrideClassHistory(node.getFullyQualifiedName(), newEntityName);
        }
        return clazz;
    }

    @Override
    public String extractShortName(String fullName) {
        return fullName;
    }

    @Override
    public double similarity(StructureNode left, StructureNode right) {
        return new LevenshteinSimilarityCalculator().calculateSimilarity(left.getName(), right.getName());
    }
}
