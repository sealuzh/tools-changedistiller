package org.evolizer.changedistiller.distilling;

import org.evolizer.changedistiller.distilling.WhenChangesAreExtracted.DistillerFactory;
import org.evolizer.changedistiller.distilling.java.JavaASTHelper;
import org.evolizer.changedistiller.distilling.java.JavaSourceCodeChangeClassifier;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryProvider;

public class DistillerModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ASTHelper.class).to(JavaASTHelper.class);
        bind(SourceCodeChangeClassifier.class).to(JavaSourceCodeChangeClassifier.class);
        bind(DistillerFactory.class).toProvider(FactoryProvider.newFactory(DistillerFactory.class, Distiller.class));
    }
}
