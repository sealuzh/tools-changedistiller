package ch.uzh.ifi.seal.changedistiller.distilling;

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

import ch.uzh.ifi.seal.changedistiller.model.classifiers.ChangeType;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;

/**
 * Classifies {@link SourceCodeChange}s into {@link ChangeType}s
 * 
 * @author Beat Fluri
 */
public interface SourceCodeChangeClassifier {

    /**
     * Classifies (according to the taxonomy of source code changes) and returns a {@link List} of
     * {@link SourceCodeChange}s by giving each change a {@link ChangeType}.
     * 
     * @param sourceCodeChanges
     *            to classify
     * @return the list of classified source code changes
     */
    List<SourceCodeChange> classifySourceCodeChanges(List<? extends SourceCodeChange> sourceCodeChanges);

}
