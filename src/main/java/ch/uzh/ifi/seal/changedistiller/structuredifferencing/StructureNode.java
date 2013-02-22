package ch.uzh.ifi.seal.changedistiller.structuredifferencing;

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

import java.util.List;

/**
 * Node for structure differencing.
 * 
 * @author Beat Fluri
 * @see ch.uzh.ifi.seal.changedistiller.structuredifferencing.WhenStructureDifferencesAreExtracted
 */
public interface StructureNode {

    /**
     * Returns the children of this structure node.
     * 
     * @return the children of the node
     */
    List<? extends StructureNode> getChildren();

    /**
     * Returns the content of this structure node.
     * 
     * @return the content of the node
     */
    String getContent();

    /**
     * Returns the name of this structure node.
     * 
     * @return the name of the node
     */
    String getName();

    /**
     * Retruns the fully qualified name of this structure node.
     * 
     * @return the fully qualified name of the node
     */
    String getFullyQualifiedName();

    /**
     * Returns whether or not the node is a class or interface.
     * 
     * @return <code>true</code> if the node is a class or interface, <code>false</code> otherwise
     */
    boolean isClassOrInterface();

    /**
     * Returns whether of not the node is a method or constructor.
     * 
     * @return <code>true</code> if the node is a method or constructor, <code>false</code> otherwise
     */
    boolean isMethodOrConstructor();

    /**
     * Returns whether of not the node is a field.
     * 
     * @return <code>true</code> if the node is a field, <code>false</code> otherwise
     */
    boolean isField();

    /**
     * Returns whether or not the node is of same type as the other .
     * 
     * @param other
     *            to check the type with
     * @return <code>true</code> if the node is of same type as the other, <code>false</code> otherwise
     */
    boolean isOfSameTypeAs(StructureNode other);

}
