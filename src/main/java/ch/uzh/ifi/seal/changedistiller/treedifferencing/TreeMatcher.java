package ch.uzh.ifi.seal.changedistiller.treedifferencing;

import java.util.Set;

import ch.uzh.ifi.seal.changedistiller.treedifferencing.matching.measure.NodeSimilarityCalculator;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.matching.measure.StringSimilarityCalculator;

/**
 * Interface for all tree matcher. A tree matcher takes two tree (left and right) and finds a match between the nodes of
 * the trees.
 * 
 * @author Beat Fluri
 */
public interface TreeMatcher {

    /**
     * Initializes the tree matcher.
     * 
     * @param leafStringSimilarityCalculator
     *            the string similarity calculator for leafs
     * @param leafStringSimilarityThreshold
     *            the threshold to verify whether two leafs are similar
     * @param nodeSimilarityCalculator
     *            the (inner) node similarity calculator
     * @param nodeSimilarityThreshold
     *            the threshold to verify whether two (inner) nodes are similar
     */
    void init(
            StringSimilarityCalculator leafStringSimilarityCalculator,
            double leafStringSimilarityThreshold,
            NodeSimilarityCalculator nodeSimilarityCalculator,
            double nodeSimilarityThreshold);

    /**
     * Initializes the tree matcher.
     * 
     * @param leafStringSimilarityCalculator
     *            the string similarity calculator for leafs
     * @param leafStringSimilarityThreshold
     *            the threshold to verify whether two leafs are similar
     * @param nodeStringSimilarityCalculator
     *            the string similarity calculator for (inner) nodes
     * @param nodeStringSimilarityThreshold
     *            the threshold to verify whether two (inner) nodes have similar string representations
     * @param nodeSimilarityCalculator
     *            the (inner) node similarity calculator
     * @param nodeSimilarityThreshold
     *            the threshold to verify whether two (inner) nodes are similar
     */
    void init(
            StringSimilarityCalculator leafStringSimilarityCalculator,
            double leafStringSimilarityThreshold,
            StringSimilarityCalculator nodeStringSimilarityCalculator,
            double nodeStringSimilarityThreshold,
            NodeSimilarityCalculator nodeSimilarityCalculator,
            double nodeSimilarityThreshold);

    /**
     * Calculate the matching between the two given {@link Node} trees
     * 
     * @param left
     *            to match with right
     * @param right
     *            to match with left
     */
    void match(Node left, Node right);

    /**
     * Sets the matching set where match(Node, Node) stores the matching.
     * 
     * @param matchingSet
     *            set in which the matching is stored
     */
    void setMatchingSet(Set<NodePair> matchingSet);

    /**
     * Enables dynamic threshold for the given depth of {@link Node} trees.
     * 
     * @param depth
     *            the depth for which dynamic threshold is enabled
     * @param threshold
     *            the threshold to use when dynamic threshold is active
     */
    void enableDynamicThreshold(int depth, double threshold);

    /**
     * Disable dynamic threshold.
     */
    void disableDynamicThreshold();
}
