package ch.uzh.ifi.seal.changedistiller.distilling;

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

import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.junit.BeforeClass;

import ch.uzh.ifi.seal.changedistiller.ast.java.Comment;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaDeclarationConverter;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaDistillerTestCase;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaMethodBodyConverter;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.SourceRange;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.model.entities.StructureEntityVersion;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import ch.uzh.ifi.seal.changedistiller.util.CompilationUtils;

public abstract class WhenChangesAreExtracted extends JavaDistillerTestCase {

    protected static JavaDeclarationConverter sDeclarationConverter;
    protected static JavaMethodBodyConverter sMethodBodyConverter;

    @BeforeClass
    public static void initialize() throws Exception {
        sDeclarationConverter = sInjector.getInstance(JavaDeclarationConverter.class);
        sMethodBodyConverter = sInjector.getInstance(JavaMethodBodyConverter.class);
    }

    protected Distiller getDistiller(StructureEntityVersion structureEntity) {
        return sInjector.getInstance(DistillerFactory.class).create(structureEntity);
    }

    public Node convertMethodBody(String methodName, JavaCompilation compilation) {
        AbstractMethodDeclaration method = CompilationUtils.findMethod(compilation.getCompilationUnit(), methodName);
        Node root = new Node(JavaEntityType.METHOD, methodName);
        root.setEntity(new SourceCodeEntity(methodName, JavaEntityType.METHOD, new SourceRange(
                method.declarationSourceStart,
                method.declarationSourceEnd)));
        List<Comment> comments = CompilationUtils.extractComments(compilation);
        sMethodBodyConverter.initialize(root, method, comments, compilation.getScanner());
        method.traverse(sMethodBodyConverter, (ClassScope) null);
        return root;
    }

    public Node convertMethodDeclaration(String methodName, JavaCompilation compilation) {
        AbstractMethodDeclaration method = CompilationUtils.findMethod(compilation.getCompilationUnit(), methodName);
        Node root = new Node(JavaEntityType.METHOD, methodName);
        root.setEntity(new SourceCodeEntity(methodName, JavaEntityType.METHOD, new SourceRange(
                method.declarationSourceStart,
                method.declarationSourceEnd)));
        sDeclarationConverter.initialize(root, compilation.getScanner());
        method.traverse(sDeclarationConverter, (ClassScope) null);
        return root;
    }

    public Node convertFieldDeclaration(String fieldName, JavaCompilation compilation) {
    	FieldDeclaration field = CompilationUtils.findField(compilation.getCompilationUnit(), fieldName);
    	Node root = new Node(JavaEntityType.FIELD, fieldName);
    	root.setEntity(new SourceCodeEntity(fieldName, JavaEntityType.FIELD, new SourceRange(
    			field.declarationSourceStart,
    			field.declarationSourceEnd)));
    	sDeclarationConverter.initialize(root, compilation.getScanner());
    	field.traverse(sDeclarationConverter, (MethodScope) null);
    	return root;
    }
}
