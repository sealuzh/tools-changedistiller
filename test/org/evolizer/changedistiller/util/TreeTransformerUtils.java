package org.evolizer.changedistiller.util;

import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.evolizer.changedistiller.distilling.java.JavaASTHelper;
import org.evolizer.changedistiller.distilling.java.JavaDeclarationConverter;
import org.evolizer.changedistiller.distilling.java.JavaMethodBodyConverter;
import org.evolizer.changedistiller.model.classifiers.SourceRange;
import org.evolizer.changedistiller.model.classifiers.java.JavaEntityType;
import org.evolizer.changedistiller.model.entities.SourceCodeEntity;
import org.evolizer.changedistiller.treedifferencing.Node;

public final class TreeTransformerUtils {

    public static Node convertMethodBody(String methodName, Compilation compilation) {
        AbstractMethodDeclaration method = CompilationUtils.findMethod(compilation.getCompilationUnit(), methodName);
        Node root = new Node(JavaEntityType.METHOD, methodName);
        root.setEntity(new SourceCodeEntity(methodName, JavaEntityType.METHOD, new SourceRange(
                method.declarationSourceStart,
                method.declarationSourceEnd)));
        JavaMethodBodyConverter bodyT =
                new JavaMethodBodyConverter(root, method, null, compilation.getScanner(), new JavaASTHelper());
        method.traverse(bodyT, (ClassScope) null);
        return root;
    }

    public static Node convertMethodDeclaration(String methodName, Compilation compilation) {
        AbstractMethodDeclaration method = CompilationUtils.findMethod(compilation.getCompilationUnit(), methodName);
        Node root = new Node(JavaEntityType.METHOD, methodName);
        root.setEntity(new SourceCodeEntity(methodName, JavaEntityType.METHOD, new SourceRange(
                method.declarationSourceStart,
                method.declarationSourceEnd)));
        JavaDeclarationConverter bodyT =
                new JavaDeclarationConverter(root, compilation.getScanner(), new JavaASTHelper());
        method.traverse(bodyT, (ClassScope) null);
        return root;
    }

}
