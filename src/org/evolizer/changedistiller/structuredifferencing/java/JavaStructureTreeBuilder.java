package org.evolizer.changedistiller.structuredifferencing.java;

import java.util.Stack;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.evolizer.changedistiller.structuredifferencing.java.JavaStructureNode.Type;

/**
 * Creates a tree of {@link JavaStructureNode}s.
 * 
 * @author Beat Fluri
 */
public class JavaStructureTreeBuilder extends ASTVisitor {

    private Stack<JavaStructureNode> fNodeStack;

    /**
     * Creates a new Java structure tree builder.
     * 
     * @param root
     *            of the structure tree
     */
    public JavaStructureTreeBuilder(JavaStructureNode root) {
        fNodeStack = new Stack<JavaStructureNode>();
        fNodeStack.push(root);
    }

    @Override
    public boolean visit(FieldDeclaration fieldDeclaration, MethodScope scope) {
        StringBuffer name = new StringBuffer();
        name.append(fieldDeclaration.name);
        name.append(" : ");
        fieldDeclaration.type.print(0, name);
        push(Type.FIELD, name.toString(), fieldDeclaration);
        return false;
    }

    @Override
    public void endVisit(FieldDeclaration fieldDeclaration, MethodScope scope) {
        pop();
    }

    @Override
    public boolean visit(ConstructorDeclaration constructorDeclaration, ClassScope scope) {
        push(Type.CONSTRUCTOR, getMethodSignature(constructorDeclaration), constructorDeclaration);
        return false;
    }

    @Override
    public void endVisit(ConstructorDeclaration constructorDeclaration, ClassScope scope) {
        pop();
    }

    @Override
    public boolean visit(MethodDeclaration methodDeclaration, ClassScope scope) {
        push(Type.METHOD, getMethodSignature(methodDeclaration), methodDeclaration);
        return false;
    }

    @Override
    public void endVisit(MethodDeclaration methodDeclaration, ClassScope scope) {
        pop();
    }

    @Override
    public boolean visit(TypeDeclaration localTypeDeclaration, BlockScope scope) {
        return visit(localTypeDeclaration, (CompilationUnitScope) null);
    }

    @Override
    public void endVisit(TypeDeclaration localTypeDeclaration, BlockScope scope) {
        endVisit(localTypeDeclaration, (CompilationUnitScope) null);
    }

    @Override
    public boolean visit(TypeDeclaration memberTypeDeclaration, ClassScope scope) {
        return visit(memberTypeDeclaration, (CompilationUnitScope) null);
    }

    @Override
    public void endVisit(TypeDeclaration memberTypeDeclaration, ClassScope scope) {
        endVisit(memberTypeDeclaration, (CompilationUnitScope) null);
    }

    @Override
    public boolean visit(TypeDeclaration typeDeclaration, CompilationUnitScope scope) {
        int kind = TypeDeclaration.kind(typeDeclaration.modifiers);
        Type type = null;
        switch (kind) {
            case TypeDeclaration.INTERFACE_DECL:
                type = Type.INTERFACE;
                break;
            case TypeDeclaration.CLASS_DECL:
                type = Type.CLASS;
                break;
            case TypeDeclaration.ANNOTATION_TYPE_DECL:
                type = Type.ANNOTATION;
                break;
            case TypeDeclaration.ENUM_DECL:
                type = Type.ENUM;
                break;
            default:
                assert (false);
        }
        push(type, String.valueOf(typeDeclaration.name), typeDeclaration);
        return true;
    }

    @Override
    public void endVisit(TypeDeclaration typeDeclaration, CompilationUnitScope scope) {
        pop();
    }

    private String getMethodSignature(AbstractMethodDeclaration methodDeclaration) {
        StringBuffer signature = new StringBuffer();
        signature.append(methodDeclaration.selector);
        signature.append('(');
        if (methodDeclaration.arguments != null) {
            for (int i = 0; i < methodDeclaration.arguments.length; i++) {
                if (i > 0) {
                    signature.append(',');
                }
                methodDeclaration.arguments[i].type.print(0, signature);
            }
        }
        signature.append(')');
        return signature.toString();
    }

    private void push(Type type, String name, ASTNode astNode) {
        JavaStructureNode node = new JavaStructureNode(type, name, astNode);
        fNodeStack.peek().addChild(node);
        fNodeStack.push(node);
    }

    private void pop() {
        fNodeStack.pop();
    }

}
