package org.evolizer.changedistiller.ast.java;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;

import org.evolizer.changedistiller.JavaChangeDistillerModule;
import org.evolizer.changedistiller.ast.ASTHelper;
import org.evolizer.changedistiller.ast.ASTHelperFactory;
import org.evolizer.changedistiller.structuredifferencing.StructureNode;
import org.evolizer.changedistiller.util.CompilationUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

@SuppressWarnings("unchecked")
public class WhenStructureEntitiesAreCreated {

    private static final String TEST_DATA = "src_change/";
    private static Injector sInjector;

    @BeforeClass
    public static void initialize() {
        sInjector = Guice.createInjector(new JavaChangeDistillerModule());
    }

    @Test
    public void publicModifierShouldBeConverted() throws Exception {
        File left = CompilationUtils.getFile(TEST_DATA + "TestLeft.java");
        ASTHelper<StructureNode> astHelper = sInjector.getInstance(ASTHelperFactory.class).create(left);
        StructureNode structureTree = astHelper.createStructureTree();
        StructureNode classNode = structureTree.getChildren().get(0);
        assertThat(astHelper.createStructureEntityVersion(classNode).isPublic(), is(true));
    }

    @Test
    public void finalModifierShouldBeConverted() throws Exception {
        File left = CompilationUtils.getFile(TEST_DATA + "TestRight.java");
        ASTHelper<StructureNode> astHelper = sInjector.getInstance(ASTHelperFactory.class).create(left);
        StructureNode structureTree = astHelper.createStructureTree();
        StructureNode classNode = structureTree.getChildren().get(0);
        assertThat(astHelper.createStructureEntityVersion(classNode).isPublic(), is(true));
        assertThat(astHelper.createStructureEntityVersion(classNode).isFinal(), is(true));
    }

    @Test
    public void protectedModifierShouldBeConverted() throws Exception {
        File left = CompilationUtils.getFile(TEST_DATA + "TestRight.java");
        ASTHelper<StructureNode> astHelper = sInjector.getInstance(ASTHelperFactory.class).create(left);
        StructureNode structureTree = astHelper.createStructureTree();
        StructureNode classNode = findNode(structureTree, "foo(int)");
        assertThat(astHelper.createStructureEntityVersion(classNode).isProtected(), is(true));
    }

    @Test
    public void privateModifierShouldBeConverted() throws Exception {
        File left = CompilationUtils.getFile(TEST_DATA + "TestLeft.java");
        ASTHelper<StructureNode> astHelper = sInjector.getInstance(ASTHelperFactory.class).create(left);
        StructureNode structureTree = astHelper.createStructureTree();
        StructureNode classNode = findNode(structureTree, "method()");
        assertThat(astHelper.createStructureEntityVersion(classNode).isPrivate(), is(true));
    }

    @Test
    public void publicFieldModifierShouldBeConverted() throws Exception {
        File left = CompilationUtils.getFile(TEST_DATA + "TestLeft.java");
        ASTHelper<StructureNode> astHelper = sInjector.getInstance(ASTHelperFactory.class).create(left);
        StructureNode structureTree = astHelper.createStructureTree();
        StructureNode classNode = findNode(structureTree, "aField : String");
        assertThat(astHelper.createStructureEntityVersion(classNode).isPublic(), is(true));
    }

    private StructureNode findNode(StructureNode root, String name) {
        for (StructureNode node : root.getChildren()) {
            if (node.getName().equals(name)) {
                return node;
            }
            StructureNode child = findNode(node, name);
            if (child != null) {
                return child;
            }
        }
        return null;
    }

}
