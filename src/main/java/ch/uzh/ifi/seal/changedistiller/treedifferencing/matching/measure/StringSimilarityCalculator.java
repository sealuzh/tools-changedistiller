package ch.uzh.ifi.seal.changedistiller.treedifferencing.matching.measure;

/**
 * Interface for string similarity calculators.
 * 
 * @author Beat Fluri
 * @see NGramsCalculator
 * @see TokenBasedCalculator
 * @see LevenshteinSimilarityCalculator
 */
public interface StringSimilarityCalculator {

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
