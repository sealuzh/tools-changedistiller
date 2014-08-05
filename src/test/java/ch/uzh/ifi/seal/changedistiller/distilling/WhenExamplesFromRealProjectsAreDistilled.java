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
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.ast.InvalidSyntaxException;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.model.entities.Insert;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.model.entities.Update;
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
        	ex.printStackTrace();
        	fail("Source code change extraction failed because a node became its own ancestor.");
        }
    }
    
    @Test
    public void noNullPointerExceptionShouldOccur1() throws Exception {
    	File left = CompilationUtils.getFile(TEST_DATA + "2/CompilerLeft.java");
        File right = CompilationUtils.getFile(TEST_DATA + "2/CompilerRight.java");
        
        try {
        	distiller.extractClassifiedSourceCodeChanges(left, right);
        	assertThat(distiller.getSourceCodeChanges(), is(not(nullValue())));
        } catch (NullPointerException ex) {
        	ex.printStackTrace();
        	fail("Source code change extraction failed.");
        }
    }
    
    @Test
    public void noNullPointerExceptionShouldOccur2() throws Exception {
    	File left = CompilationUtils.getFile(TEST_DATA + "10/ReadOnlyStorageEngineLeft.java");
        File right = CompilationUtils.getFile(TEST_DATA + "10/ReadOnlyStorageEngineRight.java");
        
        try {
        	distiller.extractClassifiedSourceCodeChanges(left, right);
        	assertThat(distiller.getSourceCodeChanges(), is(not(nullValue())));
        } catch (NullPointerException ex) {
        	ex.printStackTrace();
        	fail("Source code change extraction failed.");
        }
    }

    @Test
    public void noNullPointerExceptionShouldOccur3() throws Exception {
    	File left = CompilationUtils.getFile(TEST_DATA + "19/AstNodeLeft.java");
    	File right = CompilationUtils.getFile(TEST_DATA + "19/AstNodeRight.java");
    	
    	try {
    		distiller.extractClassifiedSourceCodeChanges(left, right);
    		assertThat(distiller.getSourceCodeChanges(), is(not(nullValue())));
    	} catch (NullPointerException ex) {
    		ex.printStackTrace();
    		fail("Source code change extraction failed.");
    	}
    }

    @Test
    public void noNullPointerExceptionShouldOccur4() throws Exception {
    	File left = CompilationUtils.getFile(TEST_DATA + "20/R1Left.java");
    	File right = CompilationUtils.getFile(TEST_DATA + "20/R1Right.java");
    	
    	try {
    		distiller.extractClassifiedSourceCodeChanges(left, right);
    		assertThat(distiller.getSourceCodeChanges(), is(not(nullValue())));
    	} catch (NullPointerException ex) {
    		ex.printStackTrace();
    		fail("Source code change extraction failed.");
    	}
    }

    @Test
    public void noNullPointerExceptionShouldOccur5() throws Exception {
    	File left = CompilationUtils.getFile(TEST_DATA + "21/ASTConverterLeft.java");
    	File right = CompilationUtils.getFile(TEST_DATA + "21/ASTConverterRight.java");
    	
    	try {
    		distiller.extractClassifiedSourceCodeChanges(left, right);
    		List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
    		assertThat(changes, is(not(nullValue())));
    		assertThat(changes.size(), is(0));
    	} catch (NullPointerException ex) {
    		ex.printStackTrace();
    		fail("Source code change extraction failed.");
    	}
    }
    
    @Test
    public void primitiveTypesShouldNotBeSimpleTypes() throws Exception {
    	File left = CompilationUtils.getFile(TEST_DATA + "14/PrimitiveVsSimpleTypeLeft.java");
    	File right = CompilationUtils.getFile(TEST_DATA + "14/PrimitiveVsSimpleTypeRight.java");
    	
    	distiller.extractClassifiedSourceCodeChanges(left, right);
    	assertThat(distiller.getSourceCodeChanges(), is(not(nullValue())));
    	
    	List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
    	assertThat(changes.size(), is(1));
    	
    	SourceCodeChange singleChange = changes.get(0);
    	
    	if(singleChange instanceof Update) {
    		Update update = (Update) singleChange;
    		SourceCodeEntity entity = update.getNewEntity();
    		assertThat((JavaEntityType) entity.getType(), is(JavaEntityType.SINGLE_TYPE));
    	} else {
    		fail("Should be Update but was " + singleChange.getClass());
    	}
    }

    @Test
    public void postfixExpressionUpdateShouldBeDetected() throws Exception {
    	File left = CompilationUtils.getFile(TEST_DATA + "22/TestLeft.java");
    	File right = CompilationUtils.getFile(TEST_DATA + "22/TestRight.java");
    	
    	distiller.extractClassifiedSourceCodeChanges(left, right);
    	assertThat(distiller.getSourceCodeChanges(), is(not(nullValue())));
    	
    	List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
    	assertThat(changes.size(), is(1));
    	
    	SourceCodeChange singleChange = changes.get(0);
    	
    	if(singleChange instanceof Update) {
    		Update update = (Update) singleChange;
    		SourceCodeEntity entity = update.getNewEntity();
    		assertThat((JavaEntityType) entity.getType(), is(JavaEntityType.POSTFIX_EXPRESSION));
    	} else {
    		fail("Should be Update but was " + singleChange.getClass());
    	}
    }
    
    @Test
    public void statementInsertIntoSwitchShouldBeDetected() throws Exception {
    	File left = CompilationUtils.getFile(TEST_DATA + "26/TestLeft.java");
    	File right = CompilationUtils.getFile(TEST_DATA + "26/TestRight.java");
    	
    	distiller.extractClassifiedSourceCodeChanges(left, right);
    	assertThat(distiller.getSourceCodeChanges(), is(not(nullValue())));
    	
    	List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
    	assertThat(changes.size(), is(1));
    	
    	SourceCodeChange singleChange = changes.get(0);
    	
    	if(singleChange instanceof Insert) {
    		Insert insert = (Insert) singleChange;
    		SourceCodeEntity entity = insert.getChangedEntity();
    		assertThat((JavaEntityType) entity.getType(), is(JavaEntityType.POSTFIX_EXPRESSION));
    	} else {
    		fail("Should be Insert but was " + singleChange.getClass());
    	}
    }
    @Test
    public void fileDistillerShouldParseJava14() {
        File left = CompilationUtils.getFile(TEST_DATA + "32/jEditLeft.java");
        File right = CompilationUtils.getFile(TEST_DATA + "32/jEditRight.java");
        distiller.extractClassifiedSourceCodeChanges(left, "1.4", right, "1.4");
        assertThat(distiller.getSourceCodeChanges().size(), greaterThan(0)); 	
    }
}
