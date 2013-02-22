package ch.uzh.ifi.seal.changedistiller.treedifferencing.matching;

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
