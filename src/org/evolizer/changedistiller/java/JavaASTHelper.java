package org.evolizer.changedistiller.java;

import static org.evolizer.changedistiller.model.classifiers.java.JavaEntityType.ASSERT_STATEMENT;
import static org.evolizer.changedistiller.model.classifiers.java.JavaEntityType.ASSIGNMENT;
import static org.evolizer.changedistiller.model.classifiers.java.JavaEntityType.BREAK_STATEMENT;
import static org.evolizer.changedistiller.model.classifiers.java.JavaEntityType.CLASS_INSTANCE_CREATION;
import static org.evolizer.changedistiller.model.classifiers.java.JavaEntityType.CONSTRUCTOR_INVOCATION;
import static org.evolizer.changedistiller.model.classifiers.java.JavaEntityType.CONTINUE_STATEMENT;
import static org.evolizer.changedistiller.model.classifiers.java.JavaEntityType.DO_STATEMENT;
import static org.evolizer.changedistiller.model.classifiers.java.JavaEntityType.EMPTY_STATEMENT;
import static org.evolizer.changedistiller.model.classifiers.java.JavaEntityType.FOREACH_STATEMENT;
import static org.evolizer.changedistiller.model.classifiers.java.JavaEntityType.FOR_STATEMENT;
import static org.evolizer.changedistiller.model.classifiers.java.JavaEntityType.IF_STATEMENT;
import static org.evolizer.changedistiller.model.classifiers.java.JavaEntityType.LABELED_STATEMENT;
import static org.evolizer.changedistiller.model.classifiers.java.JavaEntityType.METHOD_INVOCATION;
import static org.evolizer.changedistiller.model.classifiers.java.JavaEntityType.POSTFIX_EXPRESSION;
import static org.evolizer.changedistiller.model.classifiers.java.JavaEntityType.PREFIX_EXPRESSION;
import static org.evolizer.changedistiller.model.classifiers.java.JavaEntityType.RETURN_STATEMENT;
import static org.evolizer.changedistiller.model.classifiers.java.JavaEntityType.SWITCH_CASE;
import static org.evolizer.changedistiller.model.classifiers.java.JavaEntityType.SWITCH_STATEMENT;
import static org.evolizer.changedistiller.model.classifiers.java.JavaEntityType.SYNCHRONIZED_STATEMENT;
import static org.evolizer.changedistiller.model.classifiers.java.JavaEntityType.THROW_STATEMENT;
import static org.evolizer.changedistiller.model.classifiers.java.JavaEntityType.TRY_STATEMENT;
import static org.evolizer.changedistiller.model.classifiers.java.JavaEntityType.VARIABLE_DECLARATION_STATEMENT;
import static org.evolizer.changedistiller.model.classifiers.java.JavaEntityType.WHILE_STATEMENT;

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
import org.evolizer.changedistiller.model.classifiers.java.JavaEntityType;

/**
 * Implementation of ASTHelper for the Java programming language.
 * 
 * @author Beat Fluri
 */
public final class JavaASTHelper implements ASTHelper {

    private static Map<Class<? extends ASTNode>, JavaEntityType> sConversionMap =
            new HashMap<Class<? extends ASTNode>, JavaEntityType>();

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
