package org.evolizer.changedistiller.distilling;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;

import org.evolizer.changedistiller.distilling.java.JavaDistillerTestCase;
import org.evolizer.changedistiller.model.entities.ClassHistory;
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
        assertThat(distiller.getSourceCodeChanges().size(), is(22));
    }

    @Test
    public void changedFilesShouldProduceClassHistories() throws Exception {
        File left = CompilationUtils.getFile(TEST_DATA + "TestLeft.java");
        File right = CompilationUtils.getFile(TEST_DATA + "TestRight.java");
        FileDistiller distiller = sInjector.getInstance(FileDistiller.class);
        distiller.extractClassifiedSourceCodeChanges(left, right);
        ClassHistory classHistory = distiller.getClassHistory();
        assertThat(classHistory.getAttributeHistories().size(), is(2));
        assertThat(classHistory.getMethodHistories().size(), is(1));
        assertThat(classHistory.getInnerClassHistories().size(), is(1));
        classHistory = classHistory.getInnerClassHistories().values().iterator().next();
        assertThat(classHistory.getMethodHistories().size(), is(1));
    }
}
