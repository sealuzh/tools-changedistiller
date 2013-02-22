package ch.uzh.ifi.seal.changedistiller.model.classifiers;

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

/**
 * Modifier representation used by ChangeDistiller.
 * <p>
 * Defined modifiers: <code>final</code>, <code>public</code>, <code>private</code>, <code>protected</code>
 * 
 * @author zubi
 */
public final class ChangeModifier {

    /**
     * The <code>int</code> value representing the <code>final</code> modifier.
     */
    public static final int FINAL = 0x0001;

    /**
     * The <code>int</code> value representing the <code>private</code> modifier.
     */
    public static final int PRIVATE = 0x0004;

    /**
     * The <code>int</code> value representing the <code>protected</code> modifier.
     */
    public static final int PROTECTED = 0x0008;

    /**
     * The <code>int</code> value representing the <code>public</code> modifier.
     */
    public static final int PUBLIC = 0x0002;

    private ChangeModifier() {}

    /**
     * Returns whether the given flag includes the <code>final</code> modifier or not.
     * 
     * @param flag
     *            modifier flag
     * @return <code>true</code> if the flag contains the <code>final</code> modifier, <code>false</code> otherwise.
     */
    public static boolean isFinal(int flag) {
        return (flag & FINAL) != 0;
    }

    /**
     * Returns whether the given flag includes the <code>private</code> modifier or not.
     * 
     * @param flag
     *            modifier flag
     * @return <code>true</code> if the flag contains the <code>private</code> modifier, <code>false</code> otherwise.
     */
    public static boolean isPrivate(int flag) {
        return (flag & PRIVATE) != 0;
    }

    /**
     * Returns whether the given flag includes the <code>protected</code> modifier or not.
     * 
     * @param flag
     *            modifier flag
     * @return <code>true</code> if the flag contains the <code>protected</code> modifier, <code>false</code> otherwise.
     */
    public static boolean isProtected(int flag) {
        return (flag & PROTECTED) != 0;
    }

    /**
     * Returns whether the given flag includes the <code>public</code> modifier or not.
     * 
     * @param flag
     *            modifier flag
     * @return <code>true</code> if the flag contains the <code>public</code> modifier, <code>false</code> otherwise.
     */
    public static boolean isPublic(int flag) {
        return (flag & PUBLIC) != 0;
    }

}
