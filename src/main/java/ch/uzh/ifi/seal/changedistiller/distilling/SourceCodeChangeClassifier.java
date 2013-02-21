package ch.uzh.ifi.seal.changedistiller.distilling;

import java.util.List;

import ch.uzh.ifi.seal.changedistiller.model.classifiers.ChangeType;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;

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
    List<SourceCodeChange> classifySourceCodeChanges(List<? extends SourceCodeChange> sourceCodeChanges);

}
