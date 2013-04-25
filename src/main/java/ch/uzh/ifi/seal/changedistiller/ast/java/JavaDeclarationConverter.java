package ch.uzh.ifi.seal.changedistiller.ast.java;

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

import java.util.Stack;

import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.AbstractVariableDeclaration;
import org.eclipse.jdt.internal.compiler.ast.Argument;
import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
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
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.parser.Scanner;
import org.eclipse.jdt.internal.compiler.parser.TerminalTokens;

import ch.uzh.ifi.seal.changedistiller.ast.ASTNodeTypeConverter;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.EntityType;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.SourceRange;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;

import com.google.inject.Inject;

/**
 * Visitor to generate an intermediate tree (general, rooted, labeled, valued tree) out of a field, class, or method
 * declaration.
 * 
 * @author Beat Fluri
 * 
 */
public class JavaDeclarationConverter extends ASTVisitor {

    private static final String COLON_SPACE = ": ";
    private boolean fEmptyJavaDoc;
    private Stack<Node> fNodeStack;
    private boolean fInMethodDeclaration;
    private String fSource;
    private ASTNodeTypeConverter fASTHelper;
    private Scanner fScanner;

    @Inject
    JavaDeclarationConverter(ASTNodeTypeConverter astHelper) {
        fASTHelper = astHelper;
        fNodeStack = new Stack<Node>();
    }

    /**
     * Initializes the declaration converter.
     * 
     * @param root
     *            of the resulting declaration tree
     * @param scanner
     *            of the source file that is traversed
     */
    public void initialize(Node root, Scanner scanner) {
        fScanner = scanner;
        fSource = String.valueOf(scanner.source);
        fNodeStack.clear();
        fNodeStack.push(root);
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
            visitModifiers(node.modifiers);
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
        visitModifiers(fieldDeclaration.modifiers);
    }

    private void visitMethodDeclarationModifiers(AbstractMethodDeclaration methodDeclaration) {
        fScanner.resetTo(methodDeclaration.declarationSourceStart, methodDeclaration.sourceStart());
        visitModifiers(methodDeclaration.modifiers);
    }

    private void visitTypeDeclarationModifiers(TypeDeclaration typeDeclaration) {
        fScanner.resetTo(typeDeclaration.declarationSourceStart, typeDeclaration.sourceStart());
        visitModifiers(typeDeclaration.modifiers);
    }

    // logic partly taken from org.eclipse.jdt.core.dom.ASTConverter
    private void visitModifiers(int modifierMask) {
        push(JavaEntityType.MODIFIERS, "", -1, -1);
        if (modifierMask != 0) {
            Node modifiers = fNodeStack.peek();
            fScanner.tokenizeWhiteSpace = false;
            try {
                int token;
                while ((token = fScanner.getNextToken()) != TerminalTokens.TokenNameEOF) {
                    switch (token) {
                    	case TerminalTokens.TokenNameabstract:
                        case TerminalTokens.TokenNamepublic:
                        case TerminalTokens.TokenNameprotected:
                        case TerminalTokens.TokenNameprivate:
                        case TerminalTokens.TokenNamefinal:
                        case TerminalTokens.TokenNamestatic:
                        case TerminalTokens.TokenNamevolatile:
                        case TerminalTokens.TokenNamestrictfp:
                        case TerminalTokens.TokenNamenative:
                        case TerminalTokens.TokenNamesynchronized:
                        case TerminalTokens.TokenNametransient:
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
        string = getSource(javadoc);
        if (isJavadocEmpty(string)) {
            fEmptyJavaDoc = true;
        } else {
            pushValuedNode(javadoc, string);
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
        StringBuilder tmp = new StringBuilder();
        for (String s : splittedDoc) {
            tmp.append(s);
        }
        
        String result = tmp.toString();
        
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
        visitAbstractMethodDeclaration(methodDeclaration, scope);
        // ignore body, since only declaration is interesting
        return false;
    }

    @Override
    public void endVisit(MethodDeclaration methodDeclaration, ClassScope scope) {
        pop();
    }

    @Override
    public boolean visit(ConstructorDeclaration constructorDeclaration, ClassScope scope) {
        visitAbstractMethodDeclaration(constructorDeclaration, scope);
        // ignore body, since only declaration is interesting
        return false;
    }

    @Override
    public void endVisit(ConstructorDeclaration constructorDeclaration, ClassScope scope) {
        pop();
    }

    private void visitAbstractMethodDeclaration(AbstractMethodDeclaration methodDeclaration, ClassScope scope) {
        if (methodDeclaration.javadoc != null) {
            methodDeclaration.javadoc.traverse(this, scope);
        }
        fInMethodDeclaration = true;
        visitMethodDeclarationModifiers(methodDeclaration);
        visitReturnType(methodDeclaration, scope);
        visitAbstractVariableDeclarations(JavaEntityType.TYPE_PARAMETERS, methodDeclaration.typeParameters());
        visitAbstractVariableDeclarations(JavaEntityType.PARAMETERS, methodDeclaration.arguments);
        fInMethodDeclaration = false;
        visitList(JavaEntityType.THROW, methodDeclaration.thrownExceptions);
    }

    private void visitReturnType(AbstractMethodDeclaration abstractMethodDeclaration, ClassScope scope) {
        if (abstractMethodDeclaration instanceof MethodDeclaration) {
            MethodDeclaration methodDeclaration = (MethodDeclaration) abstractMethodDeclaration;
            if (methodDeclaration.returnType != null) {
                methodDeclaration.returnType.traverse(this, scope);
            }
        }
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
        int start = type.sourceStart();
        int end = findSourceEndTypeReference(type, type.typeArguments);
        pushValuedNode(type, prefixWithNameOfParrentIfInMethodDeclaration() + getSource(start, end));
        fNodeStack.peek().getEntity().setEndPosition(end);
        return false;
    }

    private String getSource(ASTNode node) {
        return getSource(node.sourceStart(), node.sourceEnd());
    }

    private String getSource(int start, int end) {
        return fSource.substring(start, end + 1);
    }

    private String prefixWithNameOfParrentIfInMethodDeclaration() {
        return fInMethodDeclaration ? getCurrentParent().getValue() + COLON_SPACE : "";
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
        pushValuedNode(type, getSource(type));
        adjustEndPositionOfParameterizedType(type);
        return false;
    }

    private void adjustEndPositionOfParameterizedType(ParameterizedQualifiedTypeReference type) {
        if (hasTypeParameter(type)) {
            visitList(JavaEntityType.TYPE_PARAMETERS, type.typeArguments[type.typeArguments.length - 1]);
            fNodeStack.peek().getEntity().setEndPosition(getLastChildOfCurrentNode().getEntity().getEndPosition() + 1);
        }
    }

    private boolean hasTypeParameter(ParameterizedQualifiedTypeReference type) {
        return type.typeArguments[type.typeArguments.length - 1] != null;
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
        pushValuedNode(type, prefixWithNameOfParrentIfInMethodDeclaration() + type.toString());
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
    public boolean visit(ArrayTypeReference arrayType, ClassScope scope) {
    	return visit(arrayType, (BlockScope) null);
    }
    
    @Override
    public void endVisit(ArrayTypeReference arrayType, ClassScope scope) {
    	endVisit(arrayType, (BlockScope) null);
    }

    @Override
    public boolean visit(SingleTypeReference type, BlockScope scope) {
        pushValuedNode(type, prefixWithNameOfParrentIfInMethodDeclaration() + String.valueOf(type.token));
        return false;
    }

    @Override
    public void endVisit(SingleTypeReference type, BlockScope scope) {
        pop();
    }

    @Override
	public boolean visit(ArrayTypeReference arrayType, BlockScope scope) {
    	pushValuedNode(arrayType, prefixWithNameOfParrentIfInMethodDeclaration() + String.valueOf(arrayType.token));
		return false;
	}
    
    @Override
    public void endVisit(ArrayTypeReference arrayType, BlockScope scope) {
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
        visitTypeDeclarationModifiers(typeDeclaration);
        visitAbstractVariableDeclarations(JavaEntityType.TYPE_PARAMETERS, typeDeclaration.typeParameters);
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
        push(
                fASTHelper.convertNode(typeParameter),
                getSource(typeParameter.sourceStart(), typeParameter.declarationSourceEnd),
                typeParameter.sourceStart(),
                typeParameter.declarationSourceEnd);
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
        if (isNotEmpty(declarations)) {
            start = declarations[0].declarationSourceStart;
            end = declarations[declarations.length - 1].declarationSourceEnd;
            visitList(declarations);
        }
        adjustSourceRangeOfCurrentNode(start, end);
        pop();
    }

    private void adjustSourceRangeOfCurrentNode(int start, int end) {
        fNodeStack.peek().getEntity().setStartPosition(start);
        fNodeStack.peek().getEntity().setEndPosition(end);
    }

    private void visitList(JavaEntityType parentLabel, ASTNode[] nodes) {
        int start = -1;
        int end = -1;
        push(parentLabel, "", start, end);
        if (isNotEmpty(nodes)) {
            start = nodes[0].sourceStart();
            visitList(nodes);
            end = getLastChildOfCurrentNode().getEntity().getEndPosition();
        }
        adjustSourceRangeOfCurrentNode(start, end);
        pop();
    }

    private boolean isNotEmpty(ASTNode[] nodes) {
        return (nodes != null) && (nodes.length > 0);
    }

    // recursive method that finds the end position of a type reference with type parameters, e.g.,
    // Foo<T>.List<Bar<T>>
    private int findSourceEndTypeReference(TypeReference type, TypeReference[] typeParameters) {
        int end = type.sourceEnd();
        if (isNotEmpty(typeParameters)) {
            TypeReference lastNode = typeParameters[typeParameters.length - 1];
            if (lastNode instanceof ParameterizedQualifiedTypeReference) {
                TypeReference[][] typeArguments = ((ParameterizedQualifiedTypeReference) lastNode).typeArguments;
                end = findSourceEndTypeReference(lastNode, typeArguments[typeArguments.length - 1]);
            } else if (lastNode instanceof ParameterizedSingleTypeReference) {
                TypeReference[] typeArguments = ((ParameterizedSingleTypeReference) lastNode).typeArguments;
                end = findSourceEndTypeReference(lastNode, typeArguments);
            } else {
                end = typeParameters[typeParameters.length - 1].sourceEnd();
            }
            if (end == -1) {
                end = lastNode.sourceEnd();
            }
            end++; // increment end position to the the last '>'
        }
        return end;
    }

    private Node getLastChildOfCurrentNode() {
        return (Node) fNodeStack.peek().getLastChild();
    }

    private void pushValuedNode(ASTNode node, String value) {
        push(fASTHelper.convertNode(node), value, node.sourceStart(), node.sourceEnd());
    }

    private void push(EntityType label, String value, int start, int end) {
        Node n = new Node(label, value.trim());
        n.setEntity(new SourceCodeEntity(value.trim(), label, new SourceRange(start, end)));
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
