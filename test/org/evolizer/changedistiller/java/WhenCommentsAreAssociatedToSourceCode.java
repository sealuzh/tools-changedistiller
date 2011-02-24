package org.evolizer.changedistiller.java;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Enumeration;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.evolizer.changedistiller.model.classifiers.EntityType;
import org.evolizer.changedistiller.treedifferencing.Node;
import org.evolizer.changedistiller.util.Compilation;
import org.evolizer.changedistiller.util.CompilationUtils;
import org.junit.BeforeClass;
import org.junit.Test;

public class WhenCommentsAreAssociatedToSourceCode {

    private static Compilation sCompilation;
    private static List<Comment> sComments;
    private static Node sRoot;

    @BeforeClass
    public static void prepareCompilationUnit() throws Exception {
        sCompilation = CompilationUtils.compileFile("ClassWithCommentsToAssociate.java");
        List<Comment> comments = CompilationUtils.extractComments(sCompilation);
        CommentCleaner visitor = new CommentCleaner(sCompilation.getSource());
        for (Comment comment : comments) {
            visitor.process(comment);
        }
        sComments = visitor.getComments();
        sRoot = new Node(EntityType.METHOD, "foo", null);
        AbstractMethodDeclaration method = CompilationUtils.findMethod(sCompilation.getCompilationUnit(), "foo");
        JavaASTBodyTransformer bodyT =
                new JavaASTBodyTransformer(sRoot, method, sComments, sCompilation.getScanner(), new JavaASTHelper());
        method.traverse(bodyT, (ClassScope) null);
    }

    @Test
    public void proximityRatingShouldAssociateCommentToClosestEntity() throws Exception {
        Node node = findNode("boolean check = (number > 0);");
        assertCorrectAssociation(node, "// check if number is greater than -1");
    }

    @Test
    public void undecidedProximityRatingShouldAssociateCommentToNextEntity() throws Exception {
        Node node = findNode("check");
        assertCorrectAssociation(node, "// check the interesting number\n        // and some new else");
    }

    @Test
    public void commentInsideBlockShouldBeAssociatedInside() throws Exception {
        Node node = findNode("a = (23 + Integer.parseInt(\"42\"));");
        assertCorrectAssociation(node, "/* A block comment\n             * with stars\n             */");
        node = findNode("b = Math.abs(number);");
        assertCorrectAssociation(node, "/* inside else */");
    }

    @Test
    public void commentInsideSimpleStatementShouldBeAssociatedToThatStatement() throws Exception {
        Node node = findNode("b = Math.round(Math.random());");
        assertCorrectAssociation(node, "/* inner comment */");
    }

    private void assertCorrectAssociation(Node node, String expectedComment) {
        List<Node> associatedNodes = node.getAssociatedNodes();
        assertThat(associatedNodes.size(), is(1));
        assertThat(associatedNodes.get(0).getValue(), is(expectedComment));
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
