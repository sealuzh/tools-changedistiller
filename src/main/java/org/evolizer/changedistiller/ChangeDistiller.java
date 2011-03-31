package org.evolizer.changedistiller;

import org.evolizer.changedistiller.distilling.FileDistiller;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Factory for creating the {@link FileDistiller} for a particular {@link Language}.
 * 
 * @author Beat Fluri
 */
public final class ChangeDistiller {

    private ChangeDistiller() {}

    /**
     * Creates a {@link FileDistiller} for the given language.
     * 
     * @param language
     *            for which the file distiller should be created
     * @return the file distiller for the given language
     */
    public static FileDistiller createFileDistiller(Language language) {
        switch (language) {
            case JAVA:
                Injector injector = Guice.createInjector(new JavaChangeDistillerModule());
                return injector.getInstance(FileDistiller.class);
        }
        return null;
    }

    public static Language[] getProvidedLanguages() {
        return Language.values();
    }

    /**
     * Programming languages that ChangeDistiller can handle.
     * 
     * @author Beat Fluri
     */
    public enum Language {
        JAVA
    }

}
