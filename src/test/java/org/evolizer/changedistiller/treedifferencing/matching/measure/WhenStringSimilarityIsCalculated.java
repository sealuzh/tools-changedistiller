package org.evolizer.changedistiller.treedifferencing.matching.measure;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import org.junit.Test;

public abstract class WhenStringSimilarityIsCalculated {

    protected abstract double calculateSimilarity(String left, String right);

    @Test
    public void emptyStringsShouldBeSimilar() throws Exception {
        assertThat(calculateSimilarity("", ""), is(1.0));
    }

    @Test
    public void identicalStringsShouldBeSimilar() throws Exception {
        assertThat(calculateSimilarity("change distiller", "change distiller"), is(1.0));
    }

    @Test
    public void stringsWithTheSameTokensShouldBeSimilar() throws Exception {
        assertThat(calculateSimilarity("change distiller", "distiller change"), is(closeTo(1.0, 0.5)));
    }

    @Test
    public void completelyDifferentStringsShouldNotBeSimilar() throws Exception {
        assertThat(calculateSimilarity("change distiller", "merlin sofa"), is(closeTo(0.0, 0.1)));
    }

    @Test
    public void stringsWithTheCommonTokensShouldBeSimilar() throws Exception {
        assertThat(calculateSimilarity("change distiller", "merlin distiller change"), is(closeTo(1.0, 0.5)));
    }

}
