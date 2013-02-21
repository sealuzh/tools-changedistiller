package ch.uzh.ifi.seal.changedistiller.treedifferencing;

/**
 * Pair of leafs
 * <p>
 * As with {@link NodePair}, they are comparable according to a given similarity.
 * 
 * @author Beat Fluri
 * 
 */
public class LeafPair extends NodePair implements Comparable<LeafPair> {

    private double fSimilarity;

    /**
     * Creates a new leaf pair.
     * 
     * @param left
     *            leaf
     * @param right
     *            leaf
     */
    public LeafPair(Node left, Node right) {
        super(left, right);
    }

    /**
     * Creates a new leaf pair.
     * 
     * @param left
     *            leaf
     * @param right
     *            leaf
     * @param similarity
     *            similarity between the two leaves
     */
    public LeafPair(Node left, Node right, Double similarity) {
        super(left, right);
        fSimilarity = similarity;
    }

    public double getSimilarity() {
        return fSimilarity;
    }

    @Override
    public int compareTo(LeafPair other) {
        return -Double.compare(fSimilarity, other.getSimilarity());
    }

}
