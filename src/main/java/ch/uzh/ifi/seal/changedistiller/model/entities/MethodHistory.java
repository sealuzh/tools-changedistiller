package ch.uzh.ifi.seal.changedistiller.model.entities;

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
        super(method);
    }

    /**
     * Adds method version to this history.
     * 
     * @param version
     *            a method version
     */
    @Override
    public void addVersion(StructureEntityVersion version) {
        if (version.getType().isMethod()) {
            getVersions().add(version);
        }
    }

}
