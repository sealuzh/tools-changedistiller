package org.evolizer.changedistiller.distilling;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.evolizer.changedistiller.ast.ASTHelper;
import org.evolizer.changedistiller.ast.ASTHelperFactory;
import org.evolizer.changedistiller.distilling.refactoring.RefactoringCandidateProcessor;
import org.evolizer.changedistiller.model.entities.ClassHistory;
import org.evolizer.changedistiller.model.entities.SourceCodeChange;
import org.evolizer.changedistiller.structuredifferencing.StructureDiffNode;
import org.evolizer.changedistiller.structuredifferencing.StructureDifferencer;
import org.evolizer.changedistiller.structuredifferencing.StructureNode;

import com.google.inject.Inject;

/**
 * Distills {@link SourceCodeChange}s between two {@link File}.
 * 
 * @author Beat Fluri
 * @author Giacomo Ghezzi
 */
public class FileDistiller {

    private DistillerFactory fDistillerFactory;
    private ASTHelperFactory fASTHelperFactory;
    private RefactoringCandidateProcessor fRefactoringProcessor;

    private List<SourceCodeChange> fChanges;
    private ASTHelper<StructureNode> fLeftASTHelper;
    private ASTHelper<StructureNode> fRightASTHelper;
    private ClassHistory fClassHistory;
    private String fVersion;

    @Inject
    FileDistiller(
            DistillerFactory distillerFactory,
            ASTHelperFactory factory,
            RefactoringCandidateProcessor refactoringProcessor) {
        fDistillerFactory = distillerFactory;
        fASTHelperFactory = factory;
        fRefactoringProcessor = refactoringProcessor;
    }

    /**
     * Extracts classified {@link SourceCodeChange}s between two {@link File}s.
     * 
     * @param left
     *            file to extract changes
     * @param right
     *            file to extract changes
     */
    @SuppressWarnings("unchecked")
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

    public void extractClassifiedSourceCodeChanges(File left, File right, String version) {
    	fVersion = version;
    	this.extractClassifiedSourceCodeChanges(left, right);
    }
    
    private void processRootChildren(StructureDiffNode diffNode) {
        for (StructureDiffNode child : diffNode.getChildren()) {
            if (child.isClassOrInterfaceDiffNode() && mayHaveChanges(child.getLeft(), child.getRight())) {
                if (fClassHistory == null) {
                	if (fVersion != null) {
                		fClassHistory = new ClassHistory(fRightASTHelper.createStructureEntityVersion(child.getRight(), fVersion));
                	} else {
                		fClassHistory = new ClassHistory(fRightASTHelper.createStructureEntityVersion(child.getRight()));
                	}
                }
                processClassDiffNode(child);
            }
        }
    }

    private void processClassDiffNode(StructureDiffNode child) {
    	ClassDistiller classDistiller;
    	if (fVersion != null) {
        classDistiller =
                new ClassDistiller(
                        child,
                        fClassHistory,
                        fLeftASTHelper,
                        fRightASTHelper,
                        fRefactoringProcessor,
                        fDistillerFactory,
                        fVersion);
    	} else {
    		classDistiller =
                new ClassDistiller(
                        child,
                        fClassHistory,
                        fLeftASTHelper,
                        fRightASTHelper,
                        fRefactoringProcessor,
                        fDistillerFactory);
    	}
        classDistiller.extractChanges();
        fChanges.addAll(classDistiller.getSourceCodeChanges());
    }

    private boolean mayHaveChanges(StructureNode left, StructureNode right) {
        return (left != null) && (right != null);
    }

    public List<SourceCodeChange> getSourceCodeChanges() {
        return fChanges;
    }

    public ClassHistory getClassHistory() {
        return fClassHistory;
    }

}
