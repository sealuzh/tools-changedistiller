package org.evolizer.changedistiller.structuredifferencing;

import java.util.LinkedList;
import java.util.List;

import org.evolizer.changedistiller.structuredifferencing.StructureDifferencer.DiffType;

/**
 * Node container for structure differences.
 * 
 * @author Beat Fluri
 */
public class StructureDiffNode {

    private List<StructureDiffNode> fChildren;
    private StructureNode fLeft;
    private StructureNode fRight;
    private DiffType fDiffType;

    /**
     * Creates a new structure diff node.
     */
    public StructureDiffNode() {
        fChildren = new LinkedList<StructureDiffNode>();
        fDiffType = DiffType.NO_CHANGE;
    }

    /**
     * Creates a new structure diff node.
     * 
     * @param left
     *            part of the diff
     * @param right
     *            part of the diff
     */
    public StructureDiffNode(StructureNode left, StructureNode right) {
        this();
        fLeft = left;
        fRight = right;
    }

    /**
     * Adds a child {@link StructureDiffNode} to this node.
     * 
     * @param child
     *            to add
     */
    public void addChild(StructureDiffNode child) {
        fChildren.add(child);
    }

    public void setDiffType(DiffType type) {
        fDiffType = type;
    }

    public void setRight(StructureNode right) {
        fRight = right;
    }

    public void setLeft(StructureNode left) {
        fLeft = left;
    }

    public List<StructureDiffNode> getChildren() {
        return fChildren;
    }

    public StructureNode getLeft() {
        return fLeft;
    }

    public StructureNode getRight() {
        return fRight;
    }

    public DiffType getDiffType() {
        return fDiffType;
    }

    public boolean isChanged() {
        return !(fDiffType == DiffType.NO_CHANGE);
    }

    /**
     * Returns whether or not the node has children.
     * 
     * @return <code>true</code> if the node has children, <code>false</code> othewise.
     */
    public boolean hasChildren() {
        return !fChildren.isEmpty();
    }

    /**
     * Returns whether or not the diff node is related to a class or interface.
     * 
     * @return <code>true</code> if the diff node is related to a class or interface, <code>false</code> otherwise
     */
    public boolean isClassOrInterfaceDiffNode() {
        if (fLeft != null) {
            return fLeft.isClassOrInterface();
        } else if (fRight != null) {
            return fRight.isClassOrInterface();
        }
        return false;
    }

    /**
     * Returns whether or not the diff node is related to a method or constructor.
     * 
     * @return <code>true</code> if the diff node is related to a method or constructor, <code>false</code> otherwise
     */
    public boolean isMethodOrConstructorDiffNode() {
        if (fLeft != null) {
            return fLeft.isMethodOrConstructor();
        } else if (fRight != null) {
            return fRight.isMethodOrConstructor();
        }
        return false;
    }

    /**
     * Returns whether or not the diff node is related to a field.
     * 
     * @return <code>true</code> if the diff node is related to a field, <code>false</code> otherwise
     */
    public boolean isFieldDiffNode() {
        if (fLeft != null) {
            return fLeft.isField();
        } else if (fRight != null) {
            return fRight.isField();
        }
        return false;
    }

    public boolean isAddition() {
        return fDiffType == DiffType.ADDITION;
    }

    public boolean isDeletion() {
        return fDiffType == DiffType.DELETION;
    }

}
