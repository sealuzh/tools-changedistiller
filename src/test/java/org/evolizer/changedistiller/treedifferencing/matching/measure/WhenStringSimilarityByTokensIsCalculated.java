package org.evolizer.changedistiller.treedifferencing.matching.measure;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import org.junit.Test;

public class WhenStringSimilarityByTokensIsCalculated extends WhenStringSimilarityIsCalculated {

    @Test
    public void lineCommentsAndStringsWithCommonTokensShouldBeSimilar() throws Exception {
        assertThat(calculateSimilarity("change distiller", "// merlin distiller\n// change"), is(closeTo(1.0, 0.5)));
    }

    @Test
    public void blockCommentsAndStringsWithCommonTokensShouldBeSimilar() throws Exception {
        assertThat(calculateSimilarity("change distiller", "/* merlin distiller\n* change */"), is(closeTo(1.0, 0.5)));
    }

    @Override
    protected double calculateSimilarity(String left, String right) {
        return new TokenBasedCalculator().calculateSimilarity(left, right);
    }

}
