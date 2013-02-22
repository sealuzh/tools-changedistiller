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
package ch.uzh.ifi.seal.changedistiller.model.classifiers;

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

import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;

/**
 * Significance level of an extracted {@link SourceCodeChange}. It depends on its {@link ChangeType} and
 * visibility (i.e. modifiers).
 * <p>
 * To assure a well ordered set of all levels a distinct integer value is assigned to each. Use
 * {@link SignificanceLevel#value()} to compare or sum up levels.
 * 
 * @author zubi
 * @see SourceCodeChange
 * @see ChangeType
 */
public enum SignificanceLevel {
    NONE(0),
    LOW(1),
    MEDIUM(2),
    HIGH(3),
    CRUCIAL(4);

    private final int fValue;

    private SignificanceLevel(int level) {
        fValue = level;
    }

    /**
     * Returns the integer value assigned to this significance level.
     * 
     * @return value of this significance level.
     */
    public int value() {
        return fValue;
    }
}
