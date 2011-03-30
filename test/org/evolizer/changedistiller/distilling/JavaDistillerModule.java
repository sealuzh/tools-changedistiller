package org.evolizer.changedistiller.distilling;

import org.evolizer.changedistiller.compilation.ASTHelperFactory;
import org.evolizer.changedistiller.compilation.java.JavaASTHelper;
import org.evolizer.changedistiller.distilling.java.JavaASTNodeTypeConverter;
import org.evolizer.changedistiller.distilling.java.JavaSourceCodeChangeClassifier;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryProvider;

public class JavaDistillerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ASTNodeTypeConverter.class).to(JavaASTNodeTypeConverter.class);
        bind(SourceCodeChangeClassifier.class).to(JavaSourceCodeChangeClassifier.class);
        bind(DistillerFactory.class).toProvider(FactoryProvider.newFactory(DistillerFactory.class, Distiller.class));
        bind(ASTHelperFactory.class)
                .toProvider(FactoryProvider.newFactory(ASTHelperFactory.class, JavaASTHelper.class));
    }

}
