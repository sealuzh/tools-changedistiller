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

import ch.uzh.ifi.seal.changedistiller.distilling.Distiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureDiffNode;

/**
 * A refactoring candidate is a container class that stores a {@link SourceCodeChange} with a {@link StructureDiffNode}.
 * 
 * <p>
 * {@link Distiller} decides according to the {@link StructureDiffNode} whether the corresponding
 * {@link SourceCodeChange} may be refactoring candidate. That is, when the change is either a attribute/class/method
 * insert or delete.
 * 
 * <p>
 * Concrete refactoring helper use these candidate to find related insert and delete operations of attributes, classes,
 * or methods.
 * 
 * @author fluri
 * @see Distiller
 * @see AbstractRefactoringHelper
 */
public final class RefactoringCandidate {

    private SourceCodeChange fChangeOperation;
    private StructureDiffNode fDiffNode;
    private boolean fFound;

    /**
     * Creates a new refactoring helper.
     * 
     * @param sourceCodeChange
     *            that may be related to a refactoring
     * @param diffNode
     *            that eclipse compare detected
     */
    public RefactoringCandidate(SourceCodeChange sourceCodeChange, StructureDiffNode diffNode) {
        setSourceCodeChange(sourceCodeChange);
        setDiffNode(diffNode);
    }

    /**
     * Set that candidate is a refactoring.
     */
    public void enableRefactoring() {
        fFound = true;
    }

    public StructureDiffNode getDiffNode() {
        return fDiffNode;
    }

    public SourceCodeChange getSourceCodeChange() {
        return fChangeOperation;
    }

    public boolean isRefactoring() {
        return fFound;
    }

    private void setDiffNode(StructureDiffNode diffNode) {
        fDiffNode = diffNode;
    }

    private void setSourceCodeChange(SourceCodeChange change) {
        fChangeOperation = change;
    }
}
