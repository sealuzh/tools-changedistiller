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
	 * "abstract" modifier constant (bit mask).
	 * Applicable to types and methods.
	 */
	public static final int ABSTRACT = 0x0400;

	/**
	 * "final" modifier constant (bit mask).
	 * Applicable to types, methods, fields, and variables.
	 */
	public static final int FINAL = 0x0010;

	/**
	 * "native" modifier constant (bit mask).
	 * Applicable only to methods.
	 */
	public static final int NATIVE = 0x0100;

	/**
	 * "private" modifier constant (bit mask).
	 * Applicable to types, methods, constructors, and fields.
	 */
	public static final int PRIVATE = 0x0002;
	
	/**
	 * "protected" modifier constant (bit mask).
	 * Applicable to types, methods, constructors, and fields.
	 */
	public static final int PROTECTED = 0x0004;

	/**
	 * "public" modifier constant (bit mask).
	 * Applicable to types, methods, constructors, and fields.
	 */
	public static final int PUBLIC = 0x0001;

	/**
	 * "static" modifier constant (bit mask).
	 * Applicable to types, methods, fields, and initializers.
	 */
	public static final int STATIC = 0x0008;

	/**
	 * "strictfp" modifier constant (bit mask).
	 * Applicable to types and methods.
	 */
	public static final int STRICTFP = 0x0800;

	/**
	 * "synchronized" modifier constant (bit mask).
	 * Applicable only to methods.
	 */
	public static final int SYNCHRONIZED = 0x0020;

	/**
	 * "transient" modifier constant (bit mask).
	 * Applicable only to fields.
	 */
	public static final int TRANSIENT = 0x0080;

	/**
	 * "volatile" modifier constant (bit mask).
	 * Applicable only to fields.
	 */
	public static final int VOLATILE = 0x0040;

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

    /**
     * Returns whether the given flag includes the <code>static</code> modifier or not.
     * 
     * @param flag
     *            modifier flag
     * @return <code>true</code> if the flag contains the <code>static</code> modifier, <code>false</code> otherwise.
     */
	public static boolean isStatic(int flag) {
		return (flag & STATIC) != 0;
	}
	
	/**
	 * Returns whether the given flag includes the <code>abstract</code> modifier or not.
	 * 
	 * @param flag
	 *            modifier flag
	 * @return <code>true</code> if the flag contains the <code>abstract</code> modifier, <code>false</code> otherwise.
	 */
	public static boolean isAbstract(int flag) {
		return (flag & ABSTRACT) != 0;
	}
	
	/**
	 * Returns whether the given flag includes the <code>native</code> modifier or not.
	 * 
	 * @param flag
	 *            modifier flag
	 * @return <code>true</code> if the flag contains the <code>native</code> modifier, <code>false</code> otherwise.
	 */
	public static boolean isNative(int flag) {
		return (flag & NATIVE) != 0;
	}
	
	/**
	 * Returns whether the given flag includes the <code>synchronized</code> modifier or not.
	 * 
	 * @param flag
	 *            modifier flag
	 * @return <code>true</code> if the flag contains the <code>synchronized</code> modifier, <code>false</code> otherwise.
	 */
	public static boolean isSynchronized(int flag) {
		return (flag & SYNCHRONIZED) != 0;
	}

	/**
	 * Returns whether the given flag includes the <code>transient</code> modifier or not.
	 * 
	 * @param flag
	 *            modifier flag
	 * @return <code>true</code> if the flag contains the <code>transient</code> modifier, <code>false</code> otherwise.
	 */
	public static boolean isTransient(int flag) {
		return (flag & TRANSIENT) != 0;
	}

	/**
	 * Returns whether the given flag includes the <code>volatile</code> modifier or not.
	 * 
	 * @param flag
	 *            modifier flag
	 * @return <code>true</code> if the flag contains the <code>volatile</code> modifier, <code>false</code> otherwise.
	 */
	public static boolean isVolatile(int flag) {
		return (flag & VOLATILE) != 0;
	}

	/**
	 * Returns whether the given flag includes the <code>strictfp</code> modifier or not.
	 * 
	 * @param flag
	 *            modifier flag
	 * @return <code>true</code> if the flag contains the <code>strictfp</code> modifier, <code>false</code> otherwise.
	 */
	public static boolean isStrictfp(int flag) {
		return (flag & STRICTFP) != 0;
	}
}
