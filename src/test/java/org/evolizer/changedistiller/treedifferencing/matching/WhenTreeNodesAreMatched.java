package org.evolizer.changedistiller.treedifferencing.matching;

import java.util.HashSet;
import java.util.Set;

import org.evolizer.changedistiller.treedifferencing.TreeMatcher;
import org.evolizer.changedistiller.treedifferencing.NodePair;
import org.evolizer.changedistiller.treedifferencing.TreeDifferencingTestCase;
import org.junit.Before;

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
