package ch.uzh.ifi.seal.changedistiller.treedifferencing.matching.measure;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import ch.uzh.ifi.seal.changedistiller.treedifferencing.matching.measure.LevenshteinSimilarityCalculator;

public class WhenStringSimilarityByLevenshteinIsCalculated {

    private LevenshteinSimilarityCalculator fLevenshtein = new LevenshteinSimilarityCalculator();

    @Test
    // our own implementation should produce the same result as Simpack would (legacy)
    public void checkWithSimpackConformance() throws Exception {
        String sa1 = new String("Language");
        String sa2 = new String("Languages");
        String sa3 = new String("Levenshtein");
        String sa4 = new String("shteinLeven");
        assertThat(getSimilarity(sa1, sa1), is(1d));
        assertThat(getSimilarity(sa1, sa2), is(1d - (1d / 9d)));
        assertThat(getSimilarity(sa2, sa1), is(1d - (1d / 9d)));
        assertThat(getSimilarity("", sa1), is(1d - (8d / 8d)));
        assertThat(getSimilarity(sa3, sa4), is((11d - 8d) / 11d));
        assertThat(getSimilarity(sa1, sa3), is((2d) / 11d));
    }

    private Double getSimilarity(String sa1, String sa2) {
        return fLevenshtein.calculateSimilarity(sa1, sa2);
    }
}
