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

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import ch.uzh.ifi.seal.changedistiller.ast.java.Comment;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation;
import ch.uzh.ifi.seal.changedistiller.util.CompilationUtils;

/**
 * Test case testing comment extraction for a compilation unit.
 * 
 * @author Beat Fluri
 */
public class WhenCommentsAreExtracted {
	// see https://bitbucket.org/sealuzh/tools-changedistiller/issue/6
	private final static String LF = System.getProperty("line.separator");

    private static JavaCompilation sCompilationUnit;

    @BeforeClass
    public static void prepareCompilationUnit() throws Exception {
        sCompilationUnit = CompilationUtils.compileFile("src_comments/ClassWithComments.java");
    }

    @Test
    public void compilationUnitOfClassWithCommentsShouldHaveComments() throws Exception {
        List<Comment> comments = CompilationUtils.extractComments(sCompilationUnit);
        assertThat(comments.size(), is(3));
        assertThat(comments.get(0).getComment(), is("/**" + LF + " * A class with comments." + LF + " *" + LF + " * @author Beat Fluri" + LF + " */"));
        assertThat(comments.get(1).getComment(), is("// a simple method invocation"));
        assertThat(comments.get(2).getComment(), is("/* no more methods */"));
    }

}
