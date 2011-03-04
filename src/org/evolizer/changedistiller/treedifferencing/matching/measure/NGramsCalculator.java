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

import java.util.HashSet;

/**
 * Implementation of the ngrams similarity measure.
 * 
 * @author fluri
 * 
 */
public class NGramsCalculator implements IStringSimilarityCalculator {

    private int fN;

    /**
     * Creates a new ngrams similarity calculator.
     */
    public NGramsCalculator() {}

    /**
     * Creates a new ngrams similarity calculator.
     * 
     * @param n
     *            the n in ngrams
     */
    public NGramsCalculator(int n) {
        fN = n;
    }

    /**
     * Sets the n in ngrams.
     * 
     * @param n
     *            the n in ngrams
     */
    public void setN(int n) {
        fN = n;
    }

    /**
     * {@inheritDoc}
     */
    public double calculateSimilarity(String left, String right) {
        return left.equals(right) ? 1.0 : getSimilarity(ngrams(left), ngrams(right));
    }

    private double getSimilarity(HashSet<String> left, HashSet<String> right) {
        int union = left.size() + right.size();
        left.retainAll(right);
        int intersection = left.size();
        return intersection * 2.0 / union;
    }

    private HashSet<String> ngrams(String fullString) {
        HashSet<String> ngrams = new HashSet<String>();

        for (int i = 0; i < fullString.length() - (fN - 1); i++) {
            ngrams.add(fullString.substring(i, i + fN));
        }
        return ngrams;
    }

}
