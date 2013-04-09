package ch.uzh.ifi.seal.changedistiller.distilling;

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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.util.CompilationUtils;

public class WhenExamplesFromRealProjectsAreDistilled {

    private static final String TEST_DATA = "src_issue/";
    private static FileDistiller distiller;

    @BeforeClass
    public static void initialize() {
        distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
    }

    @Test
    public void noStackOverflowErrorShouldOccur() throws Exception {
        File left = CompilationUtils.getFile(TEST_DATA + "8/HadoopStoreBuilderReducerLeft.java");
        File right = CompilationUtils.getFile(TEST_DATA + "8/HadoopStoreBuilderReducerRight.java");
        
        try {
        	distiller.extractClassifiedSourceCodeChanges(left, right);
        	assertThat(distiller.getSourceCodeChanges(), is(not(nullValue())));
        } catch (StackOverflowError err) {
        	fail("Source code change extraction failed because of a stack overflow (most likely while doing a post-order traversal of T1 during the edit script generation).");
        }
    }
    
    @Test
    public void noIllegalArgumentExceptionShouldOccur() throws Exception {
    	File left = CompilationUtils.getFile(TEST_DATA + "9/JsonStoreBuilderLeft.java");
        File right = CompilationUtils.getFile(TEST_DATA + "9/JsonStoreBuilderRight.java");
        
        try {
        	distiller.extractClassifiedSourceCodeChanges(left, right);
        	assertThat(distiller.getSourceCodeChanges(), is(not(nullValue())));
        } catch (IllegalArgumentException ex) {
        	fail("Source code change extraction failed because a node became its own ancestor.");
        }
    }
    
    @Test
    public void noNullPointerExceptionShouldOccur() throws Exception {
    	File left = CompilationUtils.getFile(TEST_DATA + "2/CompilerLeft.java");
        File right = CompilationUtils.getFile(TEST_DATA + "2/CompilerRight.java");
        
        try {
        	distiller.extractClassifiedSourceCodeChanges(left, right);
        	assertThat(distiller.getSourceCodeChanges(), is(not(nullValue())));
        } catch (NullPointerException ex) {
        	fail("Source code change extraction failed.");
        }
    }
}
