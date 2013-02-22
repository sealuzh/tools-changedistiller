package ch.uzh.ifi.seal.changedistiller.ast;

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

import ch.uzh.ifi.seal.changedistiller.model.classifiers.EntityType;

/**
 * AST node type converters should extend this class to facilitate conversion of AST node types.
 * 
 * @author Beat Fluri
 */
public interface ASTNodeTypeConverter {

    /**
     * Converts and returns the given node into an {@link EntityType}.
     * 
     * @param node
     *            to convert
     * @return entity type of the node
     */
    EntityType convertNode(Object node);

}
