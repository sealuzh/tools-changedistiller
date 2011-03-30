package org.evolizer.changedistiller.distilling.refactoring;

import org.evolizer.changedistiller.ast.ASTHelper;
import org.evolizer.changedistiller.model.entities.ClassHistory;
import org.evolizer.changedistiller.model.entities.StructureEntityVersion;
import org.evolizer.changedistiller.structuredifferencing.StructureNode;
import org.evolizer.changedistiller.treedifferencing.matching.measure.LevenshteinSimilarityCalculator;

/**
 * Helps finding refactorings of fields.
 * 
 * @author Beat Fluri
 * @see AbstractRefactoringHelper
 */
public class FieldRefactoringHelper extends AbstractRefactoringHelper {

    /**
     * Creates a new refactoring helper.
     * 
     * @param classHistory
     *            on which the helper creates new {@link StructureEntityVersion}s
     * @param astHelper
     *            to help the refactoring helper
     */
    public FieldRefactoringHelper(ClassHistory classHistory, ASTHelper<StructureNode> astHelper) {
        super(classHistory, astHelper);
        setThreshold(0.65);
    }

    @Override
    public StructureEntityVersion createStructureEntityVersion(StructureNode node) {
        return getASTHelper().createFieldInClassHistory(getClassHistory(), node);
    }

    @Override
    public StructureEntityVersion createStructureEntityVersion(StructureNode node, String newEntityName) {
        StructureEntityVersion attribute = createStructureEntityVersion(node);
        if (!node.getFullyQualifiedName().equals(newEntityName)) {
            attribute.setUniqueName(newEntityName);
            getClassHistory().overrideAttributeHistory(node.getFullyQualifiedName(), newEntityName);
        }
        return attribute;
    }

    @Override
    public String extractShortName(String uniqueName) {
        int pos = uniqueName.indexOf(':');
        if (pos > 0) {
            return uniqueName.substring(0, pos);
        }
        return uniqueName.substring(0);
    }

    @Override
    public double similarity(StructureNode left, StructureNode right) {
        if (!left.getName().equals(right.getName())) {
            return new LevenshteinSimilarityCalculator().calculateSimilarity(left.getContent(), right.getContent());
        } else {
            return 1.0;
        }
    }
}
