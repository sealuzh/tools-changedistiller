package org.evolizer.changedistiller.distilling;

import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.evolizer.changedistiller.distilling.java.JavaDeclarationConverter;
import org.evolizer.changedistiller.distilling.java.JavaDistillerTestCase;
import org.evolizer.changedistiller.distilling.java.JavaMethodBodyConverter;
import org.evolizer.changedistiller.model.classifiers.SourceRange;
import org.evolizer.changedistiller.model.classifiers.java.JavaEntityType;
import org.evolizer.changedistiller.model.entities.SourceCodeEntity;
import org.evolizer.changedistiller.model.entities.StructureEntityVersion;
import org.evolizer.changedistiller.treedifferencing.Node;
import org.evolizer.changedistiller.util.Compilation;
import org.evolizer.changedistiller.util.CompilationUtils;
import org.junit.BeforeClass;

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

    protected static interface DistillerFactory {

        Distiller create(StructureEntityVersion structureEntity);
    }

    public Node convertMethodBody(String methodName, Compilation compilation) {
        AbstractMethodDeclaration method = CompilationUtils.findMethod(compilation.getCompilationUnit(), methodName);
        Node root = new Node(JavaEntityType.METHOD, methodName);
        root.setEntity(new SourceCodeEntity(methodName, JavaEntityType.METHOD, new SourceRange(
                method.declarationSourceStart,
                method.declarationSourceEnd)));
        sMethodBodyConverter.initialize(root, method, null, compilation.getScanner());
        method.traverse(sMethodBodyConverter, (ClassScope) null);
        return root;
    }

    public Node convertMethodDeclaration(String methodName, Compilation compilation) {
        AbstractMethodDeclaration method = CompilationUtils.findMethod(compilation.getCompilationUnit(), methodName);
        Node root = new Node(JavaEntityType.METHOD, methodName);
        root.setEntity(new SourceCodeEntity(methodName, JavaEntityType.METHOD, new SourceRange(
                method.declarationSourceStart,
                method.declarationSourceEnd)));
        sDeclarationConverter.initialize(root, compilation.getScanner());
        method.traverse(sDeclarationConverter, (ClassScope) null);
        return root;
    }

}
