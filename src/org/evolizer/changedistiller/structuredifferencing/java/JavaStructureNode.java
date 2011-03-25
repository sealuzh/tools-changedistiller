package org.evolizer.changedistiller.structuredifferencing.java;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.evolizer.changedistiller.structuredifferencing.StructureNode;

/**
 * Node for Java structure differencing.
 * 
 * @author Beat Fluri
 */
public class JavaStructureNode implements StructureNode {

    private Type fType;
    private String fName;
    private String fQualifier;
    private ASTNode fASTNode;
    private List<JavaStructureNode> fChildren;

    /**
     * Creates a new Java structure node
     * 
     * @param type
     *            of the node
     * @param qualifier
     *            of the node
     * @param name
     *            of the node
     * @param astNode
     *            representing the structure node
     */
    public JavaStructureNode(Type type, String qualifier, String name, ASTNode astNode) {
        fType = type;
        fQualifier = qualifier;
        fName = name;
        fASTNode = astNode;
        fChildren = new LinkedList<JavaStructureNode>();
    }

    /**
     * Adds a new {@link JavaStructureNode} child.
     * 
     * @param node
     *            to add as child
     */
    public void addChild(JavaStructureNode node) {
        fChildren.add(node);
    }

    @Override
    public List<JavaStructureNode> getChildren() {
        return fChildren;
    }

    @Override
    public String toString() {
        return fType.name() + ": " + fName;
    }

    /**
     * Java structure node types.
     * 
     * @author Beat Fluri
     */
    public enum Type {
        CU,
        FIELD,
        CONSTRUCTOR,
        METHOD,
        INTERFACE,
        CLASS,
        ANNOTATION,
        ENUM
    }

    public Type getType() {
        return fType;
    }

    public String getName() {
        return fName;
    }

    /**
     * Returns the fully qualified name of this node if the node has a qualifier. Otherwise, {@link #getName()} is
     * returned.
     * 
     * @return the fully qualified name of this node, if the node has a qualifier; name otherwise.
     */
    public String getFullyQualifiedName() {
        if (fQualifier != null) {
            return fQualifier + "." + fName;
        }
        return getName();
    }

    @Override
    public String getContent() {
        return fASTNode.toString();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if ((obj != null) && (getClass() == obj.getClass())) {
            JavaStructureNode other = (JavaStructureNode) obj;
            return (fType == other.getType()) && fName.equals(other.getName());
        }
        return super.equals(obj);
    }

}
