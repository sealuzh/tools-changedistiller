package org.evolizer.changedistiller.distilling;

import org.evolizer.changedistiller.model.entities.StructureEntityVersion;

/**
 * Factory interface to create {@link Distiller} working with a {@link StructureEntityVersion}.
 * 
 * @author Beat Fluri
 */
public interface DistillerFactory {

    /**
     * Creates and returns a {@link Distiller} working on a {@link StructureEntityVersion}.
     * 
     * @param structureEntity
     *            the distiller works on
     * @return the distiller working on the structure entity version
     */
    Distiller create(StructureEntityVersion structureEntity);
}
