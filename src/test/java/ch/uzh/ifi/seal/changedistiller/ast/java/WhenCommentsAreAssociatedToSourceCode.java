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

import java.util.Enumeration;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.uzh.ifi.seal.changedistiller.ast.java.Comment;
import ch.uzh.ifi.seal.changedistiller.ast.java.CommentCleaner;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaCompilation;
import ch.uzh.ifi.seal.changedistiller.ast.java.JavaMethodBodyConverter;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.EntityType;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.SourceRange;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.Node;
import ch.uzh.ifi.seal.changedistiller.util.CompilationUtils;

public class WhenCommentsAreAssociatedToSourceCode extends JavaDistillerTestCase {
	// see https://bitbucket.org/sealuzh/tools-changedistiller/issue/6
	private final static String LF = System.getProperty("line.separator");
	
    private static JavaCompilation sCompilation;
    private static List<Comment> sComments;
    private static Node sRoot;

    @BeforeClass
    public static void prepareCompilationUnit() throws Exception {
        sCompilation = CompilationUtils.compileFile("src_comments/ClassWithCommentsToAssociate.java");
        List<Comment> comments = CompilationUtils.extractComments(sCompilation);
        CommentCleaner visitor = new CommentCleaner(sCompilation.getSource());
        for (Comment comment : comments) {
            visitor.process(comment);
        }
        sComments = visitor.getComments();
        sRoot = new Node(JavaEntityType.METHOD, "foo");
        sRoot.setEntity(new SourceCodeEntity("foo", JavaEntityType.METHOD, new SourceRange()));
        AbstractMethodDeclaration method = CompilationUtils.findMethod(sCompilation.getCompilationUnit(), "foo");
        JavaMethodBodyConverter bodyT = sInjector.getInstance(JavaMethodBodyConverter.class);
        bodyT.initialize(sRoot, method, sComments, sCompilation.getScanner());
        method.traverse(bodyT, (ClassScope) null);
    }

    @Test
    public void proximityRatingShouldAssociateCommentToClosestEntity() throws Exception {
        Node node = findNode("boolean check = (number > 0);");
        assertCorrectAssociation(node, "// check if number is greater than -1", JavaEntityType.LINE_COMMENT);
    }

    @Test
    public void undecidedProximityRatingShouldAssociateCommentToNextEntity() throws Exception {
        Node node = findNode("check");
        assertCorrectAssociation(
                node,
                "// check the interesting number" + LF + "        // and some new else",
                JavaEntityType.LINE_COMMENT);
    }

    @Test
    public void commentInsideBlockShouldBeAssociatedInside() throws Exception {
        Node node = findNode("a = (23 + Integer.parseInt(\"42\"));");
        assertCorrectAssociation(
                node,
                "/* A block comment" + LF + "             * with stars" + LF + "             */",
                JavaEntityType.BLOCK_COMMENT);
        node = findNode("b = Math.abs(number);");
        assertCorrectAssociation(node, "/* inside else */", JavaEntityType.BLOCK_COMMENT);
    }

    @Test
    public void commentInsideSimpleStatementShouldBeAssociatedToThatStatement() throws Exception {
        Node node = findNode("b = Math.round(Math.random());");
        assertCorrectAssociation(node, "/* inner comment */", JavaEntityType.BLOCK_COMMENT);
    }

    private void assertCorrectAssociation(Node node, String expectedComment, EntityType expectedCommentType) {
        List<Node> associatedNodes = node.getAssociatedNodes();
        assertThat(associatedNodes.size(), is(1));
        assertThat(associatedNodes.get(0).getValue(), is(expectedComment));
        assertThat(associatedNodes.get(0).getLabel(), is(expectedCommentType));
        assertThat(associatedNodes.get(0).getAssociatedNodes().get(0), is(node));
    }

    @SuppressWarnings("unchecked")
    private Node findNode(String value) {
        for (Enumeration<Node> e = sRoot.breadthFirstEnumeration(); e.hasMoreElements();) {
            Node node = e.nextElement();
            if (node.getValue().equals(value)) {
                return node;
            }
        }
        return null;
    }

}
