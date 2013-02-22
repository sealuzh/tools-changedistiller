package ch.uzh.ifi.seal.changedistiller.structuredifferencing;

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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Collections;

import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.CompilationUnitScope;
import org.junit.Test;

import ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureDiffNode;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureDifferencer;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.StructureDifferencer.DiffType;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.java.JavaStructureNode;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.java.JavaStructureTreeBuilder;
import ch.uzh.ifi.seal.changedistiller.structuredifferencing.java.JavaStructureNode.Type;
import ch.uzh.ifi.seal.changedistiller.util.CompilationUtils;

public class WhenStructureDifferencesAreExtracted {

    private JavaStructureNode fLeft;
    private JavaStructureNode fRight;
    private StructureDiffNode fDiffs;

    @Test
    public void equalClassesShouldReturnNoChanges() throws Exception {
        createLeftStructureTree("public class Foo { String fName; void method(int a) { a = 24; } }");
        createRightStructureTree("public class Foo { String fName; void method(int a) { a = 24; } }");
        createDifferences();
        assertThat(fDiffs, is(nullValue()));
    }

    @Test
    public void differentMemberOrderingShouldReturnNoChanges() throws Exception {
        createLeftStructureTree("public class Foo { String fName; void method(int a) { a = 24; } }");
        createRightStructureTree("public class Foo { void method(int a) { a = 24; } String fName; }");
        createDifferences();
        assertThat(fDiffs, is(nullValue()));
    }

    @Test
    public void addedMethodShouldBeFound() throws Exception {
        createLeftStructureTree("public class Foo {}");
        createRightStructureTree("public class Foo { void method(int a) { a = 24; } }");
        createDifferences();
        assertThat(fDiffs.getChildren().size(), is(1));
        StructureDiffNode methodAddition = fDiffs.getChildren().get(0).getChildren().get(0);
        assertThat(methodAddition.getDiffType(), is(DiffType.ADDITION));
        assertThat(methodAddition.getChildren(), is(Collections.EMPTY_LIST));
        assertThat(methodAddition.getLeft(), is(nullValue()));
        assertThat(methodAddition.getRight().getName(), is("method(int)"));
    }

    @Test
    public void deletedMethodShouldBeFound() throws Exception {
        createLeftStructureTree("public class Foo { void method(int a) { a = 24; } }");
        createRightStructureTree("public class Foo {}");
        createDifferences();
        assertThat(fDiffs.getChildren().size(), is(1));
        StructureDiffNode methodDeletion = fDiffs.getChildren().get(0).getChildren().get(0);
        assertThat(methodDeletion.getDiffType(), is(DiffType.DELETION));
        assertThat(methodDeletion.getChildren(), is(Collections.EMPTY_LIST));
        assertThat(methodDeletion.getLeft().getName(), is("method(int)"));
        assertThat(methodDeletion.getRight(), is(nullValue()));
    }

    @Test
    public void changedMethodShouldBeFound() throws Exception {
        createLeftStructureTree("public class Foo { void method(int a) { a = 24; } }");
        createRightStructureTree("public class Foo { void method(int a) { a = 21; } }");
        createDifferences();
        assertThat(fDiffs.getChildren().size(), is(1));
        StructureDiffNode methodChange = fDiffs.getChildren().get(0).getChildren().get(0);
        assertThat(methodChange.getDiffType(), is(DiffType.CHANGE));
        assertThat(methodChange.getChildren(), is(Collections.EMPTY_LIST));
        assertThat(methodChange.getLeft().getName(), is("method(int)"));
        assertThat(methodChange.getRight().getName(), is("method(int)"));
    }

    @Test
    public void addedClassShouldBeFound() throws Exception {
        createLeftStructureTree("public class Foo {  }");
        createRightStructureTree("public class Foo { class Bar {} }");
        createDifferences();
        assertThat(fDiffs.getChildren().size(), is(1));
        StructureDiffNode classAddition = fDiffs.getChildren().get(0).getChildren().get(0);
        assertThat(classAddition.getDiffType(), is(DiffType.ADDITION));
        assertThat(classAddition.getChildren(), is(Collections.EMPTY_LIST));
        assertThat(classAddition.getLeft(), is(nullValue()));
        assertThat(classAddition.getRight().getName(), is("Bar"));
    }

    @Test
    public void deletedClassShouldBeFound() throws Exception {
        createLeftStructureTree("public class Foo { class Bar {} }");
        createRightStructureTree("public class Foo {  }");
        createDifferences();
        assertThat(fDiffs.getChildren().size(), is(1));
        StructureDiffNode classAddition = fDiffs.getChildren().get(0).getChildren().get(0);
        assertThat(classAddition.getDiffType(), is(DiffType.DELETION));
        assertThat(classAddition.getChildren(), is(Collections.EMPTY_LIST));
        assertThat(classAddition.getLeft().getName(), is("Bar"));
        assertThat(classAddition.getRight(), is(nullValue()));
    }

    @Test
    public void addedFieldShouldBeFound() throws Exception {
        createLeftStructureTree("public class Foo {  }");
        createRightStructureTree("public class Foo { private String fName; }");
        createDifferences();
        assertThat(fDiffs.getChildren().size(), is(1));
        StructureDiffNode classAddition = fDiffs.getChildren().get(0).getChildren().get(0);
        assertThat(classAddition.getDiffType(), is(DiffType.ADDITION));
        assertThat(classAddition.getChildren(), is(Collections.EMPTY_LIST));
        assertThat(classAddition.getLeft(), is(nullValue()));
        assertThat(classAddition.getRight().getName(), is("fName : String"));
    }

    @Test
    public void deletedFieldShouldBeFound() throws Exception {
        createLeftStructureTree("public class Foo { private String fName; }");
        createRightStructureTree("public class Foo {  }");
        createDifferences();
        assertThat(fDiffs.getChildren().size(), is(1));
        StructureDiffNode classAddition = fDiffs.getChildren().get(0).getChildren().get(0);
        assertThat(classAddition.getDiffType(), is(DiffType.DELETION));
        assertThat(classAddition.getChildren(), is(Collections.EMPTY_LIST));
        assertThat(classAddition.getLeft().getName(), is("fName : String"));
        assertThat(classAddition.getRight(), is(nullValue()));
    }

    @Test
    public void changedMethodInInnerClassShouldBeFound() throws Exception {
        createLeftStructureTree("public class Foo { class Bar { void method() {} } }");
        createRightStructureTree("public class Foo { class Bar { void method() { int a = 12; } } }");
        createDifferences();
        StructureDiffNode changedMethod = fDiffs.getChildren().get(0).getChildren().get(0).getChildren().get(0);
        assertThat(changedMethod.getDiffType(), is(DiffType.CHANGE));
        assertThat(changedMethod.getChildren(), is(Collections.EMPTY_LIST));
        assertThat(changedMethod.getLeft().getName(), is("method()"));
        assertThat(changedMethod.getRight().getName(), is("method()"));
    }

    private JavaStructureNode createStructureTree(String source) {
        JavaCompilation compilation = CompilationUtils.compileSource(source);
        CompilationUnitDeclaration cu = compilation.getCompilationUnit();
        JavaStructureNode root = new JavaStructureNode(Type.CU, null, null, cu);
        cu.traverse(new JavaStructureTreeBuilder(root), (CompilationUnitScope) null);
        return root;
    }

    private void createDifferences() {
        StructureDifferencer differencer = new StructureDifferencer();
        differencer.extractDifferences(fLeft, fRight);
        fDiffs = differencer.getDifferences();
    }

    private void createLeftStructureTree(String source) {
        fLeft = createStructureTree(source);
    }

    private void createRightStructureTree(String source) {
        fRight = createStructureTree(source);
    }
}
