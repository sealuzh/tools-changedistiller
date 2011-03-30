package org.evolizer.changedistiller.model.classifiers.java;

import org.evolizer.changedistiller.model.classifiers.EntityTypeUtils;

/**
 * Implementation of {@link EntityTypeUtils} for the Java language.
 * 
 * @author Beat Fluri
 */
public class JavaEntityTypeUtils implements EntityTypeUtils<JavaEntityType> {

    private static JavaEntityTypeUtils sInstance;

    /**
     * Returns the instance of {@link JavaEntityTypeUtils}.
     * 
     * @return the instance
     */
    public static JavaEntityTypeUtils getInstance() {
        if (sInstance == null) {
            sInstance = new JavaEntityTypeUtils();
        }
        return sInstance;
    }

    @Override
    public int getNumberOfEntityTypes() {
        return JavaEntityType.values().length;
    }

    @Override
    public boolean isType(JavaEntityType type) {
        switch (type) {
            case ARRAY_TYPE:
            case PARAMETERIZED_TYPE:
            case PRIMITIVE_TYPE:
            case QUALIFIED_TYPE:
            case SIMPLE_TYPE:
            case WILDCARD_TYPE:
                return true;
            default:
                return false;
        }
    }

    /**
     * Returns whether the given entity type is a statement or not.
     * 
     * @param type
     *            to analyze
     * @return <code>true</code> if given entity type is a statement, <code>false</code> otherwise.
     */
    public boolean isAtStatementLevel(JavaEntityType type) {
        switch (type) {
            case ASSERT_STATEMENT:
            case ASSIGNMENT:
            case BREAK_STATEMENT:
            case CATCH_CLAUSE:
            case CLASS_INSTANCE_CREATION:
            case CONSTRUCTOR_INVOCATION:
            case CONTINUE_STATEMENT:
            case DO_STATEMENT:
            case FINALLY:
            case FOR_STATEMENT:
            case IF_STATEMENT:
            case LABELED_STATEMENT:
            case METHOD_INVOCATION:
            case RETURN_STATEMENT:
            case SWITCH_CASE:
            case SWITCH_STATEMENT:
            case SYNCHRONIZED_STATEMENT:
            case THROW_STATEMENT:
            case TRY_STATEMENT:
            case VARIABLE_DECLARATION_STATEMENT:
            case WHILE_STATEMENT:
            case FOREACH_STATEMENT:
                return true;
            default:
                return false;
        }
    }

}
