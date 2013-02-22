package ch.uzh.ifi.seal.changedistiller.distilling.refactoring;

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

import ch.uzh.ifi.seal.changedistiller.ast.ASTHelper;
import ch.uzh.ifi.seal.changedistiller.model.entities.ClassHistory;
import ch.uzh.ifi.seal.changedistiller.model.entities.StructureEntityVersion;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureNode;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.matching.measure.LevenshteinSimilarityCalculator;

/**
 * Helps finding refactorings of classes.
 * 
 * @author Beat Fluri, Giacomo Ghezzi
 * @see AbstractRefactoringHelper
 */
public class ClassRefactoringHelper extends AbstractRefactoringHelper {

    /**
     * Creates a new refactoring helper.
     * 
     * @param classHistory
     *            on which the helper creates new {@link StructureEntityVersion}s
     * @param astHelper
     *            to help the refactoring helper
     */
    public ClassRefactoringHelper(ClassHistory classHistory, ASTHelper<StructureNode> astHelper) {
        super(classHistory, astHelper);
        setThreshold(0.65);
    }
    
    @Override
    public StructureEntityVersion createStructureEntityVersion(StructureNode node) {
        return getASTHelper().createInnerClassInClassHistory(getClassHistory(), node);
    }

    @Override
    public StructureEntityVersion createStructureEntityVersionWithID(StructureNode node, String version) {
        return getASTHelper().createInnerClassInClassHistory(getClassHistory(), node, version);
    }
    
    @Override
    public StructureEntityVersion createStructureEntityVersion(StructureNode node, String newEntityName) {
        StructureEntityVersion clazz = createStructureEntityVersion(node);
        if (!node.getFullyQualifiedName().equals(newEntityName)) {
            clazz.setUniqueName(newEntityName);
            getClassHistory().overrideClassHistory(node.getFullyQualifiedName(), newEntityName);
        }
        return clazz;
    }
    
    @Override
    public StructureEntityVersion createStructureEntityVersionWithID(StructureNode node, String newEntityName, String version) {
        StructureEntityVersion clazz = createStructureEntityVersionWithID(node, version);
        if (!node.getFullyQualifiedName().equals(newEntityName)) {
            clazz.setUniqueName(newEntityName);
            getClassHistory().overrideClassHistory(node.getFullyQualifiedName(), newEntityName);
        }
        return clazz;
    }

    @Override
    public String extractShortName(String fullName) {
        return fullName;
    }

    @Override
    public double similarity(StructureNode left, StructureNode right) {
        return new LevenshteinSimilarityCalculator().calculateSimilarity(left.getName(), right.getName());
    }
}
