package org.evolizer.changedistiller.distilling;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.evolizer.changedistiller.model.classifiers.java.JavaEntityType;
import org.evolizer.changedistiller.model.entities.StructureEntityVersion;
import org.evolizer.changedistiller.treedifferencing.Node;
import org.evolizer.changedistiller.util.Compilation;
import org.evolizer.changedistiller.util.CompilationUtils;
import org.junit.Test;

//simple test cases. exhaustive test cases with classification check in separate tests suite
public class WhenDeclarationChangesAreExtracted extends WhenChangesAreExtracted {

    private final static String TEST_DATA = "src_change/";

    @Test
    public void unchangedMethodDeclarationShouldNotHaveAnyChanges() throws Exception {
        Compilation compilation = CompilationUtils.compileFile(TEST_DATA + "TestLeft.java");
        Node rootLeft = convertMethodDeclaration("foo", compilation);
        Node rootRight = convertMethodDeclaration("foo", compilation);
        StructureEntityVersion structureEntity = new StructureEntityVersion(JavaEntityType.METHOD, "foo", 0);
        Distiller distiller = getDistiller(structureEntity);
        distiller.extractClassifiedSourceCodeChanges(rootLeft, rootRight);
        assertThat(structureEntity.getSourceCodeChanges().isEmpty(), is(true));
    }

    @Test
    public void changedMethodShouldHaveChanges() throws Exception {
        Compilation compilationLeft = CompilationUtils.compileFile(TEST_DATA + "TestLeft.java");
        Compilation compilationRight = CompilationUtils.compileFile(TEST_DATA + "TestRight.java");
        Node rootLeft = convertMethodDeclaration("foo", compilationLeft);
        Node rootRight = convertMethodDeclaration("foo", compilationRight);
        StructureEntityVersion structureEntity = new StructureEntityVersion(JavaEntityType.METHOD, "foo", 0);
        Distiller distiller = getDistiller(structureEntity);
        distiller.extractClassifiedSourceCodeChanges(rootLeft, rootRight);
        assertThat(structureEntity.getSourceCodeChanges().size(), is(2));
    }

}
