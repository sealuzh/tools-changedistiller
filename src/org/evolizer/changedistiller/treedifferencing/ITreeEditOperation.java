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
package org.evolizer.changedistiller.treedifferencing;

/**
 * Interface for basic tree edit operations.
 * 
 * @author fluri
 * @see Node
 */
public interface ITreeEditOperation {

    /**
     * Type code for an insert operation.
     */
    int INSERT = 8;

    /**
     * Type code for a delete operation.
     */
    int DELETE = 16;

    /**
     * Type code for an update operation.
     */
    int UPDATE = 32;

    /**
     * Type code for a move operation.
     */
    int MOVE = 64;

    /**
     * Applies the tree edit operation on the {@link Node} that is involved.
     */
    void apply();

    /**
     * Returns the type code of the tree edit operation.
     * 
     * @return the type code of the tree edit operation
     */
    int getOperationType();
}
