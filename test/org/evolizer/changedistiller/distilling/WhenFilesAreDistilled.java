package org.evolizer.changedistiller.distilling;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.util.List;

import org.evolizer.changedistiller.distilling.java.JavaDistillerTestCase;
import org.evolizer.changedistiller.model.entities.ClassHistory;
import org.evolizer.changedistiller.model.entities.StructureEntityVersion;
import org.evolizer.changedistiller.util.CompilationUtils;
import org.junit.Test;

public class WhenFilesAreDistilled extends JavaDistillerTestCase {

    private static final String TEST_DATA = "src_change/";

    @Test
    public void unchangedFilesShouldNotProduceSourceCodeChanges() throws Exception {
        File left = CompilationUtils.getFile(TEST_DATA + "TestLeft.java");
        File right = CompilationUtils.getFile(TEST_DATA + "TestLeft.java");
        FileDistiller distiller = sInjector.getInstance(FileDistiller.class);
        distiller.extractClassifiedSourceCodeChanges(left, right);
        assertThat(distiller.getSourceCodeChanges(), is(nullValue()));
    }

    @Test
    public void changedFilesShouldProduceSourceCodeChanges() throws Exception {
        File left = CompilationUtils.getFile(TEST_DATA + "TestLeft.java");
        File right = CompilationUtils.getFile(TEST_DATA + "TestRight.java");
        FileDistiller distiller = sInjector.getInstance(FileDistiller.class);
        distiller.extractClassifiedSourceCodeChanges(left, right);
        assertThat(distiller.getStructureEntityVersions().size(), is(3));
        List<StructureEntityVersion> entities = distiller.getStructureEntityVersions();
        StructureEntityVersion entity = entities.get(0);
        assertThat(entity.getUniqueName(), is("test.Test.foo(int)"));
        assertThat(entity.getSourceCodeChanges().size(), is(12));
        entity = entities.get(1);
        assertThat(entity.getUniqueName(), is("test.Test.Bar"));
        assertThat(entity.getSourceCodeChanges().size(), is(2));
        entity = entities.get(2);
        assertThat(entity.getUniqueName(), is("test.Test"));
        assertThat(entity.getSourceCodeChanges().size(), is(10));
    }

    @Test
    public void changedFilesShouldProduceClassHistories() throws Exception {
        File left = CompilationUtils.getFile(TEST_DATA + "TestLeft.java");
        File right = CompilationUtils.getFile(TEST_DATA + "TestRight.java");
        FileDistiller distiller = sInjector.getInstance(FileDistiller.class);
        distiller.extractClassifiedSourceCodeChanges(left, right);
        ClassHistory classHistory = distiller.getClassHistory();
        // assertThat(classHistory.getAttributeHistories().size(), is(4));
        assertThat(classHistory.getMethodHistories().size(), is(1));
        assertThat(classHistory.getInnerClassHistories().size(), is(1));
    }
}
