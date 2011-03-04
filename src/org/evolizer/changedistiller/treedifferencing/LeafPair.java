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
package org.evolizer.changedistiller.treedifferencing;

/**
 * Pair of leafs
 * <p>
 * As with {@link NodePair}, they are comparable according to a given similarity.
 * 
 * @author fluri
 * 
 */
public class LeafPair extends NodePair implements Comparable<LeafPair> {

    private double fSimilarity;

    /**
     * Creates a new leaf pair.
     * 
     * @param left
     *            the left leaf
     * @param right
     *            the right leaf
     */
    public LeafPair(Node left, Node right) {
        super(left, right);
    }

    /**
     * Creates a new leaf pair.
     * 
     * @param left
     *            the left leaf
     * @param right
     *            the right leaf
     * @param similarity
     *            similarity between the two leafs
     */
    public LeafPair(Node left, Node right, Double similarity) {
        super(left, right);
        fSimilarity = similarity;
    }

    /**
     * Returns the similarity between the two leafs.
     * 
     * @return the similarity between the two leafs
     */
    public double getSimilarity() {
        return fSimilarity;
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(LeafPair other) {
        return -Double.compare(fSimilarity, other.getSimilarity());
    }

}
