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

/**
 * Interface for basic tree edit operations.
 * 
 * @author Beat Fluri
 * @see Node
 */
public interface TreeEditOperation {

    /**
     * Applies the tree edit operation on the {@link Node} that is involved.
     */
    void apply();

    /**
     * Returns the {@link OperationType} of the tree edit operation.
     * 
     * @return the operation type of the tree edit operation
     */
    OperationType getOperationType();

    /**
     * Type of {@link TreeEditOperation}.
     * 
     * @author Beat Fluri
     */
    enum OperationType {
        INSERT,
        DELETE,
        MOVE,
        UPDATE;
    }
}
