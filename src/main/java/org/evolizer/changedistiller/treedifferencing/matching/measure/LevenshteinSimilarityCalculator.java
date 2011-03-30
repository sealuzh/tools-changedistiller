package org.evolizer.changedistiller.treedifferencing.matching.measure;

import org.apache.commons.lang.StringUtils;

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
