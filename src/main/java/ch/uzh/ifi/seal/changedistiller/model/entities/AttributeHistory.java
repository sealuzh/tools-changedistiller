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
 * Attribute histories store change data about the history of an attribute.
 * <p>
 * Label:
 * 
 * <pre>
 * AttributeHistory:&lt;attributeName&gt;:&lt;attributeType&gt;
 * </pre>
 * <p>
 * Unique name:
 * 
 * <pre>
 * &lt;fullyQualifiedClassName&gt;.&lt;attributeName&gt; : &lt;attributeType&gt;
 * </pre>
 * 
 * , e.g. <code>org.foo.Bar.imba : String</code>.
 * 
 * @author Beat Fluri
 * @author zubi
 * @see AbstractHistory
 * @see ClassHistory
 */
public class AttributeHistory extends AbstractHistory {

    /**
     * Default constructor, used by Hibernate.
     */
    AttributeHistory() {}

    /**
     * Creates a new attribute history.
     * 
     * @param attribute
     *            the attribute that is added to this history
     */
    public AttributeHistory(StructureEntityVersion attribute) {
    	addVersion(attribute);
    }

    /**
     * Adds attribute version to this history.
     * 
     * @param version
     *            a attribute version
     */
    @Override
    public final void addVersion(StructureEntityVersion version) {
        if (version.getType().isField()) {
            getVersions().add(version);
        }
    }
}
