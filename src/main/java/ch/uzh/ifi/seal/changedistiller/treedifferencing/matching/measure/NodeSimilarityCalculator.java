package ch.uzh.ifi.seal.changedistiller.treedifferencing.matching.measure;

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
