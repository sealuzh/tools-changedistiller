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
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation;
import ch.uzh.ifi.seal.changedistiller.distilling.Distiller;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.model.entities.StructureEntityVersion;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import ch.uzh.ifi.seal.changedistiller.util.CompilationUtils;

// simple test cases. exhaustive test cases with classification check in separate tests suite
public class WhenMethodBodyChangesAreExtracted extends WhenChangesAreExtracted {

    private final static String TEST_DATA = "src_change/";

    @Test
    public void unchangedMethodBodyShouldNotHaveAnyChanges() throws Exception {
        JavaCompilation compilation = CompilationUtils.compileFile(TEST_DATA + "TestLeft.java");
        Node rootLeft = convertMethodBody("foo", compilation);
        Node rootRight = convertMethodBody("foo", compilation);
        StructureEntityVersion structureEntity = new StructureEntityVersion(JavaEntityType.METHOD, "foo", 0);
        Distiller distiller = getDistiller(structureEntity);
        distiller.extractClassifiedSourceCodeChanges(rootLeft, rootRight);
        assertThat(structureEntity.getSourceCodeChanges().isEmpty(), is(true));
    }

    @Test
    public void changedMethodBodyShouldHaveChanges() throws Exception {
        JavaCompilation compilationLeft = CompilationUtils.compileFile(TEST_DATA + "TestLeft.java");
        JavaCompilation compilationRight = CompilationUtils.compileFile(TEST_DATA + "TestRight.java");
        Node rootLeft = convertMethodBody("foo", compilationLeft);
        Node rootRight = convertMethodBody("foo", compilationRight);
        StructureEntityVersion structureEntity = new StructureEntityVersion(JavaEntityType.METHOD, "foo", 0);
        Distiller distiller = getDistiller(structureEntity);
        distiller.extractClassifiedSourceCodeChanges(rootLeft, rootRight);
        assertThat(structureEntity.getSourceCodeChanges().size(), is(11));
    }

}
