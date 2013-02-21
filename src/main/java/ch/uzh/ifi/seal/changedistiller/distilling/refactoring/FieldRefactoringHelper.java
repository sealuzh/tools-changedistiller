package ch.uzh.ifi.seal.changedistiller.distilling.refactoring;

import ch.uzh.ifi.seal.changedistiller.ast.ASTHelper;
import ch.uzh.ifi.seal.changedistiller.model.entities.ClassHistory;
import ch.uzh.ifi.seal.changedistiller.model.entities.StructureEntityVersion;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureNode;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.matching.measure.LevenshteinSimilarityCalculator;

/**
 * Helps finding refactorings of fields.
 * 
 * @author Beat Fluri, Giacomo Ghezzi
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
    public StructureEntityVersion createStructureEntityVersionWithID(StructureNode node, String version) {
        return getASTHelper().createFieldInClassHistory(getClassHistory(), node, version);
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
    public StructureEntityVersion createStructureEntityVersionWithID(StructureNode node, String newEntityName, String version) {
        StructureEntityVersion attribute = createStructureEntityVersionWithID(node, version);
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
