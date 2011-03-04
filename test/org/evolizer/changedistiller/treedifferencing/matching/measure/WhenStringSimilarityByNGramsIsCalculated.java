package org.evolizer.changedistiller.treedifferencing.matching.measure;

public class WhenStringSimilarityByNGramsIsCalculated extends WhenStringSimilarityIsCalculated {

    protected double calculateSimilarity(String left, String right) {
        return new NGramsCalculator(2).calculateSimilarity(left, right);
    }

}
