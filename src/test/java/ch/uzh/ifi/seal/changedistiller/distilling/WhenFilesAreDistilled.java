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

import java.io.File;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import ch.uzh.ifi.seal.changedistiller.ChangeDistiller;
import ch.uzh.ifi.seal.changedistiller.ChangeDistiller.Language;
import ch.uzh.ifi.seal.changedistiller.distilling.FileDistiller;
import ch.uzh.ifi.seal.changedistiller.model.entities.ClassHistory;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.util.CompilationUtils;

public class WhenFilesAreDistilled {

    private static final String TEST_DATA = "src_change/";
    private static FileDistiller distiller;

    @BeforeClass
    public static void initialize() {
        distiller = ChangeDistiller.createFileDistiller(Language.JAVA);
    }

    @Test
    public void unchangedFilesShouldNotProduceSourceCodeChanges() throws Exception {
        File left = CompilationUtils.getFile(TEST_DATA + "TestLeft.java");
        File right = CompilationUtils.getFile(TEST_DATA + "TestLeft.java");
        distiller.extractClassifiedSourceCodeChanges(left, right);
        List<SourceCodeChange> changes = distiller.getSourceCodeChanges();
        assertThat(changes, is(not(nullValue())));
        assertThat(changes.size(), is(0));
    }

    @Test
    public void changedFilesShouldProduceSourceCodeChanges() throws Exception {
        File left = CompilationUtils.getFile(TEST_DATA + "TestLeft.java");
        File right = CompilationUtils.getFile(TEST_DATA + "TestRight.java");
        distiller.extractClassifiedSourceCodeChanges(left, right);
        assertThat(distiller.getSourceCodeChanges().size(), is(23));
    }

    @Test
    public void changedFilesShouldProduceClassHistories() throws Exception {
        File left = CompilationUtils.getFile(TEST_DATA + "TestLeft.java");
        File right = CompilationUtils.getFile(TEST_DATA + "TestRight.java");
        distiller.extractClassifiedSourceCodeChanges(left, right);
        ClassHistory classHistory = distiller.getClassHistory();
        assertThat(classHistory.getAttributeHistories().size(), is(3));
        assertThat(classHistory.getMethodHistories().size(), is(1));
        assertThat(classHistory.getInnerClassHistories().size(), is(1));
        classHistory = classHistory.getInnerClassHistories().values().iterator().next();
        assertThat(classHistory.getUniqueName(), is("test.Test.Bar"));
        assertThat(classHistory.getMethodHistories().size(), is(1));
        String k = classHistory.getMethodHistories().keySet().iterator().next();
        assertThat(classHistory.getMethodHistories().get(k).getUniqueName(), is("test.Test.Bar.newMethod()"));
    }
}
