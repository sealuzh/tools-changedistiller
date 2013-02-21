package ch.uzh.ifi.seal.changedistiller.treedifferencing.matching.measure;

import java.util.Set;

import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.NodePair;

/**
 * Interface for (inner) node similarity calculators.
 * 
 * @author Beat Fluri
 * 
 */
public interface NodeSimilarityCalculator {

    /**
     * Returns the similarity between two {@link Node}s.
     * 
     * @param left
     *            to calculate the similarity with right
     * @param right
     *            to calculate the similarity with left
     * @return the similarity between two nodes
     */
    double calculateSimilarity(Node left, Node right);

    /**
     * Sets the matching set of leafs in case the similarity calculator needs these information.
     * 
     * @param leafMatchSet
     *            the matching set of leafs
     */
    void setLeafMatchSet(Set<? extends NodePair> leafMatchSet);
}
