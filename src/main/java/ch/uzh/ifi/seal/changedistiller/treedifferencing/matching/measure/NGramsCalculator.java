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

import java.util.HashSet;

/**
 * Implementation of the ngrams similarity measure.
 * 
 * @author Beat Fluri
 * 
 */
public class NGramsCalculator implements StringSimilarityCalculator {

    private int fN;

    /**
     * Creates a new ngrams similarity calculator.
     * 
     * @param n
     *            the n in ngrams
     */
    public NGramsCalculator(int n) {
        fN = n;
    }

    public void setN(int n) {
        fN = n;
    }

    @Override
    public double calculateSimilarity(String left, String right) {
        return left.equals(right) ? 1.0 : getSimilarity(createNGrams(left), createNGrams(right));
    }

    private double getSimilarity(HashSet<String> left, HashSet<String> right) {
        int union = left.size() + right.size();
        left.retainAll(right);
        int intersection = left.size();
        return intersection * 2.0 / union;
    }

    private HashSet<String> createNGrams(String fullString) {
        HashSet<String> ngrams = new HashSet<String>();
        for (int i = 0; i < fullString.length() - (fN - 1); i++) {
            ngrams.add(fullString.substring(i, i + fN));
        }
        return ngrams;
    }

}
