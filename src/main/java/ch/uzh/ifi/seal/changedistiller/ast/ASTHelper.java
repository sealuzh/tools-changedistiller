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
import ch.uzh.ifi.seal.changedistiller.model.entities.AttributeHistory;
import ch.uzh.ifi.seal.changedistiller.model.entities.ClassHistory;
import ch.uzh.ifi.seal.changedistiller.model.entities.MethodHistory;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.model.entities.StructureEntityVersion;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureNode;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;

/**
 * Handles language specific ASTs and provides access to their content. An AST helper is associated to a file.
 * 
 * @param <T>
 *            subtype of {@link StructureNode} with which the AST helper works
 * 
 * @author Beat Fluri
 * @author Giacomo Ghezzi
 */
public interface ASTHelper<T extends StructureNode> {

    /**
     * Creates and returns a {@link StructureNode} tree of the associated file.
     * 
     * @return the structure node tree of the associated file
     */
    T createStructureTree();

    /**
     * Creates and returns the declaration {@link Node} tree for the {@link StructureNode}.
     * 
     * @param node
     *            to create the declaration tree
     * @return the declaration tree for the structure node
     */
    Node createDeclarationTree(T node);

    /**
     * Creates and returns the method body {@link Node} tree for the {@link StructureNode}.
     * 
     * @param node
     *            to create the method body tree
     * @return the method body tree for the structure node
     */
    Node createMethodBodyTree(T node);

    /**
     * Converts and returns the type of the AST entity that is associated to the {@link StructureNode} to an
     * {@link EntityType}.
     * 
     * @param node
     *            to convert its type
     * @return the entity type of the AST entity associated to the structure node
     */
    EntityType convertType(T node);

    /**
     * Creates and returns the {@link SourceCodeEntity} for the {@link StructureNode}.
     * 
     * @param node
     *            to create the source code entity for
     * @return the source code entity for the node
     */
    SourceCodeEntity createSourceCodeEntity(T node);

    /**
     * Creates and returns the {@link StructureEntityVersion} for the {@link StructureNode}.
     * 
     * @param node
     *            to create the structure entity version for
     * @param version
     *            the number or id of the version to create
     * @return the structure entity version for the node
     */
    StructureEntityVersion createStructureEntityVersion(T node, String version);
    /**
     * Creates and returns the {@link StructureEntityVersion} for the {@link StructureNode}.
     * 
     * @param node
     *            to create the structure entity version for
     * @return the structure entity version for the node
     */
    StructureEntityVersion createStructureEntityVersion(T node);

    /**
     * Creates and returns the {@link StructureEntityVersion} as method of the {@link ClassHistory}. The method is
     * attached to the corresponding {@link MethodHistory}, if exists. Otherwise a new one is created.
     * 
     * @param classHistory
     *            to create the structure entity version for
     * @param node
     *            to create the structure entity version
     * @param version
     *            the number or id of the version to create
     * @return the method structure entity version in the class history for the node
     */
    StructureEntityVersion createMethodInClassHistory(ClassHistory classHistory, T node, String version);

    /**
     * Creates and returns the {@link StructureEntityVersion} as method of the {@link ClassHistory}. The method is
     * attached to the corresponding {@link MethodHistory}, if exists. Otherwise a new one is created.
     * 
     * @param classHistory
     *            to create the structure entity version for
     * @param node
     *            to create the structure entity version
     * @return the method structure entity version in the class history for the node
     */
    StructureEntityVersion createMethodInClassHistory(ClassHistory classHistory, T node);
    
    /**
     * Creates and returns the {@link StructureEntityVersion} as field of the {@link ClassHistory}. The field is
     * attached to the corresponding {@link AttributeHistory}, if exists. Otherwise a new one is created.
     * 
     * @param classHistory
     *            to create the structure entity version for
     * @param node
     *            to create the structure entity version
     * @param version
     *            the number or id of the version to create
     * @return the field structure entity version in the class history for the node
     */
    StructureEntityVersion createFieldInClassHistory(ClassHistory classHistory, T node, String version);

    /**
     * Creates and returns the {@link StructureEntityVersion} as field of the {@link ClassHistory}. The field is
     * attached to the corresponding {@link AttributeHistory}, if exists. Otherwise a new one is created.
     * 
     * @param classHistory
     *            to create the structure entity version for
     * @param node
     *            to create the structure entity version
     * @return the field structure entity version in the class history for the node
     */
    StructureEntityVersion createFieldInClassHistory(ClassHistory classHistory, T node);
    
    /**
     * Creates and returns the {@link StructureEntityVersion} as class of the {@link ClassHistory}. An inner class
     * version is added to this resulting Inner{@link ClassHistory}.
     * 
     * @param classHistory
     *            to create the structure entity version for
     * @param node
     *            to create the structure entity version
     * @param version
     *            the number or id of the version to create
     * @return the class structure entity version in the class history for the node
     */
    StructureEntityVersion createInnerClassInClassHistory(ClassHistory classHistory, T node, String version);

    /**
     * Creates and returns the {@link StructureEntityVersion} as class of the {@link ClassHistory}. An inner class
     * version is added to this resulting Inner{@link ClassHistory}.
     * 
     * @param classHistory
     *            to create the structure entity version for
     * @param node
     *            to create the structure entity version
     * @return the class structure entity version in the class history for the node
     */
    StructureEntityVersion createInnerClassInClassHistory(ClassHistory classHistory, T node);
    
    /**
     * Creates and returns the declaration {@link Node} tree for the {@link StructureNode}. In addition it replaces the
     * qualified name with the given one.
     * 
     * @param node
     *            to create the declaration tree
     * @param qualifiedName
     *            to use for the tree generation
     * @return the declaration tree for the structure node
     */
    Node createDeclarationTree(T node, String qualifiedName);

}
