package ch.uzh.ifi.seal.changedistiller.distilling.refactoring;

import ch.uzh.ifi.seal.changedistiller.ast.ASTHelper;
import ch.uzh.ifi.seal.changedistiller.model.entities.ClassHistory;
import ch.uzh.ifi.seal.changedistiller.model.entities.StructureEntityVersion;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureNode;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.matching.measure.NGramsCalculator;

/**
 * Helps finding refactorings of methods.
 * 
 * @author Beat Fluri, Giacomo Ghezzi
 * @see AbstractRefactoringHelper
 */
public class MethodRefactoringHelper extends AbstractRefactoringHelper {

    /**
     * Creates a new refactoring helper.
     * 
     * @param classHistory
     *            on which the helper creates new {@link StructureEntityVersion}s
     * @param astHelper
     *            to help the refactoring helper
     */
    public MethodRefactoringHelper(ClassHistory classHistory, ASTHelper<StructureNode> astHelper) {
        super(classHistory, astHelper);
        setThreshold(0.6);
    }

    @Override
    public StructureEntityVersion createStructureEntityVersion(StructureNode node) {
        return getASTHelper().createMethodInClassHistory(getClassHistory(), node);
    }

    @Override
    public StructureEntityVersion createStructureEntityVersionWithID(StructureNode node, String version) {
        return getASTHelper().createMethodInClassHistory(getClassHistory(), node, version);
    }
    
    @Override
    public StructureEntityVersion createStructureEntityVersion(StructureNode node, String newEntityName) {
        StructureEntityVersion method = createStructureEntityVersion(node);
        if (!node.getFullyQualifiedName().equals(newEntityName)) {
            method.setUniqueName(newEntityName);
            getClassHistory().overrideMethodHistory(node.getFullyQualifiedName(), newEntityName);
        }
        return method;
    }

    @Override
    public StructureEntityVersion createStructureEntityVersionWithID(StructureNode node, String newEntityName, String version) {
        StructureEntityVersion method = createStructureEntityVersionWithID(node, version);
        if (!node.getFullyQualifiedName().equals(newEntityName)) {
            method.setUniqueName(newEntityName);
            getClassHistory().overrideMethodHistory(node.getFullyQualifiedName(), newEntityName);
        }
        return method;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String extractShortName(String fullName) {
        int pos = fullName.indexOf('(');
        if (pos > 0) {
            return fullName.substring(0, pos);
        }
        return fullName.substring(0);
    }

    @Override
    public double similarity(StructureNode left, StructureNode right) {
        return new NGramsCalculator(2).calculateSimilarity(left.getName(), right.getName());
    }

}
