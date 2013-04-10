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

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Calculates structure differences between two trees of {@link StructureNode}s.
 * 
 * @author Beat Fluri
 */
public class StructureDifferencer {

    private StructureDiffNode fDifferences;

    /**
     * Types of differences.
     * 
     * @author Beat Fluri
     */
    public enum DiffType {
        ADDITION,
        DELETION,
        CHANGE,
        NO_CHANGE
    }

    // this code is inspired by org.eclipse.compare.structureMergeViewer.Differencer
    /**
     * Finds and returns the structure differences between a left and right {@link StructureNode} tree.
     * 
     * @param left
     *            to compare with right
     * @param right
     *            to with left
     */
    public void extractDifferences(StructureNode left, StructureNode right) {
        if ((left == null) && (right == null)) {
            return;
        }
        fDifferences = traverse(left, right);
    }

    private StructureDiffNode traverse(StructureNode left, StructureNode right) {
        StructureNode[] leftChildren = getChildren(left);
        StructureNode[] rightChildren = getChildren(right);
        StructureDiffNode root = new StructureDiffNode(left, right);
        if ((leftChildren != null) && (rightChildren != null)) {
            root = traverseChildren(root, leftChildren, rightChildren);
        } else {
            root = extractLeaveChange(root, left, right);
        }
        if (hasChanges(root)) {
            return root;
        }
        return null;
    }

    private StructureDiffNode extractLeaveChange(StructureDiffNode root, StructureNode left, StructureNode right) {
        if (left == null) {
            if (right != null) {
                root.setLeft(null);
                root.setRight(right);
                root.setDiffType(DiffType.ADDITION);
            } else {
                assert (false);
            }
        } else if (right == null) {
            root.setLeft(left);
            root.setRight(null);
            root.setDiffType(DiffType.DELETION);
        } else {
            if (!contentsEqual(left, right)) {
                root.setLeft(left);
                root.setRight(right);
                root.setDiffType(DiffType.CHANGE);
            } else {
                return null;
            }
        }
        return root;
    }

    private StructureDiffNode traverseChildren(
            StructureDiffNode root,
            StructureNode[] leftChildren,
            StructureNode[] rightChildren) {
        Set<StructureNode> allSet = new HashSet<StructureNode>(20);
        Map<StructureNode, StructureNode> leftSet = new HashMap<StructureNode, StructureNode>(10);
        Map<StructureNode, StructureNode> rightSet = new HashMap<StructureNode, StructureNode>(10);
        for (StructureNode node : leftChildren) {
            allSet.add(node);
            leftSet.put(node, node);
        }
        for (StructureNode node : rightChildren) {
            allSet.add(node);
            rightSet.put(node, node);
        }
        for (StructureNode node : allSet) {
            StructureNode leftChild = leftSet.get(node);
            StructureNode rightChild = rightSet.get(node);
            StructureDiffNode diff = traverse(leftChild, rightChild);
            if (diff != null) {
                root.addChild(diff);
            }
        }
        return root;
    }

    private boolean hasChanges(StructureDiffNode root) {
        return (root != null) && (!root.getChildren().isEmpty() || (root.getDiffType() != DiffType.NO_CHANGE));
    }

    private boolean contentsEqual(StructureNode left, StructureNode right) {
        if ((left.getContent() == null) && (right.getContent() == null)) {
            return true;
        }
        StringReader leftContent = getStream(left);
        StringReader rightContent = getStream(right);
        try {
            if ((leftContent == null) || (rightContent == null)) {
                return false;
            }
            while (true) {
                int l = leftContent.read();
                int r = rightContent.read();
                if ((l == -1) && (r == -1)) {
                    return true;
                }
                if (l != r) {
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
        	if(leftContent != null) { // shouldn't happen - checked to calm sonar
        		leftContent.close();
        	}
        	
        	if(rightContent != null) { // shouldn't happen - checked to calm sonar        		
        		rightContent.close();
        	}
        }
        return false;
    }

    private StringReader getStream(StructureNode left) {
        String content = left.getContent();
        if (content != null) {
            return new StringReader(content);
        }
        return null;
    }

    private StructureNode[] getChildren(StructureNode node) {
        if ((node != null) && !node.getChildren().isEmpty()) {
            List<? extends StructureNode> nodes = node.getChildren();
            return nodes.toArray(new StructureNode[nodes.size()]);
        }
        return null;
    }

    public StructureDiffNode getDifferences() {
        return fDifferences;
    }
}
