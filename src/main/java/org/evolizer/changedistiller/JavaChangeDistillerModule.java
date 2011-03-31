package org.evolizer.changedistiller;

import org.evolizer.changedistiller.ast.ASTHelper;
import org.evolizer.changedistiller.ast.ASTHelperFactory;
import org.evolizer.changedistiller.ast.ASTNodeTypeConverter;
import org.evolizer.changedistiller.ast.java.JavaASTHelper;
import org.evolizer.changedistiller.ast.java.JavaASTNodeTypeConverter;
import org.evolizer.changedistiller.ast.java.JavaSourceCodeChangeClassifier;
import org.evolizer.changedistiller.distilling.DistillerFactory;
import org.evolizer.changedistiller.distilling.SourceCodeChangeClassifier;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * Injection module to configure ChangeDistiller with the Java programming language.
 * 
 * @author Beat Fluri
 */
public class JavaChangeDistillerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ASTNodeTypeConverter.class).to(JavaASTNodeTypeConverter.class);
        bind(SourceCodeChangeClassifier.class).to(JavaSourceCodeChangeClassifier.class);
        install(new FactoryModuleBuilder().build(DistillerFactory.class));
        install(new FactoryModuleBuilder().implement(ASTHelper.class, JavaASTHelper.class)
                .build(ASTHelperFactory.class));
    }
}
