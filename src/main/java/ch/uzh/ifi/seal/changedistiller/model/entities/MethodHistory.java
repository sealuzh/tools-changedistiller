package ch.uzh.ifi.seal.changedistiller.model.entities;

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
 * Method histories store change data about the history of an method.
 * <p>
 * Unique name:
 * 
 * <pre>
 * &lt;fullyQualifiedClassName&gt;.&lt;methodName&gt;(&lt;listOfParamTypes&gt;)
 * </pre>
 * 
 * , e.g. <code>org.foo.Bar.doIt(String, int)</code>.
 * <p>
 * Label:
 * 
 * <pre>
 * MethodHistory:&lt;methodSignature&gt;
 * </pre>
 * 
 * @author Beat Fluri
 * @author zubi
 * @see AbstractHistory
 * @see ClassHistory
 * @see AttributeHistory
 */
public class MethodHistory extends AbstractHistory {

    MethodHistory() {}

    /**
     * Instantiates a new method history.
     * 
     * @param method
     *            the method that is added to this history
     */
    public MethodHistory(StructureEntityVersion method) {
        addVersion(method);
    }

    /**
     * Adds method version to this history.
     * 
     * @param version
     *            a method version
     */
    @Override
    public final void addVersion(StructureEntityVersion version) {
        if (version.getType().isMethod()) {
            getVersions().add(version);
        }
    }

}
