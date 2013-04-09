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
import ch.uzh.ifi.seal.changedistiller.ast.java.CommentCleaner;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation;
import ch.uzh.ifi.seal.changedistiller.util.CompilationUtils;

public class WhenConsecutiveCommentsAreJoined {
	// see https://bitbucket.org/sealuzh/tools-changedistiller/issue/6
	private final static String LF = System.getProperty("line.separator");

    private static JavaCompilation sCompilationUnit;
    private static List<Comment> sComments;

    @BeforeClass
    public static void prepareCompilationUnit() throws Exception {
        sCompilationUnit = CompilationUtils.compileFile("src_comments/ClassWithConsecutiveComments.java");
        List<Comment> comments = CompilationUtils.extractComments(sCompilationUnit);
        CommentCleaner visitor = new CommentCleaner(sCompilationUnit.getSource());
        for (Comment comment : comments) {
            visitor.process(comment);
        }
        sComments = visitor.getComments();
    }

    @Test
    public void consecutiveLineCommentsShouldBeJoined() throws Exception {
        assertThat(getCommentString(sComments.get(0)), is("// a simple method invocation" + LF + "        // simple indeed"));
        assertThat(sComments.get(0).getComment(), is("// a simple method invocation" + LF + "        // simple indeed"));
    }

    @Test
    public void consecutiveBlockCommentsShouldNotBeJoined() throws Exception {
        assertThat(getCommentString(sComments.get(1)), is("/* first block comment */"));
        assertThat(sComments.get(1).getComment(), is("/* first block comment */"));
        assertThat(getCommentString(sComments.get(2)), is("/* second block comment */"));
        assertThat(sComments.get(2).getComment(), is("/* second block comment */"));
    }

    @Test
    public void consecutiveBlockAndLineCommentsShouldNotBeJoined() throws Exception {
        assertThat(getCommentString(sComments.get(3)), is("/* no more methods */"));
        assertThat(sComments.get(3).getComment(), is("/* no more methods */"));
        assertThat(getCommentString(sComments.get(4)), is("// no more line comments"));
        assertThat(sComments.get(4).getComment(), is("// no more line comments"));
    }

    @Test
    public void deadCodeShouldBeRemoved() throws Exception {
        assertThat(sComments.size(), is(5));
    }

    public String getCommentString(Comment comment) {
        return sCompilationUnit.getSource().substring(comment.sourceStart(), comment.sourceEnd());
    }

}
