package ch.uzh.ifi.seal.changedistiller.treedifferencing.matching.measure;

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
