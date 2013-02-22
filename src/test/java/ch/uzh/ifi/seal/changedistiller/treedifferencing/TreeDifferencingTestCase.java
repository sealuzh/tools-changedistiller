package ch.uzh.ifi.seal.changedistiller.treedifferencing;

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

import org.junit.Before;

import ch.uzh.ifi.seal.changedistiller.model.classifiers.EntityType;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;

public abstract class TreeDifferencingTestCase {

    protected Node fRootLeft;
    protected Node fRootRight;

    @Before
    public void setup() throws Exception {
        fRootLeft = new Node(JavaEntityType.ROOT_NODE, "method()");
        fRootRight = new Node(JavaEntityType.ROOT_NODE, "method()");
    }

    protected Node addToLeft(EntityType label, String value) {
        return addToNode(fRootLeft, label, value);
    }

    protected Node addToRight(EntityType label, String value) {
        return addToNode(fRootRight, label, value);
    }

    protected Node addToNode(Node root, EntityType label, String value) {
        Node node = new Node(label, value);
        root.add(node);
        return node;
    }

}
