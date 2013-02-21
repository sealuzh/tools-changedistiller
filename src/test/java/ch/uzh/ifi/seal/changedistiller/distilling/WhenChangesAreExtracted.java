package ch.uzh.ifi.seal.changedistiller.distilling;

import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.junit.BeforeClass;

import ch.uzh.ifi.seal.changedistiller.ast.java.Comment;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaDeclarationConverter;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaDistillerTestCase;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaMethodBodyConverter;
import ch.uzh.ifi.seal.changedistiller.distilling.Distiller;
import ch.uzh.ifi.seal.changedistiller.distilling.DistillerFactory;
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

}
