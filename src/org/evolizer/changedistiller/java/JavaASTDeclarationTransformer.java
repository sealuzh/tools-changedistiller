package org.evolizer.changedistiller.java;

import java.util.Stack;

import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.Block;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Expression;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Javadoc;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.parser.Scanner;
import org.eclipse.jdt.internal.compiler.parser.TerminalTokens;
import org.evolizer.changedistiller.model.classifiers.EntityType;
import org.evolizer.changedistiller.model.classifiers.SourceRange;
import org.evolizer.changedistiller.model.classifiers.java.JavaEntityType;
import org.evolizer.changedistiller.model.entities.SourceCodeEntity;
import org.evolizer.changedistiller.treedifferencing.Node;

/**
 * Visitor to generate an intermediate tree (general, rooted, labeled, valued tree) out of a attribute, class, or method
 * declaration.
 * 
 * @author fluri
 * 
 */
public class JavaASTDeclarationTransformer extends ASTVisitor {

    private static final String COLON_SPACE = ": ";
    private boolean fEmptyJavaDoc;
    private Stack<Node> fNodeStack = new Stack<Node>();
    private boolean fInMethodDeclaration;
    private String fSource;
    private ASTHelper fASTHelper;
    private Scanner fScanner;

    /**
     * Creates a new declaration transformer.
     * 
     * @param root
     *            the root node of the tree to generate
     * @param scanner
     *            the scanner with which the AST was created
     * @param astHelper
     *            the helper that helps with conversions for the change history meta model
     */
    public JavaASTDeclarationTransformer(Node root, Scanner scanner, ASTHelper astHelper) {
        fScanner = scanner;
        fSource = String.valueOf(scanner.source);
        fNodeStack.clear();
        fNodeStack.push(root);
        fASTHelper = astHelper;
    }

    @Override
    public boolean visit(Argument argument, ClassScope scope) {
        return visit(argument, (BlockScope) null);
    }

    @Override
    public void endVisit(Argument argument, ClassScope scope) {
        endVisit(argument, (BlockScope) null);
    }

    @Override
    public boolean visit(Argument node, BlockScope scope) {
        boolean isNotParam = getCurrentParent().getLabel() != JavaEntityType.PARAMETERS;
        pushValuedNode(node, String.valueOf(node.name));
        if (isNotParam) {
            visitModifiers(node, node.modifiers);
        }
        node.type.traverse(this, scope);
        return false;
    }

    @Override
    public void endVisit(Argument node, BlockScope scope) {
        pop();
    }

    @Override
    public boolean visit(Block block, BlockScope scope) {
        // skip block as it is not interesting
        return true;
    }

    @Override
    public void endVisit(Block block, BlockScope scope) {
        // do nothing pop is not needed (see visit(Block, BlockScope))
    }

    @Override
    public boolean visit(FieldDeclaration fieldDeclaration, MethodScope scope) {
        if (fieldDeclaration.javadoc != null) {
            fieldDeclaration.javadoc.traverse(this, scope);
        }
        visitFieldDeclarationModifiers(fieldDeclaration);
        fieldDeclaration.type.traverse(this, scope);
        visitExpression(fieldDeclaration.initialization);
        return false;
    }

    @Override
    public void endVisit(FieldDeclaration fieldDeclaration, MethodScope scope) {
        pop();
    }

    private void visitExpression(Expression expression) {
        if (expression != null) {
            push(
                    fASTHelper.convertNode(expression),
                    expression.toString(),
                    expression.sourceStart(),
                    expression.sourceEnd());
            pop();
        }
    }

    private void visitFieldDeclarationModifiers(FieldDeclaration fieldDeclaration) {
        fScanner.resetTo(fieldDeclaration.declarationSourceStart, fieldDeclaration.sourceStart());
        visitModifiers(fieldDeclaration, fieldDeclaration.modifiers);
    }

    private void visitMethodDeclarationModifiers(MethodDeclaration methodDeclaration) {
        fScanner.resetTo(methodDeclaration.declarationSourceStart, methodDeclaration.sourceStart());
        visitModifiers(methodDeclaration, methodDeclaration.modifiers);
    }

    // logic partly taken from org.eclipse.jdt.core.dom.ASTConverter
    private void visitModifiers(ASTNode node, int modifierMask) {
        push(JavaEntityType.MODIFIERS, "", -1, -1);
        if (modifierMask != 0) {
            Node modifiers = fNodeStack.peek();
            fScanner.tokenizeWhiteSpace = false;
            try {
                int token;
                while ((token = fScanner.getNextToken()) != TerminalTokens.TokenNameEOF) {
                    switch (token) {
                        case TerminalTokens.TokenNamepublic:
                        case TerminalTokens.TokenNameprotected:
                        case TerminalTokens.TokenNameprivate:
                        case TerminalTokens.TokenNamefinal:
                            push(
                                    JavaEntityType.MODIFIER,
                                    fScanner.getCurrentTokenString(),
                                    fScanner.getCurrentTokenStartPosition(),
                                    fScanner.getCurrentTokenEndPosition());
                            pop();
                            break;
                        default:
                            break;
                    }
                }
                // CHECKSTYLE:OFF
            } catch (InvalidInputException e) {
                // CHECKSTYLE:ON
                // ignore
            }
            setSourceRange(modifiers);
        }
        pop();
    }

    private void setSourceRange(Node modifiers) {
        SourceCodeEntity firstModifier = ((Node) modifiers.getFirstLeaf()).getEntity();
        SourceCodeEntity lastModifier = ((Node) modifiers.getLastLeaf()).getEntity();
        modifiers.getEntity().setStartPosition(firstModifier.getStartPosition());
        modifiers.getEntity().setEndPosition(lastModifier.getEndPosition());
    }

    @Override
    public boolean visit(Javadoc javadoc, ClassScope scope) {
        return visit(javadoc, (BlockScope) null);
    }

    @Override
    public void endVisit(Javadoc javadoc, ClassScope scope) {
        endVisit(javadoc, (BlockScope) null);
    }

    @Override
    public boolean visit(Javadoc javadoc, BlockScope scope) {
        String string = null;
        string = fSource.substring(javadoc.sourceStart(), javadoc.sourceEnd() + 1);
        if (!isJavadocEmpty(string)) {
            pushValuedNode(javadoc, string);
        } else {
            fEmptyJavaDoc = true;
        }
        return false;
    }

    @Override
    public void endVisit(Javadoc javadoc, BlockScope scope) {
        if (!fEmptyJavaDoc) {
            pop();
        }
        fEmptyJavaDoc = false;
    }

    private boolean isJavadocEmpty(String doc) {
        String[] splittedDoc = doc.split("/\\*+\\s*");
        String result = "";
        for (String s : splittedDoc) {
            result += s;
        }
        try {
            result = result.split("\\s*\\*/")[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            result = result.replace('/', ' ');
        }
        result = result.replace('*', ' ').trim();

        return result.equals("");
    }

    @Override
    public boolean visit(MethodDeclaration methodDeclaration, ClassScope scope) {
        if (methodDeclaration.javadoc != null) {
            methodDeclaration.javadoc.traverse(this, scope);
        }
        fInMethodDeclaration = true;
        visitMethodDeclarationModifiers(methodDeclaration);
        if (methodDeclaration.returnType != null) {
            methodDeclaration.returnType.traverse(this, scope);
        }
        visitAbstractVariableDeclarations(JavaEntityType.TYPE_ARGUMENTS, methodDeclaration.typeParameters());
        visitAbstractVariableDeclarations(JavaEntityType.PARAMETERS, methodDeclaration.arguments);
        visitList(JavaEntityType.THROW, methodDeclaration.thrownExceptions);
        // ignore body, since only declaration is interesting
        return false;
    }

    @Override
    public void endVisit(MethodDeclaration methodDeclaration, ClassScope scope) {
        fInMethodDeclaration = false;
        pop();
    }

    @Override
    public boolean visit(ConstructorDeclaration constructorDeclaration, ClassScope scope) {
        return super.visit(constructorDeclaration, scope);
    }

    @Override
    public void endVisit(ConstructorDeclaration constructorDeclaration, ClassScope scope) {
        super.endVisit(constructorDeclaration, scope);
    }

    @Override
    public boolean visit(ParameterizedSingleTypeReference parameterizedSingleTypeReference, ClassScope scope) {
        return visit(parameterizedSingleTypeReference, (BlockScope) null);
    }

    @Override
    public void endVisit(ParameterizedSingleTypeReference type, ClassScope scope) {
        endVisit(type, (BlockScope) null);
    }

    @Override
    public boolean visit(ParameterizedSingleTypeReference type, BlockScope scope) {
        pushValuedNode(type, String.valueOf(type.token));
        visitList(JavaEntityType.TYPE_ARGUMENTS, type.typeArguments);
        fNodeStack.peek().getEntity().setEndPosition(getLastChildOfCurrentNode().getEntity().getEndPosition() + 1);
        return false;
    }

    @Override
    public void endVisit(ParameterizedSingleTypeReference type, BlockScope scope) {
        pop();
    }

    @Override
    public boolean visit(ParameterizedQualifiedTypeReference type, ClassScope scope) {
        return visit(type, (BlockScope) null);
    }

    @Override
    public void endVisit(ParameterizedQualifiedTypeReference type, ClassScope scope) {
        endVisit(type, (BlockScope) null);
    }

    @Override
    public boolean visit(ParameterizedQualifiedTypeReference type, BlockScope scope) {
        String name = fSource.substring(type.sourceStart(), type.sourceEnd() + 1);
        pushValuedNode(type, name);
        if (type.typeArguments[type.typeArguments.length - 1] != null) {
            visitList(JavaEntityType.TYPE_ARGUMENTS, type.typeArguments[type.typeArguments.length - 1]);
            fNodeStack.peek().getEntity().setEndPosition(getLastChildOfCurrentNode().getEntity().getEndPosition() + 1);
        }
        return false;
    }

    @Override
    public void endVisit(ParameterizedQualifiedTypeReference type, BlockScope scope) {
        pop();
    }

    @Override
    public boolean visit(QualifiedTypeReference type, ClassScope scope) {
        return visit(type, (BlockScope) null);
    }

    @Override
    public void endVisit(QualifiedTypeReference type, ClassScope scope) {
        endVisit(type, (BlockScope) null);
    }

    @Override
    public boolean visit(QualifiedTypeReference type, BlockScope scope) {
        pushValuedNode(type, type.toString());
        return false;
    }

    @Override
    public void endVisit(QualifiedTypeReference type, BlockScope scope) {
        pop();
    }

    @Override
    public boolean visit(SingleTypeReference type, ClassScope scope) {
        return visit(type, (BlockScope) null);
    }

    @Override
    public void endVisit(SingleTypeReference type, ClassScope scope) {
        endVisit(type, (BlockScope) null);
    }

    @Override
    public boolean visit(SingleTypeReference type, BlockScope scope) {
        String vName = "";
        if (fInMethodDeclaration) {
            vName += getCurrentParent().getValue() + COLON_SPACE;
        }
        pushValuedNode(type, vName + String.valueOf(type.token));
        return false;
    }

    @Override
    public void endVisit(SingleTypeReference type, BlockScope scope) {
        pop();
    }

    @Override
    public boolean visit(TypeDeclaration typeDeclaration, ClassScope scope) {
        return visit(typeDeclaration, (BlockScope) null);
    }

    @Override
    public void endVisit(TypeDeclaration typeDeclaration, ClassScope scope) {
        endVisit(typeDeclaration, (BlockScope) null);
    }

    @Override
    public boolean visit(TypeDeclaration typeDeclaration, CompilationUnitScope scope) {
        return visit(typeDeclaration, (BlockScope) null);
    }

    @Override
    public void endVisit(TypeDeclaration typeDeclaration, CompilationUnitScope scope) {
        endVisit(typeDeclaration, (BlockScope) null);
    }

    @Override
    public boolean visit(TypeDeclaration typeDeclaration, BlockScope scope) {
        if (typeDeclaration.javadoc != null) {
            typeDeclaration.javadoc.traverse(this, scope);
        }
        visitModifiers(typeDeclaration, typeDeclaration.modifiers);
        visitAbstractVariableDeclarations(JavaEntityType.TYPE_ARGUMENTS, typeDeclaration.typeParameters);
        if (typeDeclaration.superclass != null) {
            typeDeclaration.superclass.traverse(this, scope);
        }
        visitList(JavaEntityType.SUPER_INTERFACE_TYPES, typeDeclaration.superInterfaces);
        return false;
    }

    @Override
    public void endVisit(TypeDeclaration typeDeclaration, BlockScope scope) {
        pop();
    }

    @Override
    public boolean visit(TypeParameter typeParameter, ClassScope scope) {
        return visit(typeParameter, (BlockScope) null);
    }

    @Override
    public void endVisit(TypeParameter typeParameter, ClassScope scope) {
        endVisit(typeParameter, (BlockScope) null);
    }

    @Override
    public boolean visit(TypeParameter typeParameter, BlockScope scope) {
        pushValuedNode(typeParameter, String.valueOf(typeParameter.name));
        typeParameter.type.traverse(this, scope);
        visitList(typeParameter.bounds);
        return false;
    }

    @Override
    public void endVisit(TypeParameter typeParameter, BlockScope scope) {
        pop();
    }

    @Override
    public boolean visit(Wildcard type, ClassScope scope) {
        return visit(type, (BlockScope) null);
    }

    @Override
    public void endVisit(Wildcard type, ClassScope scope) {
        endVisit(type, (BlockScope) null);
    }

    @Override
    public boolean visit(Wildcard type, BlockScope scope) {
        String bound = "";
        switch (type.kind) {
            case Wildcard.EXTENDS:
                bound = "extends";
                break;
            case Wildcard.SUPER:
                bound = "super";
                break;
            default:
        }
        pushValuedNode(type, bound);
        return true;
    }

    @Override
    public void endVisit(Wildcard type, BlockScope scope) {
        pop();
    }

    private void visitList(ASTNode[] list) {
        for (ASTNode node : list) {
            node.traverse(this, null);
        }
    }

    private void visitAbstractVariableDeclarations(
            JavaEntityType parentLabel,
            AbstractVariableDeclaration[] declarations) {
        int start = -1;
        int end = -1;
        push(parentLabel, "", start, end);
        if ((declarations != null) && (declarations.length > 0)) {
            start = declarations[0].declarationSourceStart;
            end = declarations[declarations.length - 1].declarationSourceEnd;
            visitList(declarations);
        }
        fNodeStack.peek().getEntity().setStartPosition(start);
        fNodeStack.peek().getEntity().setEndPosition(end);
        pop();
    }

    private void visitList(JavaEntityType parentLabel, ASTNode[] nodes) {
        int start = -1;
        int end = -1;
        push(parentLabel, "", start, end);
        if ((nodes != null) && (nodes.length > 0)) {
            start = nodes[0].sourceStart();
            visitList(nodes);
            end = getLastChildOfCurrentNode().getEntity().getEndPosition();
        }
        fNodeStack.peek().getEntity().setStartPosition(start);
        fNodeStack.peek().getEntity().setEndPosition(end);
        pop();
    }

    private Node getLastChildOfCurrentNode() {
        return (Node) fNodeStack.peek().getLastChild();
    }

    private void pushValuedNode(ASTNode node, String value) {
        push(fASTHelper.convertNode(node), value, node.sourceStart(), node.sourceEnd());
    }

    private void push(EntityType label, String value, int start, int end) {
        Node n = new Node(new SourceCodeEntity(value.trim(), label, new SourceRange(start, end)));
        getCurrentParent().add(n);
        fNodeStack.push(n);
    }

    private void pop() {
        fNodeStack.pop();
    }

    private Node getCurrentParent() {
        return fNodeStack.peek();
    }

}
