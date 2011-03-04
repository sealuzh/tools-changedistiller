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

import org.evolizer.changedistiller.treedifferencing.Node;

/**
 * Interface for string similarity calculators.
 * 
 * <p>
 * Usually the strings are taken from the {@link Node} values.
 * 
 * @author fluri
 * @see LevenshteinCalculator
 * @see NGramsCalculator
 * @see TokenBasedCalculator
 */
public interface IStringSimilarityCalculator {

    /**
     * Returns the similarity between two strings.
     * 
     * @param left
     *            to calculate the similarity with right
     * @param right
     *            to calculate the similarity with left
     * @return the similarity between the two strings
     */
    double calculateSimilarity(String left, String right);

}
