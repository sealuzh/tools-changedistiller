package org.evolizer.changedistiller.model.entities;

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
        super(attribute);
    }

    /**
     * Adds attribute version to this history.
     * 
     * @param version
     *            a attribute version
     */
    @Override
    public void addVersion(StructureEntityVersion version) {
        if (version.getType().isField()) {
            getVersions().add(version);
        }
    }

}
