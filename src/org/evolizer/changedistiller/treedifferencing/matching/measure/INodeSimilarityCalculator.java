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

import java.util.Set;

import org.evolizer.changedistiller.treedifferencing.Node;
import org.evolizer.changedistiller.treedifferencing.NodePair;

/**
 * Interface for (inner) node similarity calculators.
 * 
 * @author fluri
 * 
 */
public interface INodeSimilarityCalculator {

    /**
     * Returns the similarity between the two given {@link Node}s.
     * 
     * @param left
     *            to calculate the similarity with right
     * @param right
     *            to calculate the similarity with left
     * @return the similarity between the two given nodes
     */
    double calculateSimilarity(Node left, Node right);

    /**
     * Sets the matching set of leafs in case the similarity calculator needs these information.
     * 
     * @param leafMatchSet
     *            the matching set of leafs
     * @see ChawatheCalculator
     */
    void setLeafMatchSet(Set<? extends NodePair> leafMatchSet);
}
