package ch.uzh.ifi.seal.changedistiller.treedifferencing;

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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ch.uzh.ifi.seal.changedistiller.model.classifiers.EntityType;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.matching.MatchingFactory;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.matching.measure.TokenBasedCalculator;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.operation.DeleteOperation;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.operation.InsertOperation;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.operation.MoveOperation;
import ch.uzh.ifi.seal.changedistiller.treedifferencing.operation.UpdateOperation;

/**
 * Implementation of the core tree differencing algorithm of Chawathe. This algorithm takes two {@link Node} trees
 * generates a matching between the nodes of both trees and calculates an edit script of {@link TreeEditOperation} that
 * transform the left into the right tree.
 * <p>
 * The implementation style is not clean, i.e., it uses single character method and variable names, because the
 * implementation should reflect the original algorithm from the paper as much as possible.
 * 
 * @author Beat Fluri
 * @see TreeEditOperation
 * @see Node
 */
public class TreeDifferencer {

    private static final int UP = 1;
    private static final int LEFT = 2;
    private static final int DIAG = 3;

    private Set<NodePair> fMatch;
    private HashMap<Node, Node> fLeftToRightMatch;
    private HashMap<Node, Node> fRightToLeftMatch;

    private HashSet<NodePair> fMatchPrime;
    private HashMap<Node, Node> fLeftToRightMatchPrime;
    private HashMap<Node, Node> fRightToLeftMatchPrime;

    private List<TreeEditOperation> fEditScript;

    /**
     * Calculates the edit script of {@link TreeEditOperation} between the left and the right {@link Node} trees.
     * 
     * @param left
     *            tree to calculate the edit script for
     * @param right
     *            tree to calculate the edit script for
     */
    public void calculateEditScript(Node left, Node right) {
        fMatch = new HashSet<NodePair>();

        TreeMatcher dnm = MatchingFactory.getMatcher(fMatch);
        dnm.match(left, right);
        fLeftToRightMatch = new HashMap<Node, Node>();
        fRightToLeftMatch = new HashMap<Node, Node>();
        for (NodePair p : fMatch) {
            fLeftToRightMatch.put(p.getLeft(), p.getRight());
            fRightToLeftMatch.put(p.getRight(), p.getLeft());
        }
        editScript(left, right);
    }

    public List<TreeEditOperation> getEditScript() {
        return fEditScript;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void editScript(Node left, Node right) {
        // 1.
        // E <- {}
        fEditScript = new LinkedList<TreeEditOperation>();

        // M' <- M
        fMatchPrime = new HashSet(fMatch);
        fLeftToRightMatchPrime = fLeftToRightMatch;
        fRightToLeftMatchPrime = fRightToLeftMatch;

        // 2.
        // Visit the nodes in T2 in breath-first order
        Enumeration breathFirst = right.breadthFirstEnumeration();
        // skip MethodDeclaration
        while (breathFirst.hasMoreElements()) {

            // (a)
            // Let x be the current node in the breath-first search T2
            Node /* T2 */x = (Node) breathFirst.nextElement();

            // Let y = p(x)
            Node /* T2 */y = (Node) x.getParent();

            // Let z be the partner of y in M' (*)
            Node /* T1 */z = fRightToLeftMatchPrime.get(y);
            Node /* T1 */w = fRightToLeftMatchPrime.get(x);

            // (b) If x has no partner in M'
            if (fRightToLeftMatchPrime.get(x) == null) {
                // i. k <- FindPos(x)
                int k = findPosition(x);

                // ii. Append INS((w, a, v(x)), z, k) to E, for a new identifier w.
                w = (Node) x.clone();
                w.enableMatched();
                x.enableMatched();
                TreeEditOperation insert = new InsertOperation(w, z, k);
                fEditScript.add(insert);

                // iii. Add (w, x) to M' and apply INS((w, a, v(x)), z, k) to T1
                addMatchToPrimes(x, w);
                insert.apply();

                // (c) else if x is not a root (x has a partner in M')
            } else if (!x.isRoot()) {
                // i.
                // Let w be the partner of x in M'
                /* T1 */w = fRightToLeftMatchPrime.get(x);
                // Let v = p(w) in T1
                Node /* T1 */v = (Node) w.getParent();

                // ii. If v(w) != v(x)
                boolean equals = true;
                if (l(w).isComment()) {
                    TokenBasedCalculator tbc = new TokenBasedCalculator();
                    double sim = tbc.calculateSimilarity(v(w), v(x));
                    equals = sim == 1.0;
                } else {
                    equals = v(w).equals(v(x));
                }
                if (!equals) {
                    // A. Append UPD(w, v(x)) to E
                    TreeEditOperation update = new UpdateOperation(w, x, v(x));
                    fEditScript.add(update);
                    // B. Apply UPD(w, v(x)) to T1
                    update.apply();
                }
                // iii. If (y, v) not in M'
                if (!matchContains(v, y, fMatchPrime)) {
                    // A. Let z be the partner of y in M'
                    // Node z /*T1*/= (Node) fRightToLeftMatchPrime.get(y); already executed
                    // B. k <- FindPos(x)
                    int k = findPosition(x);
                    // C. Append MOV(w, z, k) to E
                    TreeEditOperation move = new MoveOperation(w, x, z, k);
                    fEditScript.add(move);
                    // D. Apply MOV(w, z, k) to T1
                    move.apply();
                }
            }
            // (d) AlignChildren(w, x)
            if (!w.isLeaf()) {
                alignChildren(w, x);
            }
        }

        // 3. Do a post-order traversal of T1 (this is the delete phase)
        LinkedList<TreeEditOperation> dels = new LinkedList<TreeEditOperation>();
        for (Enumeration postOrder = left.postorderEnumeration(); postOrder.hasMoreElements();) {
            // (a) Let w be the current node in the post-order traversal of T1
            Node w = (Node) postOrder.nextElement();
            // (b) If w has no partner in M'
            if (fLeftToRightMatchPrime.get(w) == null) {
                // Append DEL(w) to E
                TreeEditOperation delete = new DeleteOperation(w);
                fEditScript.add(delete);
                dels.add(delete);
            }
        }
        // Apply DEL(w) to T1
        /*
         * for (ITreeEditOperation o : dels) { o.apply(); }
         */
        // 4. E is a minimum cost edit script, M' is a total matching, and T1 is isomorphic to T2
    }

    private void alignChildren(Node w, Node x) {
        if (w.isLeaf() || x.isLeaf()) {
            return;
        }

        // 1. Mark all children of w and all children f x "out of order"
        markChildrenOutOfOrder(w);
        markChildrenOutOfOrder(x);

        // 2.
        // Let S1 be the sequence of children of w whose partners are children of x
        List<Node> sOne = createChildrenSequence(w, x, fLeftToRightMatchPrime);
        // Let S2 be the sequence of children of x whose partners are children of w
        List<Node> sTwo = createChildrenSequence(x, w, fRightToLeftMatchPrime);

        // 3. Define the function equal(a, b) to be true if and only if (a, b) in M'
        // 4. Let S <- LCS(S1, S2, equal)
        HashSet<NodePair> s = longestCommonSubsequence(sOne, sTwo);

        // 5. For each (a, b) in S, mark nodes a and b "in order"
        for (NodePair p : s) {
            p.getLeft().enableInOrder();
            p.getRight().enableInOrder();
        }

        // 6. For each a in S1, b in S2 such that (a, b) in M but (a, b) not in S
        for (Node a : sOne) {
            if (!a.isInOrder()) { // a not in S
                for (Node b : sTwo) {
                    if (!b.isInOrder() && matchContains(a, b, fMatch)) { // b not in S and (a, b) in M
                        // (a) k <- FindPos(b)
                        int k = findPosition(b);
                        // (b)
                        // Append MOV(a, w, k) to E
                        TreeEditOperation move = new MoveOperation(a, b, w, k);
                        fEditScript.add(move);
                        // Apply MOV(a, w, k) to T1
                        move.apply();
                        // (c) Mark a and b "in order"
                        a.enableInOrder();
                        b.enableInOrder();
                    }
                }
            }
        }
    }

    /**
     * Sequence of children of node whose partners are children of x
     */
    @SuppressWarnings("rawtypes")
    private List<Node> createChildrenSequence(Node node, Node x, HashMap<Node, Node> match) {
        LinkedList<Node> result = new LinkedList<Node>();

        for (Enumeration e = node.children(); e.hasMoreElements();) {
            Node n = (Node) e.nextElement();
            Node v = match.get(n);
            if ((v != null) && (v.getParent() == x)) {
                result.add(n);
            }
        }

        return result;
    }

    @SuppressWarnings("rawtypes")
    private void markChildrenOutOfOrder(Node node) {
        for (Enumeration e = node.children(); e.hasMoreElements();) {
            ((Node) e.nextElement()).enableOutOfOrder();
        }
    }

    private boolean matchContains(Node v, Node y, Set<NodePair> match) {
        for (NodePair p : match) {
            if (((p.getLeft() == v) && (p.getRight() == y)) || ((p.getLeft() == y) && (p.getRight() == v))) {
                return true;
            }
        }
        return false;
    }

    private String v(Node node) {
        return node.getValue();
    }

    private EntityType l(Node node) {
        return node.getLabel();
    }

    private void addMatchToPrimes(Node x /* T1 */, Node w /* T2 */) {
        fMatchPrime.add(new NodePair(w, x));
        fLeftToRightMatchPrime.put(w, x);
        fRightToLeftMatchPrime.put(x, w);
    }

    private int findPosition(Node node) {
        // 1. Let y = p(x) in T2
        // [and let w be the partner of x (x in T1)] makes no sense
        // Node y = (Node) node.getParent();
        /*
         * //2. if (node == y.getFirstChild() && node.isInOrder()) { return 0; }
         */

        // 2. If x is the leftmost child of y that is marked "in order" return 1
        // 3. Find v in T2 where v is the rightmost sibling of x that is to the
        // left of x and is marked "in order"
        // combining both steps
        Node v = (Node) node.getPreviousSibling();
        while ((v != null) && !v.isInOrder()) {
            v = (Node) v.getPreviousSibling();
        }

        // x is the leftmost child of y that is marked "in order"
        if (v == null) {
            return 0;
        }

        // 4. Let u be the partner of v in T1 (*)
        Node u = fRightToLeftMatchPrime.get(v);
        if (u == null) {
            System.out.println("ERROR: partner expected (findPosition)");
        }

        // 5. Suppose u is the ith child of its parent
        // (counting from left to right) that is marked "in order"
        // return i+1
        Node p = (Node) u.getParent();
        int count = 0;
        for (int i = 0; i < p.getIndex(u); i++) {
            Node h = (Node) p.getChildAt(i);
            if (h.isInOrder()) {
                count++;
            }
        }

        return count + 1;
    }

    private HashSet<NodePair> longestCommonSubsequence(List<Node> left, List<Node> right) {
        int m = left.size();
        int n = right.size();

        int[][] c = new int[m + 1][n + 1];
        int[][] b = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            c[i][0] = 0;
            b[i][0] = 0;
        }
        for (int i = 0; i <= n; i++) {
            c[0][i] = 0;
            b[0][i] = 0;
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (matchContains(left.get(i - 1), right.get(j - 1), fMatchPrime)) {
                    c[i][j] = c[i - 1][j - 1] + 1;
                    b[i][j] = DIAG;
                } else if (c[i - 1][j] >= c[i][j - 1]) {
                    c[i][j] = c[i - 1][j];
                    b[i][j] = UP;
                } else {
                    c[i][j] = c[i][j - 1];
                    b[i][j] = LEFT;
                }
            }
        }
        HashSet<NodePair> result = new HashSet<NodePair>();
        extractLCS(b, left, right, m, n, result);
        return result;
    }

    private void extractLCS(int[][] b, List<Node> l, List<Node> r, int i, int j, HashSet<NodePair> lcs) {
        if ((i != 0) && (j != 0)) {
            if (b[i][j] == DIAG) {
                lcs.add(new NodePair(l.get(i - 1), r.get(j - 1)));
                extractLCS(b, l, r, i - 1, j - 1, lcs);
            } else if (b[i][j] == UP) {
                extractLCS(b, l, r, i - 1, j, lcs);
            } else {
                extractLCS(b, l, r, i, j - 1, lcs);
            }
        }
    }
}
