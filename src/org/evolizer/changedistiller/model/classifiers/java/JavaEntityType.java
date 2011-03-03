package org.evolizer.changedistiller.model.classifiers.java;

import org.evolizer.changedistiller.model.classifiers.EntityType;

/**
 * Implementation of {@link EntityType} for the Java language.
 * 
 * @author Beat Fluri
 */
public enum JavaEntityType implements EntityType {
    ARGUMENTS(false),
    ARRAY_ACCESS(true),
    ARRAY_CREATION(true),
    ARRAY_INITIALIZER(true),
    ARRAY_TYPE(true),
    ASSERT_STATEMENT(true),
    ASSIGNMENT(true),
    FIELD(false),
    BLOCK(false),
    BLOCK_COMMENT(true),
    BODY(false),
    BOOLEAN_LITERAL(true),
    BREAK_STATEMENT(true),
    CAST_EXPRESSION(true),
    CATCH_CLAUSE(true),
    CATCH_CLAUSES(false),
    CHARACTER_LITERAL(true),
    CLASS(false),
    CLASS_INSTANCE_CREATION(true),
    COMPILATION_UNIT(true),
    CONDITIONAL_EXPRESSION(true),
    CONSTRUCTOR_INVOCATION(true),
    CONTINUE_STATEMENT(true),
    DO_STATEMENT(true),
    ELSE_STATEMENT(true),
    EMPTY_STATEMENT(true),
    FOREACH_STATEMENT(true),
    FIELD_ACCESS(true),
    FIELD_DECLARATION(true),
    FINALLY(false),
    FOR_STATEMENT(true),
    IF_STATEMENT(true),
    INFIX_EXPRESSION(true),
    INSTANCEOF_EXPRESSION(true),
    JAVADOC(true),
    LABELED_STATEMENT(true),
    LINE_COMMENT(true),
    METHOD(false),
    METHOD_DECLARATION(true),
    METHOD_INVOCATION(true),
    MODIFIER(true),
    MODIFIERS(false),
    NULL_LITERAL(true),
    NUMBER_LITERAL(true),
    PARAMETERIZED_TYPE(true),
    PARAMETERS(false),
    PARAMETER(true),
    POSTFIX_EXPRESSION(true),
    PREFIX_EXPRESSION(true),
    PRIMITIVE_TYPE(true),
    QUALIFIED_NAME(true),
    QUALIFIED_TYPE(true),
    RETURN_STATEMENT(true),
    ROOT_NODE(true),
    SIMPLE_NAME(true),
    SIMPLE_TYPE(true),
    STRING_LITERAL(true),
    SUPER_INTERFACE_TYPES(false),
    SWITCH_CASE(true),
    SWITCH_STATEMENT(true),
    SYNCHRONIZED_STATEMENT(true),
    THEN_STATEMENT(true),
    THROW(false),
    THROW_STATEMENT(true),
    TRY_STATEMENT(true),
    TYPE_PARAMETERS(false),
    TYPE_DECLARATION(true),
    TYPE_LITERAL(true),
    TYPE_PARAMETER(true),
    VARIABLE_DECLARATION_STATEMENT(true),
    WHILE_STATEMENT(true),
    WILDCARD_TYPE(true);

    private final boolean fIsValidChange;

    private JavaEntityType(boolean isValidChange) {
        fIsValidChange = isValidChange;
    }

    public boolean isValidChange() {
        return fIsValidChange;
    }

}
