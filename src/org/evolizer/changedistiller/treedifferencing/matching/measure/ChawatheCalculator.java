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
package org.evolizer.changedistiller.treedifferencing.matching.measure;

import java.util.Enumeration;
import java.util.Set;

import org.evolizer.changedistiller.treedifferencing.Node;
import org.evolizer.changedistiller.treedifferencing.NodePair;

/**
 * Implementation of the default inner node similarity calculator proposed by Chawathe.
 * 
 * @author fluri
 * 
 */
public class ChawatheCalculator implements INodeSimilarityCalculator {

    private Set<? extends NodePair> fLeafMatchSet;

    /**
     * {@inheritDoc}
     */
    public void setLeafMatchSet(Set<? extends NodePair> leafMatchSet) {
        fLeafMatchSet = leafMatchSet;
    }

    /**
     * {@inheritDoc}
     */
    public double calculateSimilarity(Node left, Node right) {
        int common = 0;
        // common(x, y) = {(w, z) in M | x contains w, and y contains z}
        // |common|
        for (NodePair p : fLeafMatchSet) {
            Node l = p.getLeft();
            Node r = p.getRight();
            if (left.isNodeDescendant(l) && l.isLeaf() && !isComment(l) && right.isNodeDescendant(r) && r.isLeaf()
                    && !isComment(r)) {
                common++;
            }
        }
        int max = maxLeafStatements(left, right);
        return (double) common / (double) max;
    }

    private int maxLeafStatements(Node left, Node right) {
        int leftLeafStatements = left.getLeafCount() - numberOfCommentNodes(left);
        int rightLeafStatements = right.getLeafCount() - numberOfCommentNodes(right);

        return Math.max(leftLeafStatements, rightLeafStatements);
    }

    @SuppressWarnings("unchecked")
    private int numberOfCommentNodes(Node node) {
        int count = 0;

        Enumeration<Node> nodes = node.breadthFirstEnumeration();
        while (nodes.hasMoreElements()) {
            Node child = nodes.nextElement();
            if (isComment(child)) {
                count++;
            }
        }

        return count;
    }

    private boolean isComment(Node node) {
        return node.getLabel().isComment();
    }
}
