package ch.uzh.ifi.seal.changedistiller.treedifferencing.matching;

import java.util.Set;

import ch.uzh.ifi.seal.changedistiller.treedifferencing.NodePair;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.TreeMatcher;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.matching.measure.ChawatheCalculator;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.matching.measure.NGramsCalculator;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.matching.measure.NodeSimilarityCalculator;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.matching.measure.StringSimilarityCalculator;

/**
 * Factory to generate a {@link TreeMatcher} out of specified preference values.
 * 
 * @author Beat Fluri
 * 
 * @see TreeMatcher
 * @see BestLeafTreeMatcher
 */
public final class MatchingFactory {

    private MatchingFactory() {}

    /**
     * Returns the default {@link TreeMatcher} {@link BestLeafTreeMatcher}.
     * 
     * @param matchingSet
     *            in which the matcher stores the match pairs
     * @return the best leaf tree matcher
     */
    public static TreeMatcher getMatcher(Set<NodePair> matchingSet) {
        StringSimilarityCalculator leafCalc = new NGramsCalculator(2);
        double lTh = 0.6;

        // node string matching
        StringSimilarityCalculator nodeStringCalc = leafCalc;
        double nStTh = lTh;

        // node matching
        NodeSimilarityCalculator nodeCalc = new ChawatheCalculator();
        nodeCalc.setLeafMatchSet(matchingSet);

        double nTh = 0.0;

        TreeMatcher result = new BestLeafTreeMatcher();
        result.init(leafCalc, lTh, nodeStringCalc, nStTh, nodeCalc, nTh);

        result.enableDynamicThreshold(4, 0.4);
        result.setMatchingSet(matchingSet);
        return result;
    }

}
