package org.evolizer.changedistiller.distilling;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;

import org.evolizer.changedistiller.ChangeDistiller;
import org.evolizer.changedistiller.ChangeDistiller.Language;
import org.evolizer.changedistiller.model.entities.ClassHistory;
import org.evolizer.changedistiller.util.CompilationUtils;
import org.junit.BeforeClass;
import org.junit.Test;

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
        assertThat(distiller.getSourceCodeChanges(), is(nullValue()));
    }

    @Test
    public void changedFilesShouldProduceSourceCodeChanges() throws Exception {
        File left = CompilationUtils.getFile(TEST_DATA + "TestLeft.java");
        File right = CompilationUtils.getFile(TEST_DATA + "TestRight.java");
        distiller.extractClassifiedSourceCodeChanges(left, right);
        assertThat(distiller.getSourceCodeChanges().size(), is(22));
    }

    @Test
    public void changedFilesShouldProduceClassHistories() throws Exception {
        File left = CompilationUtils.getFile(TEST_DATA + "TestLeft.java");
        File right = CompilationUtils.getFile(TEST_DATA + "TestRight.java");
        distiller.extractClassifiedSourceCodeChanges(left, right);
        ClassHistory classHistory = distiller.getClassHistory();
        assertThat(classHistory.getAttributeHistories().size(), is(2));
        assertThat(classHistory.getMethodHistories().size(), is(1));
        assertThat(classHistory.getInnerClassHistories().size(), is(1));
        classHistory = classHistory.getInnerClassHistories().values().iterator().next();
        assertThat(classHistory.getUniqueName(), is("test.Test.Bar"));
        assertThat(classHistory.getMethodHistories().size(), is(1));
        String k = classHistory.getMethodHistories().keySet().iterator().next();
        assertThat(classHistory.getMethodHistories().get(k).getUniqueName(), is("test.Test.Bar.newMethod()"));
    }
}
