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

import java.io.File;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;

import ch.uzh.ifi.seal.changedistiller.ast.ASTHelper;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.ChangeModifier;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.EntityType;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.SourceRange;
import ch.uzh.ifi.seal.changedistiller.model.entities.AttributeHistory;
import ch.uzh.ifi.seal.changedistiller.model.entities.ClassHistory;
import ch.uzh.ifi.seal.changedistiller.model.entities.MethodHistory;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.model.entities.StructureEntityVersion;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.java.JavaStructureNode;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.java.JavaStructureNode.Type;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.java.JavaStructureTreeBuilder;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * Implementation of {@link ASTHelper} for the Java programming language.
 * 
 * @author Beat Fluri,
 * @author Giacomo Ghezzi
 */
public class JavaASTHelper implements ASTHelper<JavaStructureNode> {

    private JavaDeclarationConverter fDeclarationConverter;
    private JavaMethodBodyConverter fBodyConverter;
    private JavaASTNodeTypeConverter fASTHelper;
    private JavaCompilation fCompilation;
    private List<Comment> fComments;

    @Inject
    JavaASTHelper(
            @Assisted File file,
            @Assisted String javaVersion,
            JavaASTNodeTypeConverter astHelper,
            JavaDeclarationConverter declarationConverter,
            JavaMethodBodyConverter bodyConverter) {
    	long versionNumber;
    	switch (javaVersion) {
    	case "1.1": versionNumber = ClassFileConstants.JDK1_1;break;
    	case "1.2": versionNumber = ClassFileConstants.JDK1_2;break;
    	case "1.3": versionNumber = ClassFileConstants.JDK1_3;break;
    	case "1.4": versionNumber = ClassFileConstants.JDK1_4;break;
    	case "1.5": versionNumber = ClassFileConstants.JDK1_5;break;
    	case "1.6": versionNumber = ClassFileConstants.JDK1_6;break;
    	case "1.7": versionNumber = ClassFileConstants.JDK1_7;break;
    	default: versionNumber = ClassFileConstants.JDK1_6;
    	}
        fCompilation = JavaCompilationUtils.compile(file, versionNumber);
        prepareComments();
        fASTHelper = astHelper;
        fDeclarationConverter = declarationConverter;
        fBodyConverter = bodyConverter;
    }
    
    private void prepareComments() {
        cleanComments(collectComments());
    }

    private void cleanComments(List<Comment> comments) {
        CommentCleaner visitor = new CommentCleaner(fCompilation.getSource());
        for (Comment comment : comments) {
            visitor.process(comment);
        }
        fComments = visitor.getComments();
    }

    private List<Comment> collectComments() {
        CommentCollector collector = new CommentCollector(fCompilation.getCompilationUnit(), fCompilation.getSource());
        collector.collect();
        return collector.getComments();
    }

    @Override
    public Node createDeclarationTree(JavaStructureNode node) {
        ASTNode astNode = node.getASTNode();
        Node root = createRootNode(node, astNode);
        return createDeclarationTree(astNode, root);
    }

    private Node createDeclarationTree(ASTNode astNode, Node root) {
        fDeclarationConverter.initialize(root, fCompilation.getScanner());
        if (astNode instanceof TypeDeclaration) {
            ((TypeDeclaration) astNode).traverse(fDeclarationConverter, (ClassScope) null);
        } else if (astNode instanceof AbstractMethodDeclaration) {
            ((AbstractMethodDeclaration) astNode).traverse(fDeclarationConverter, (ClassScope) null);
        } else if (astNode instanceof FieldDeclaration) {
            ((FieldDeclaration) astNode).traverse(fDeclarationConverter, null);
        }
        return root;
    }

    @Override
    public Node createDeclarationTree(JavaStructureNode node, String qualifiedName) {
        ASTNode astNode = node.getASTNode();
        Node root = createRootNode(node, astNode);
        root.setValue(qualifiedName);
        return createDeclarationTree(astNode, root);
    }

    private Node createRootNode(JavaStructureNode node, ASTNode astNode) {
        Node root = new Node(fASTHelper.convertNode(astNode), node.getFullyQualifiedName());
        root.setEntity(createSourceCodeEntity(node));
        return root;
    }

    @Override
    public Node createMethodBodyTree(JavaStructureNode node) {
        ASTNode astNode = node.getASTNode();
        if (astNode instanceof AbstractMethodDeclaration) {
            Node root = createRootNode(node, astNode);
            fBodyConverter.initialize(root, astNode, fComments, fCompilation.getScanner());
            ((AbstractMethodDeclaration) astNode).traverse(fBodyConverter, (ClassScope) null);
            return root;
        }
        return null;
    }

    @Override
    public JavaStructureNode createStructureTree() {
        CompilationUnitDeclaration cu = fCompilation.getCompilationUnit();
        JavaStructureNode node = new JavaStructureNode(Type.CU, null, null, cu);
        cu.traverse(new JavaStructureTreeBuilder(node), (CompilationUnitScope) null);
        return node;
    }

    @Override
    public EntityType convertType(JavaStructureNode node) {
        return fASTHelper.convertNode(node.getASTNode());
    }

    @Override
    public SourceCodeEntity createSourceCodeEntity(JavaStructureNode node) {
        return new SourceCodeEntity(
                node.getFullyQualifiedName(),
                convertType(node),
                extractModifier(node.getASTNode()),
                createSourceRange(node.getASTNode()));
    }

    private SourceRange createSourceRange(ASTNode astNode) {
        if (astNode instanceof TypeDeclaration) {
            TypeDeclaration type = (TypeDeclaration) astNode;
            return new SourceRange(type.declarationSourceStart, type.declarationSourceEnd);
        }
        if (astNode instanceof AbstractMethodDeclaration) {
            AbstractMethodDeclaration method = (AbstractMethodDeclaration) astNode;
            return new SourceRange(method.declarationSourceStart, method.declarationSourceEnd);
        }
        if (astNode instanceof FieldDeclaration) {
            FieldDeclaration field = (FieldDeclaration) astNode;
            return new SourceRange(field.declarationSourceStart, field.declarationSourceEnd);
        }
        return new SourceRange(astNode.sourceStart(), astNode.sourceEnd());
    }

    @Override
    public StructureEntityVersion createStructureEntityVersion(JavaStructureNode node, String versionNum) {
        return new StructureEntityVersion(
                convertType(node),
                node.getFullyQualifiedName(),
                extractModifier(node.getASTNode()),
                versionNum);
    }
    
    @Override
    public StructureEntityVersion createStructureEntityVersion(JavaStructureNode node) {
        return new StructureEntityVersion(
                convertType(node),
                node.getFullyQualifiedName(),
                extractModifier(node.getASTNode()));
    }

    private int extractModifier(ASTNode node) {
        int ecjModifer = -1;
        if (node instanceof AbstractMethodDeclaration) {
            ecjModifer = ((AbstractMethodDeclaration) node).modifiers;
        } else if (node instanceof FieldDeclaration) {
            ecjModifer = ((FieldDeclaration) node).modifiers;
        } else if (node instanceof TypeDeclaration) {
            ecjModifer = ((TypeDeclaration) node).modifiers;
        }
        if (ecjModifer > -1) {
            return convertECJModifier(ecjModifer);
        }
        return 0;
    }

    private int convertECJModifier(int ecjModifer) {
        int modifier = 0x0;
        if (isAbstract(ecjModifer)) {
            modifier |= ChangeModifier.ABSTRACT;
        }
        if (isFinal(ecjModifer)) {
            modifier |= ChangeModifier.FINAL;
        }
        if (isNative(ecjModifer)) {
        	modifier |= ChangeModifier.NATIVE;
        }
        if (isStatic(ecjModifer)) {
        	modifier |= ChangeModifier.STATIC;
        }
        if (isStrictFP(ecjModifer)) {
        	modifier |= ChangeModifier.STRICTFP;
        }
        if (isSynchronized(ecjModifer)) {
        	modifier |= ChangeModifier.SYNCHRONIZED;
        }
        if (isTransient(ecjModifer)) {
        	modifier |= ChangeModifier.TRANSIENT;
        }
        if (isVolatile(ecjModifer)) {
        	modifier |= ChangeModifier.VOLATILE;
        }
        if (isPublic(ecjModifer)) {
            modifier |= ChangeModifier.PUBLIC;
        }
        if (isProtected(ecjModifer)) {
            modifier |= ChangeModifier.PROTECTED;
        }
        if (isPrivate(ecjModifer)) {
            modifier |= ChangeModifier.PRIVATE;
        }
        return modifier;
    }
    
    private boolean isNative(int ecjModifier) {
    	return (ecjModifier & ClassFileConstants.AccNative) != 0;
    }
    
    private boolean isStatic(int ecjModifier) {
    	return (ecjModifier & ClassFileConstants.AccStatic) != 0;
    }
    
    private boolean isStrictFP(int ecjModifier) {
    	return (ecjModifier & ClassFileConstants.AccStrictfp) != 0;
    }

    private boolean isSynchronized(int ecjModifier) {
    	return (ecjModifier & ClassFileConstants.AccSynchronized) != 0;
    }

    private boolean isTransient(int ecjModifier) {
    	return (ecjModifier & ClassFileConstants.AccTransient) != 0;
    }

    private boolean isVolatile(int ecjModifier) {
    	return (ecjModifier & ClassFileConstants.AccVolatile) != 0;
    }
    

    private boolean isAbstract(int ecjModifier) {
        return (ecjModifier & ClassFileConstants.AccAbstract) != 0;
    }
    
    private boolean isPrivate(int ecjModifier) {
        return (ecjModifier & ClassFileConstants.AccPrivate) != 0;
    }

    private boolean isProtected(int ecjModifier) {
        return (ecjModifier & ClassFileConstants.AccProtected) != 0;
    }

    private boolean isPublic(int ecjModifier) {
        return (ecjModifier & ClassFileConstants.AccPublic) != 0;
    }

    private boolean isFinal(int ecjModifier) {
        return (ecjModifier & ClassFileConstants.AccFinal) != 0;
    }

    @Override
    public StructureEntityVersion createMethodInClassHistory(ClassHistory classHistory, JavaStructureNode node, String versionNum) {
        MethodHistory mh;
        StructureEntityVersion method = createStructureEntityVersion(node, versionNum);
        if (classHistory.getMethodHistories().containsKey(method.getUniqueName())) {
            mh = classHistory.getMethodHistories().get(method.getUniqueName());
            mh.addVersion(method);
        } else {
            mh = new MethodHistory(method);
            classHistory.getMethodHistories().put(method.getUniqueName(), mh);
        }
        return method;
    }

    @Override
    public StructureEntityVersion createMethodInClassHistory(ClassHistory classHistory, JavaStructureNode node) {
        MethodHistory mh;
        StructureEntityVersion method = createStructureEntityVersion(node);
        if (classHistory.getMethodHistories().containsKey(method.getUniqueName())) {
            mh = classHistory.getMethodHistories().get(method.getUniqueName());
            mh.addVersion(method);
        } else {
            mh = new MethodHistory(method);
            classHistory.getMethodHistories().put(method.getUniqueName(), mh);
        }
        return method;
    }
    
    @Override
    public StructureEntityVersion createFieldInClassHistory(ClassHistory classHistory, JavaStructureNode node, String versionNum) {
        AttributeHistory ah = null;
        StructureEntityVersion attribute = createStructureEntityVersion(node, versionNum);
        if (classHistory.getAttributeHistories().containsKey(attribute.getUniqueName())) {
            ah = classHistory.getAttributeHistories().get(attribute.getUniqueName());
            ah.addVersion(attribute);
        } else {
            ah = new AttributeHistory(attribute);
            classHistory.getAttributeHistories().put(attribute.getUniqueName(), ah);
        }
        return attribute;

    }
    
    @Override
    public StructureEntityVersion createFieldInClassHistory(ClassHistory classHistory, JavaStructureNode node) {
        AttributeHistory ah = null;
        StructureEntityVersion attribute = createStructureEntityVersion(node);
        if (classHistory.getAttributeHistories().containsKey(attribute.getUniqueName())) {
            ah = classHistory.getAttributeHistories().get(attribute.getUniqueName());
            ah.addVersion(attribute);
        } else {
            ah = new AttributeHistory(attribute);
            classHistory.getAttributeHistories().put(attribute.getUniqueName(), ah);
        }
        return attribute;

    }

    @Override
    public StructureEntityVersion createInnerClassInClassHistory(ClassHistory classHistory, JavaStructureNode node, String versionNum) {
        ClassHistory ch = null;
        StructureEntityVersion clazz = createStructureEntityVersion(node, versionNum);
        if (classHistory.getInnerClassHistories().containsKey(clazz.getUniqueName())) {
            ch = classHistory.getInnerClassHistories().get(clazz.getUniqueName());
            ch.addVersion(clazz);
        } else {
            ch = new ClassHistory(clazz);
            classHistory.getInnerClassHistories().put(clazz.getUniqueName(), ch);
        }
        return clazz;

    }
    
    @Override
    public StructureEntityVersion createInnerClassInClassHistory(ClassHistory classHistory, JavaStructureNode node) {
        ClassHistory ch = null;
        StructureEntityVersion clazz = createStructureEntityVersion(node);
        if (classHistory.getInnerClassHistories().containsKey(clazz.getUniqueName())) {
            ch = classHistory.getInnerClassHistories().get(clazz.getUniqueName());
            ch.addVersion(clazz);
        } else {
            ch = new ClassHistory(clazz);
            classHistory.getInnerClassHistories().put(clazz.getUniqueName(), ch);
        }
        return clazz;

    }

}
