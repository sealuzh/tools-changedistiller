package ch.uzh.ifi.seal.changedistiller;


import ch.uzh.ifi.seal.changedistiller.ast.ASTHelper;
import ch.uzh.ifi.seal.changedistiller.ast.ASTHelperFactory;
import ch.uzh.ifi.seal.changedistiller.ast.ASTNodeTypeConverter;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaASTHelper;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaASTNodeTypeConverter;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaSourceCodeChangeClassifier;
import ch.uzh.ifi.seal.changedistiller.distilling.DistillerFactory;
import ch.uzh.ifi.seal.changedistiller.distilling.SourceCodeChangeClassifier;

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
