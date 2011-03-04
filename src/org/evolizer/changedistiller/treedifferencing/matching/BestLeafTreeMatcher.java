/*
 * Copyright 2009 University of Zurich, Switzerland
 *
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
 */
package org.evolizer.changedistiller.treedifferencing.matching;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import org.evolizer.changedistiller.treedifferencing.ITreeMatcher;
import org.evolizer.changedistiller.treedifferencing.LeafPair;
import org.evolizer.changedistiller.treedifferencing.Node;
import org.evolizer.changedistiller.treedifferencing.NodePair;
import org.evolizer.changedistiller.treedifferencing.matching.measure.INodeSimilarityCalculator;
import org.evolizer.changedistiller.treedifferencing.matching.measure.IStringSimilarityCalculator;
import org.evolizer.changedistiller.treedifferencing.matching.measure.TokenBasedCalculator;

/**
 * Implementation of the best matching tree matcher.
 * 
 * @author fluri
 * 
 */
public class BestLeafTreeMatcher implements ITreeMatcher {

    private IStringSimilarityCalculator fLeafGenericStringSimilarityCalculator;
    private double fLeafGenericStringSimilarityThreshold;

    // Hardcoded! Needs integration into benchmark facilities.
    private IStringSimilarityCalculator fLeafCommentStringSimilarityCalculator = new TokenBasedCalculator();
    private final double fLeafCommentStringSimilarityThreshold = 0.4;

    private INodeSimilarityCalculator fNodeSimilarityCalculator;
    private double fNodeSimilarityThreshold;

    private IStringSimilarityCalculator fNodeStringSimilarityCalculator;
    private double fNodeStringSimilarityThreshold;
    private final double fWeightingThreshold = 0.8;

    private boolean fDynamicEnabled;
    private int fDynamicDepth;
    private double fDynamicThreshold;

    private Set<NodePair> fMatch;

    /**
     * {@inheritDoc}
     */
    public void init(
            IStringSimilarityCalculator leafStringSimCalc,
            double leafStringSimThreshold,
            INodeSimilarityCalculator nodeSimCalc,
            double nodeSimThreshold) {
        fLeafGenericStringSimilarityCalculator = leafStringSimCalc;
        fLeafGenericStringSimilarityThreshold = leafStringSimThreshold;
        fNodeStringSimilarityCalculator = leafStringSimCalc;
        fNodeStringSimilarityThreshold = leafStringSimThreshold;
        fNodeSimilarityCalculator = nodeSimCalc;
        fNodeSimilarityThreshold = nodeSimThreshold;
    }

    /**
     * {@inheritDoc}
     */
    public void init(
            IStringSimilarityCalculator leafStringSimCalc,
            double leafStringSimThreshold,
            IStringSimilarityCalculator nodeStringSimCalc,
            double nodeStringSimThreshold,
            INodeSimilarityCalculator nodeSimCalc,
            double nodeSimThreshold) {
        init(leafStringSimCalc, leafStringSimThreshold, nodeSimCalc, nodeSimThreshold);
        fNodeStringSimilarityCalculator = nodeStringSimCalc;
        fNodeStringSimilarityThreshold = nodeStringSimThreshold;
    }

    /**
     * {@inheritDoc}
     */
    public void enableDynamicThreshold(int depth, double threshold) {
        fDynamicDepth = depth;
        fDynamicThreshold = threshold;
        fDynamicEnabled = true;
    }

    /**
     * {@inheritDoc}
     */
    public void disableDynamicThreshold() {
        fDynamicEnabled = false;
    }

    /**
     * {@inheritDoc}
     */
    public void setMatchingSet(Set<NodePair> matchingSet) {
        fMatch = matchingSet;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public void match(Node left, Node right) {
        List<LeafPair> matchedLeafs = new ArrayList<LeafPair>();
        for (Enumeration<Node> leftNodes = left.postorderEnumeration(); leftNodes.hasMoreElements();) {
            Node x = leftNodes.nextElement();
            if (x.isLeaf()) {
                for (Enumeration<Node> rightNodes = right.postorderEnumeration(); rightNodes.hasMoreElements();) {
                    Node y = rightNodes.nextElement();
                    if (y.isLeaf()) {
                        if (x.getLabel() == y.getLabel()) {
                            double similarity = 0;

                            if (x.getLabel().isComment()) {
                                similarity =
                                        fLeafCommentStringSimilarityCalculator.calculateSimilarity(
                                                x.getValue(),
                                                y.getValue());

                                // Important! Otherwhise nodes that match poorly will make it into final matching set,
                                // if no better matches are found!
                                if (similarity >= fLeafCommentStringSimilarityThreshold) {
                                    matchedLeafs.add(new LeafPair(x, y, similarity));
                                }

                            } else { // ...other statements.
                                similarity =
                                        fLeafGenericStringSimilarityCalculator.calculateSimilarity(
                                                x.getValue(),
                                                y.getValue());

                                // Important! Otherwhise nodes that match poorly will make it into final matching set,
                                // if no better matches are found!
                                if (similarity >= fLeafGenericStringSimilarityThreshold) {
                                    matchedLeafs.add(new LeafPair(x, y, similarity));
                                }
                            }
                        }
                    }
                }
            }
        }

        // sort matching set according to similarity in descending order
        Collections.sort(matchedLeafs);

        for (LeafPair pair : matchedLeafs) {
            Node x = pair.getLeft();
            Node y = pair.getRight();
            if (!(x.isMatched() || y.isMatched())) {
                fMatch.add(pair);
                x.enableMatched();
                y.enableMatched();
            }
        }

        for (Enumeration<Node> leftNodes = left.postorderEnumeration(); leftNodes.hasMoreElements();) {
            Node x = leftNodes.nextElement();
            // bug found: x.isLeaf() && x.isRoot()
            // if (!(x.isLeaf() || x.isMatched())) {
            if (!x.isMatched() && (!x.isLeaf() || x.isRoot())) {
                for (Enumeration<Node> rightNodes = right.postorderEnumeration(); rightNodes.hasMoreElements()
                        && !x.isMatched();) {
                    Node y = rightNodes.nextElement();
                    // bug found: y.isLeaf() && y.isRoot()
                    // if (!(y.isLeaf() || y.isMatched()) && equal(x, y)) {
                    if ((!y.isMatched() && (!y.isLeaf() || y.isRoot())) && equal(x, y)) {
                        fMatch.add(new NodePair(x, y));
                        x.enableMatched();
                        y.enableMatched();
                    }
                }
            }
        }
    }

    private boolean equal(Node x, Node y) {
        // inner nodes
        if ((!x.isLeaf() && !y.isLeaf()) || (x.isRoot() && y.isRoot())) {
            if (x.getLabel() == y.getLabel()) {
                // little heuristic
                if (x.isRoot()) {
                    return x.getValue().equals(x.getValue());
                } else {
                    double t = fNodeSimilarityThreshold;
                    if (fDynamicEnabled && (x.getLeafCount() < fDynamicDepth) && (y.getLeafCount() < fDynamicDepth)) {
                        t = fDynamicThreshold;
                    }
                    double simNode = fNodeSimilarityCalculator.calculateSimilarity(x, y);
                    double simString = fNodeStringSimilarityCalculator.calculateSimilarity(x.getValue(), y.getValue());
                    if ((simString < fNodeStringSimilarityThreshold) && (simNode >= fWeightingThreshold)) {
                        return true;
                    } else {
                        return (simNode >= t) && (simString >= fNodeStringSimilarityThreshold);
                    }
                }
            }
        }
        return false;
    }
}
