package org.evolizer.changedistiller.java;

import static org.evolizer.changedistiller.model.classifiers.EntityType.ASSERT_STATEMENT;
import static org.evolizer.changedistiller.model.classifiers.EntityType.ASSIGNMENT;
import static org.evolizer.changedistiller.model.classifiers.EntityType.BREAK_STATEMENT;
import static org.evolizer.changedistiller.model.classifiers.EntityType.CLASS_INSTANCE_CREATION;
import static org.evolizer.changedistiller.model.classifiers.EntityType.CONSTRUCTOR_INVOCATION;
import static org.evolizer.changedistiller.model.classifiers.EntityType.CONTINUE_STATEMENT;
import static org.evolizer.changedistiller.model.classifiers.EntityType.DO_STATEMENT;
import static org.evolizer.changedistiller.model.classifiers.EntityType.EMPTY_STATEMENT;
import static org.evolizer.changedistiller.model.classifiers.EntityType.FOREACH_STATEMENT;
import static org.evolizer.changedistiller.model.classifiers.EntityType.FOR_STATEMENT;
import static org.evolizer.changedistiller.model.classifiers.EntityType.IF_STATEMENT;
import static org.evolizer.changedistiller.model.classifiers.EntityType.LABELED_STATEMENT;
import static org.evolizer.changedistiller.model.classifiers.EntityType.METHOD_INVOCATION;
import static org.evolizer.changedistiller.model.classifiers.EntityType.POSTFIX_EXPRESSION;
import static org.evolizer.changedistiller.model.classifiers.EntityType.PREFIX_EXPRESSION;
import static org.evolizer.changedistiller.model.classifiers.EntityType.RETURN_STATEMENT;
import static org.evolizer.changedistiller.model.classifiers.EntityType.SWITCH_CASE;
import static org.evolizer.changedistiller.model.classifiers.EntityType.SWITCH_STATEMENT;
import static org.evolizer.changedistiller.model.classifiers.EntityType.SYNCHRONIZED_STATEMENT;
import static org.evolizer.changedistiller.model.classifiers.EntityType.THROW_STATEMENT;
import static org.evolizer.changedistiller.model.classifiers.EntityType.TRY_STATEMENT;
import static org.evolizer.changedistiller.model.classifiers.EntityType.VARIABLE_DECLARATION_STATEMENT;
import static org.evolizer.changedistiller.model.classifiers.EntityType.WHILE_STATEMENT;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.AssertStatement;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.BreakStatement;
import org.eclipse.jdt.internal.compiler.ast.CaseStatement;
import org.eclipse.jdt.internal.compiler.ast.CompoundAssignment;
import org.eclipse.jdt.internal.compiler.ast.ContinueStatement;
import org.eclipse.jdt.internal.compiler.ast.DoStatement;
import org.eclipse.jdt.internal.compiler.ast.EmptyStatement;
import org.eclipse.jdt.internal.compiler.ast.ExplicitConstructorCall;
import org.eclipse.jdt.internal.compiler.ast.ForStatement;
import org.eclipse.jdt.internal.compiler.ast.ForeachStatement;
import org.eclipse.jdt.internal.compiler.ast.IfStatement;
import org.eclipse.jdt.internal.compiler.ast.LabeledStatement;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.PostfixExpression;
import org.eclipse.jdt.internal.compiler.ast.PrefixExpression;
import org.eclipse.jdt.internal.compiler.ast.QualifiedAllocationExpression;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.ast.SwitchStatement;
import org.eclipse.jdt.internal.compiler.ast.SynchronizedStatement;
import org.eclipse.jdt.internal.compiler.ast.ThrowStatement;
import org.eclipse.jdt.internal.compiler.ast.TryStatement;
import org.eclipse.jdt.internal.compiler.ast.WhileStatement;
import org.evolizer.changedistiller.model.classifiers.EntityType;

/**
 * Implementation of ASTHelper for the Java programming language.
 * 
 * @author Beat Fluri
 */
public final class JavaASTHelper implements ASTHelper {

    private static Map<Class<? extends ASTNode>, EntityType> sConversionMap =
            new HashMap<Class<? extends ASTNode>, EntityType>();

    static {
        sConversionMap.put(Assignment.class, ASSIGNMENT);
        sConversionMap.put(CompoundAssignment.class, ASSIGNMENT);
        sConversionMap.put(PostfixExpression.class, POSTFIX_EXPRESSION);
        sConversionMap.put(PrefixExpression.class, PREFIX_EXPRESSION);
        sConversionMap.put(AllocationExpression.class, CLASS_INSTANCE_CREATION);
        sConversionMap.put(QualifiedAllocationExpression.class, CLASS_INSTANCE_CREATION);
        sConversionMap.put(AssertStatement.class, ASSERT_STATEMENT);
        sConversionMap.put(BreakStatement.class, BREAK_STATEMENT);
        sConversionMap.put(ExplicitConstructorCall.class, CONSTRUCTOR_INVOCATION);
        sConversionMap.put(ContinueStatement.class, CONTINUE_STATEMENT);
        sConversionMap.put(DoStatement.class, DO_STATEMENT);
        sConversionMap.put(EmptyStatement.class, EMPTY_STATEMENT);
        sConversionMap.put(ForeachStatement.class, FOREACH_STATEMENT);
        sConversionMap.put(ForStatement.class, FOR_STATEMENT);
        sConversionMap.put(IfStatement.class, IF_STATEMENT);
        sConversionMap.put(LabeledStatement.class, LABELED_STATEMENT);
        sConversionMap.put(LocalDeclaration.class, VARIABLE_DECLARATION_STATEMENT);
        sConversionMap.put(MessageSend.class, METHOD_INVOCATION);
        sConversionMap.put(ReturnStatement.class, RETURN_STATEMENT);
        sConversionMap.put(SwitchStatement.class, SWITCH_STATEMENT);
        sConversionMap.put(CaseStatement.class, SWITCH_CASE);
        sConversionMap.put(SynchronizedStatement.class, SYNCHRONIZED_STATEMENT);
        sConversionMap.put(ThrowStatement.class, THROW_STATEMENT);
        sConversionMap.put(TryStatement.class, TRY_STATEMENT);
        sConversionMap.put(WhileStatement.class, WHILE_STATEMENT);
    }

    @Override
    public EntityType convertNode(Object node) {
        if (!(node instanceof ASTNode)) {
            throw new RuntimeException("Node must be of type ASTNode.");
        }
        return sConversionMap.get(node.getClass());
    }
}
