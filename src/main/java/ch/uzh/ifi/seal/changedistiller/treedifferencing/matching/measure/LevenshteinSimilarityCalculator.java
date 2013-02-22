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

import org.apache.commons.lang3.StringUtils;

/**
 * Implementation of a {@link StringSimilarityCalculator} based on the Levenshtein distance.
 * 
 * @author Beat Fluri
 */
public class LevenshteinSimilarityCalculator implements StringSimilarityCalculator {

    @Override
    public double calculateSimilarity(String left, String right) {
        double levenshteinDistance = StringUtils.getLevenshteinDistance(left, right);
        double worstCaseDistance = calculateWorstCaseDistance(left, right);
        if (worstCaseDistance != 0d) {
            return (worstCaseDistance - levenshteinDistance) / worstCaseDistance;
        }
        return 0d;
    }

    private double calculateWorstCaseDistance(String source, String target) {
        double sourceLen = source.length();
        double targetLen = target.length();
        double maxDistance = 0d;

        if (targetLen == sourceLen) {
            maxDistance = sourceLen;
        } else if (targetLen > sourceLen) {
            maxDistance = sourceLen;
            maxDistance += targetLen - sourceLen;
        } else {
            maxDistance = targetLen;
            maxDistance += sourceLen - targetLen;
        }
        return maxDistance;
    }
}
