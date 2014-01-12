package ch.uzh.ifi.seal.changedistiller.ast.java;

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

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

import ch.uzh.ifi.seal.changedistiller.JavaChangeDistillerModule;
import ch.uzh.ifi.seal.changedistiller.ast.ASTHelper;
import ch.uzh.ifi.seal.changedistiller.ast.ASTHelperFactory;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureNode;
import ch.uzh.ifi.seal.changedistiller.util.CompilationUtils;

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
        ASTHelper<StructureNode> astHelper = getHelper(left);
        StructureNode structureTree = astHelper.createStructureTree();
        StructureNode classNode = structureTree.getChildren().get(0);
        assertThat(astHelper.createStructureEntityVersion(classNode).isPublic(), is(true));
    }

    @Test
    public void finalModifierShouldBeConverted() throws Exception {
        File left = CompilationUtils.getFile(TEST_DATA + "TestRight.java");
        ASTHelper<StructureNode> astHelper = getHelper(left);
        StructureNode structureTree = astHelper.createStructureTree();
        StructureNode classNode = structureTree.getChildren().get(0);
        assertThat(astHelper.createStructureEntityVersion(classNode).isPublic(), is(true));
        assertThat(astHelper.createStructureEntityVersion(classNode).isFinal(), is(true));
    }

    @Test
    public void abstractModifierShouldBeConverted() throws Exception {
    	File left = CompilationUtils.getFile(TEST_DATA + "Test2Left.java");
    	ASTHelper<StructureNode> astHelper = getHelper(left);
    	StructureNode structureTree = astHelper.createStructureTree();
    	StructureNode classNode = structureTree.getChildren().get(0);
    	assertThat(astHelper.createStructureEntityVersion(classNode).isPublic(), is(true));
    	assertThat(astHelper.createStructureEntityVersion(classNode).isAbstract(), is(true));
    }

    @Test
    public void protectedModifierShouldBeConverted() throws Exception {
        File left = CompilationUtils.getFile(TEST_DATA + "TestRight.java");
        ASTHelper<StructureNode> astHelper = getHelper(left);
        StructureNode structureTree = astHelper.createStructureTree();
        StructureNode classNode = findNode(structureTree, "foo(int)");
        assertThat(astHelper.createStructureEntityVersion(classNode).isProtected(), is(true));
    }

    @Test
    public void privateModifierShouldBeConverted() throws Exception {
        File left = CompilationUtils.getFile(TEST_DATA + "TestLeft.java");
        ASTHelper<StructureNode> astHelper = getHelper(left);
        StructureNode structureTree = astHelper.createStructureTree();
        StructureNode classNode = findNode(structureTree, "method()");
        assertThat(astHelper.createStructureEntityVersion(classNode).isPrivate(), is(true));
    }

    @Test
    public void nativeModifierShouldBeConverted() throws Exception {
    	File left = CompilationUtils.getFile(TEST_DATA + "TestLeft.java");
    	ASTHelper<StructureNode> astHelper = getHelper(left);
    	StructureNode structureTree = astHelper.createStructureTree();
    	StructureNode classNode = findNode(structureTree, "nativeMethod()");
    	assertThat(astHelper.createStructureEntityVersion(classNode).isNative(), is(true));
    }

    @Test
    public void strictfpModifierShouldBeConverted() throws Exception {
    	File left = CompilationUtils.getFile(TEST_DATA + "TestLeft.java");
    	ASTHelper<StructureNode> astHelper = getHelper(left);
    	StructureNode structureTree = astHelper.createStructureTree();
    	StructureNode classNode = findNode(structureTree, "strictfpMethod()");
    	assertThat(astHelper.createStructureEntityVersion(classNode).isStrictfp(), is(true));
    }

    @Test
    public void publicFieldModifierShouldBeConverted() throws Exception {
        File left = CompilationUtils.getFile(TEST_DATA + "TestLeft.java");
        ASTHelper<StructureNode> astHelper = getHelper(left);
        StructureNode structureTree = astHelper.createStructureTree();
        StructureNode classNode = findNode(structureTree, "aField : String");
        assertThat(astHelper.createStructureEntityVersion(classNode).isPublic(), is(true));
    }

    @Test
    public void staticFieldModifierShouldBeConverted() throws Exception {
    	File left = CompilationUtils.getFile(TEST_DATA + "TestLeft.java");
    	ASTHelper<StructureNode> astHelper = getHelper(left);
    	StructureNode structureTree = astHelper.createStructureTree();
    	StructureNode classNode = findNode(structureTree, "sField : String");
    	assertThat(astHelper.createStructureEntityVersion(classNode).isStatic(), is(true));
    }

    @Test
    public void volatileFieldModifierShouldBeConverted() throws Exception {
    	File left = CompilationUtils.getFile(TEST_DATA + "TestLeft.java");
    	ASTHelper<StructureNode> astHelper = getHelper(left);
    	StructureNode structureTree = astHelper.createStructureTree();
    	StructureNode classNode = findNode(structureTree, "vField : int");
    	assertThat(astHelper.createStructureEntityVersion(classNode).isVolatile(), is(true));
    }

    @Test
    public void synchronizedFieldModifierShouldBeConverted() throws Exception {
    	File left = CompilationUtils.getFile(TEST_DATA + "TestLeft.java");
    	ASTHelper<StructureNode> astHelper = getHelper(left);
    	StructureNode structureTree = astHelper.createStructureTree();
    	StructureNode classNode = findNode(structureTree, "synchField : long");
    	assertThat(astHelper.createStructureEntityVersion(classNode).isSynchronized(), is(true));
    }

    @Test
    public void transientFieldModifierShouldBeConverted() throws Exception {
    	File left = CompilationUtils.getFile(TEST_DATA + "TestLeft.java");
    	ASTHelper<StructureNode> astHelper = getHelper(left);
    	StructureNode structureTree = astHelper.createStructureTree();
    	StructureNode classNode = findNode(structureTree, "tField : String");
    	assertThat(astHelper.createStructureEntityVersion(classNode).isTransient(), is(true));
    }

	private ASTHelper<StructureNode> getHelper(File left) {
		return sInjector.getInstance(ASTHelperFactory.class).create(left, "default");
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
