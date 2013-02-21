package ch.uzh.ifi.seal.changedistiller.treedifferencing.matching;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;

import ch.uzh.ifi.seal.changedistiller.treedifferencing.NodePair;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.TreeDifferencingTestCase;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.TreeMatcher;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.matching.MatchingFactory;

public abstract class WhenTreeNodesAreMatched extends TreeDifferencingTestCase {

    public Set<NodePair> fMatchSet;
    public TreeMatcher fMatcher;

    @Override
    @Before
    public void setup() throws Exception {
        super.setup();
        fMatchSet = new HashSet<NodePair>();
        fMatcher = MatchingFactory.getMatcher(fMatchSet);
    }

    protected void createMatchSet() {
        fMatcher.match(fRootLeft, fRootRight);
    }

}
