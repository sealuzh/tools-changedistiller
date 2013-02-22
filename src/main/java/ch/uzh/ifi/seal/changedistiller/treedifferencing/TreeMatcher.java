package ch.uzh.ifi.seal.changedistiller.treedifferencing;

/*
 * #%L
 * ChangeDistiller
 * %%
 * Copyright (C) 2011 - 2013 Software Architecture and Evolution Lab, Department of Informatics, UZH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
