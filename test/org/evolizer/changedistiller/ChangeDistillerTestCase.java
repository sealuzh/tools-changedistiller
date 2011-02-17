package org.evolizer.changedistiller;

import org.junit.BeforeClass;

/**
 * Base class for all test cases.
 * 
 * @author Beat Fluri
 */
public abstract class ChangeDistillerTestCase {

    protected static String sClass;

    /**
     * Prepares the compliation unit for traversing.
     * 
     * @throws Exception
     *             so that JUnit may collect and report
     */
    @BeforeClass
    public static void prepareCompilationUnit() throws Exception {

    }

}
