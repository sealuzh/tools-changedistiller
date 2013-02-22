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

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.junit.BeforeClass;

import ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaDeclarationConverter;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaMethodBodyConverter;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.EntityType;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.SourceRange;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import ch.uzh.ifi.seal.changedistiller.util.CompilationUtils;

public abstract class WhenASTsAreConverted extends JavaDistillerTestCase {

    protected static JavaDeclarationConverter sDeclarationConverter;
    protected static JavaMethodBodyConverter sMethodBodyConverter;

    protected String fSnippet;
    protected JavaCompilation fCompilation;
    protected Node fRoot;

    @BeforeClass
    public static void initialize() throws Exception {
        sDeclarationConverter = sInjector.getInstance(JavaDeclarationConverter.class);
        sMethodBodyConverter = sInjector.getInstance(JavaMethodBodyConverter.class);
    }

    protected void prepareCompilation() {
        fCompilation = CompilationUtils.compileSource(getSourceCodeWithSnippets(fSnippet));
    }

    protected abstract String getSourceCodeWithSnippets(String... sourceSnippets);

    protected String getTreeString() {
        return fRoot.print(new StringBuilder()).toString();
    }

    protected Node getFirstLeaf() {
        return ((Node) fRoot.getFirstLeaf());
    }

    protected Node getFirstChild() {
        return (Node) fRoot.getFirstChild();
    }

    protected Node getLastChild() {
        return (Node) fRoot.getLastChild();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected void assertThat(Object actual, Matcher matcher) {
        MatcherAssert.assertThat(actual, matcher);
    }

    protected void createRootNode(EntityType label, String value) {
        fRoot = new Node(label, value);
        fRoot.setEntity(new SourceCodeEntity(value, label, new SourceRange()));
    }

}