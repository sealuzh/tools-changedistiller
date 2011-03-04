/*
 * Copyright 2009 University of Zurich, Switzerland
 *
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
 */
package org.evolizer.changedistiller.treedifferencing.matching;

import java.util.Set;

import org.evolizer.changedistiller.treedifferencing.ITreeMatcher;
import org.evolizer.changedistiller.treedifferencing.NodePair;
import org.evolizer.changedistiller.treedifferencing.matching.measure.ChawatheCalculator;
import org.evolizer.changedistiller.treedifferencing.matching.measure.INodeSimilarityCalculator;
import org.evolizer.changedistiller.treedifferencing.matching.measure.IStringSimilarityCalculator;
import org.evolizer.changedistiller.treedifferencing.matching.measure.NGramsCalculator;

/**
 * Factory to generate a {@link ITreeMatcher} out of specified preference values.
 * 
 * @author fluri
 * @see ITreeMatcher
 * @see BestLeafTreeMatcher
 * @see DefaultTreeMatcher
 */
public final class MatchingFactory {

    private MatchingFactory() {}

    /**
     * Returns an {@link ITreeMatcher} according to specified preference values.
     * 
     * @param matchingSet
     *            in which the matcher stores the match pairs
     * @return the tree matcher out of specified preference values
     */
    public static ITreeMatcher getMatcher(Set<NodePair> matchingSet) {
        IStringSimilarityCalculator leafCalc = new NGramsCalculator();
        ((NGramsCalculator) leafCalc).setN(2);

        double lTh = 0.6;

        // node string matching
        IStringSimilarityCalculator nodeStringCalc = leafCalc;
        double nStTh = lTh;

        // node matching
        INodeSimilarityCalculator nodeCalc = new ChawatheCalculator();
        nodeCalc.setLeafMatchSet(matchingSet);

        double nTh = 0.0;

        ITreeMatcher result = new BestLeafTreeMatcher();
        result.init(leafCalc, lTh, nodeStringCalc, nStTh, nodeCalc, nTh);

        result.enableDynamicThreshold(4, 0.4);
        result.setMatchingSet(matchingSet);
        return result;
    }

}
