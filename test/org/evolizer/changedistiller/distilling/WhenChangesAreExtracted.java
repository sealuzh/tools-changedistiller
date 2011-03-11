package org.evolizer.changedistiller.distilling;

import org.evolizer.changedistiller.model.entities.StructureEntityVersion;
import org.junit.BeforeClass;

import com.google.inject.Guice;
import com.google.inject.Injector;


public abstract class WhenChangesAreExtracted {

    protected static Injector sInjector;

    protected Distiller getDistiller(StructureEntityVersion structureEntity) {
        Distiller distiller = sInjector.getInstance(DistillerFactory.class).create(structureEntity);
        return distiller;
    }

    protected static interface DistillerFactory {
    
        Distiller create(StructureEntityVersion structureEntity);
    }

    @BeforeClass
    public static void createInjector() {
        sInjector = Guice.createInjector(new DistillerModule());
    }

}
