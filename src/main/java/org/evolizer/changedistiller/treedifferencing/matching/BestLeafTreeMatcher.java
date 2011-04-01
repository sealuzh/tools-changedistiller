package org.evolizer.changedistiller.treedifferencing.matching;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import org.evolizer.changedistiller.treedifferencing.LeafPair;
import org.evolizer.changedistiller.treedifferencing.Node;
import org.evolizer.changedistiller.treedifferencing.NodePair;
import org.evolizer.changedistiller.treedifferencing.TreeMatcher;
import org.evolizer.changedistiller.treedifferencing.matching.measure.NodeSimilarityCalculator;
import org.evolizer.changedistiller.treedifferencing.matching.measure.StringSimilarityCalculator;
import org.evolizer.changedistiller.treedifferencing.matching.measure.TokenBasedCalculator;

/**
 * Implementation of the best matching tree matcher.
 * 
 * @author Beat Fluri
 * 
 */
public class BestLeafTreeMatcher implements TreeMatcher {

    private StringSimilarityCalculator fLeafGenericStringSimilarityCalculator;
    private double fLeafGenericStringSimilarityThreshold;

    // Hardcoded! Needs integration into benchmark facilities.
    private StringSimilarityCalculator fLeafCommentStringSimilarityCalculator = new TokenBasedCalculator();
    private final double fLeafCommentStringSimilarityThreshold = 0.4;

    private NodeSimilarityCalculator fNodeSimilarityCalculator;
    private double fNodeSimilarityThreshold;

    private StringSimilarityCalculator fNodeStringSimilarityCalculator;
    private double fNodeStringSimilarityThreshold;
    private final double fWeightingThreshold = 0.8;

    private boolean fDynamicEnabled;
    private int fDynamicDepth;
    private double fDynamicThreshold;

    private Set<NodePair> fMatch;

    @Override
    public void init(
            StringSimilarityCalculator leafStringSimCalc,
            double leafStringSimThreshold,
            NodeSimilarityCalculator nodeSimCalc,
            double nodeSimThreshold) {
        fLeafGenericStringSimilarityCalculator = leafStringSimCalc;
        fLeafGenericStringSimilarityThreshold = leafStringSimThreshold;
        fNodeStringSimilarityCalculator = leafStringSimCalc;
        fNodeStringSimilarityThreshold = leafStringSimThreshold;
        fNodeSimilarityCalculator = nodeSimCalc;
        fNodeSimilarityThreshold = nodeSimThreshold;
    }

    @Override
    public void init(
            StringSimilarityCalculator leafStringSimCalc,
            double leafStringSimThreshold,
            StringSimilarityCalculator nodeStringSimCalc,
            double nodeStringSimThreshold,
            NodeSimilarityCalculator nodeSimCalc,
            double nodeSimThreshold) {
        init(leafStringSimCalc, leafStringSimThreshold, nodeSimCalc, nodeSimThreshold);
        fNodeStringSimilarityCalculator = nodeStringSimCalc;
        fNodeStringSimilarityThreshold = nodeStringSimThreshold;
    }

    @Override
    public void enableDynamicThreshold(int depth, double threshold) {
        fDynamicDepth = depth;
        fDynamicThreshold = threshold;
        fDynamicEnabled = true;
    }

    @Override
    public void disableDynamicThreshold() {
        fDynamicEnabled = false;
    }

    @Override
    public void setMatchingSet(Set<NodePair> matchingSet) {
        fMatch = matchingSet;
    }

    @Override
    public void match(Node left, Node right) {
        List<LeafPair> matchedLeafs = matchLeaves(left, right);
        // sort matching set according to similarity in descending order
        Collections.sort(matchedLeafs);
        markMatchedLeaves(matchedLeafs);
        matchNodes(left, right);
    }

    @SuppressWarnings("unchecked")
    private void matchNodes(Node left, Node right) {
        for (Enumeration<Node> leftNodes = left.postorderEnumeration(); leftNodes.hasMoreElements();) {
            Node x = leftNodes.nextElement();
            if (!x.isMatched() && (!x.isLeaf() || x.isRoot())) {
                for (Enumeration<Node> rightNodes = right.postorderEnumeration(); rightNodes.hasMoreElements()
                        && !x.isMatched();) {
                    Node y = rightNodes.nextElement();
                    if ((!y.isMatched() && (!y.isLeaf() || y.isRoot())) && equal(x, y)) {
                        fMatch.add(new NodePair(x, y));
                        x.enableMatched();
                        y.enableMatched();
                    }
                }
            }
        }
    }

    private void markMatchedLeaves(List<LeafPair> matchedLeafs) {
        for (LeafPair pair : matchedLeafs) {
            Node x = pair.getLeft();
            Node y = pair.getRight();
            if (!(x.isMatched() || y.isMatched())) {
                fMatch.add(pair);
                x.enableMatched();
                y.enableMatched();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<LeafPair> matchLeaves(Node left, Node right) {
        List<LeafPair> matchedLeafs = new ArrayList<LeafPair>();
        for (Enumeration<Node> leftNodes = left.postorderEnumeration(); leftNodes.hasMoreElements();) {
            Node x = leftNodes.nextElement();
            if (x.isLeaf()) {
                for (Enumeration<Node> rightNodes = right.postorderEnumeration(); rightNodes.hasMoreElements();) {
                    Node y = rightNodes.nextElement();
                    if (y.isLeaf() && haveSameLabel(x, y)) {
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
        return matchedLeafs;
    }

    private boolean haveSameLabel(Node x, Node y) {
        return x.getLabel() == y.getLabel();
    }

    private boolean equal(Node x, Node y) {
        // inner nodes
        if (areInnerOrRootNodes(x, y) && haveSameLabel(x, y)) {
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
        return false;
    }

    private boolean areInnerOrRootNodes(Node x, Node y) {
        return areInnerNodes(x, y) || areRootNodes(x, y);
    }

    private boolean areInnerNodes(Node x, Node y) {
        return (!x.isLeaf() && !y.isLeaf());
    }

    private boolean areRootNodes(Node x, Node y) {
        return (x.isRoot() && y.isRoot());
    }
}
