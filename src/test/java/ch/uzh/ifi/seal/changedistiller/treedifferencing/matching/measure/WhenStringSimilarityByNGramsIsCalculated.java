package ch.uzh.ifi.seal.changedistiller.treedifferencing.matching.measure;

import ch.uzh.ifi.seal.changedistiller.treedifferencing.matching.measure.NGramsCalculator;

public class WhenStringSimilarityByNGramsIsCalculated extends WhenStringSimilarityIsCalculated {

    protected double calculateSimilarity(String left, String right) {
        return new NGramsCalculator(2).calculateSimilarity(left, right);
    }

}
