package org.evolizer.changedistiller.distilling;

import java.util.List;

import org.evolizer.changedistiller.model.classifiers.ChangeType;
import org.evolizer.changedistiller.model.entities.SourceCodeChange;

/**
 * Classifies {@link SourceCodeChange}s into {@link ChangeType}s
 * 
 * @author Beat Fluri
 */
public interface SourceCodeChangeClassifier {

    /**
     * Classifies (according to the taxonomy of source code changes) and returns a {@link List} of
     * {@link SourceCodeChange}s by giving each change a {@link ChangeType}.
     * 
     * @param sourceCodeChanges
     *            to classify
     * @return the list of classified source code changes
     */
    List<SourceCodeChange> classifySourceCodeChanges(List<SourceCodeChange> sourceCodeChanges);

}
