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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.number.IsCloseTo.closeTo;

import org.junit.Test;

import ch.uzh.ifi.seal.changedistiller.treedifferencing.matching.measure.TokenBasedCalculator;

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
