package org.evolizer.changedistiller.distilling.java;

import org.evolizer.changedistiller.distilling.JavaDistillerModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public abstract class JavaDistillerTestCase {

    protected static final Injector sInjector;

    static {
        sInjector = Guice.createInjector(new JavaDistillerModule());
    }

}
