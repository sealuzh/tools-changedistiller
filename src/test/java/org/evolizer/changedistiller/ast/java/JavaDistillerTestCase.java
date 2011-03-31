package org.evolizer.changedistiller.ast.java;

import org.evolizer.changedistiller.JavaChangeDistillerModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public abstract class JavaDistillerTestCase {

    protected static final Injector sInjector;

    static {
        sInjector = Guice.createInjector(new JavaChangeDistillerModule());
    }

}
