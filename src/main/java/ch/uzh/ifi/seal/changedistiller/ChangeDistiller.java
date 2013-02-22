package ch.uzh.ifi.seal.changedistiller;

/*
 * #%L
 * ChangeDistiller
 * %%
 * Copyright (C) 2011 - 2013 Software Architecture and Evolution Lab, Department of Informatics, UZH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;

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
