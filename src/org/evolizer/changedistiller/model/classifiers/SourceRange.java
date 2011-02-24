package org.evolizer.changedistiller.model.classifiers;

/**
 * Representation of a range in a document with offset and length.
 * 
 * @author fluri, zubi
 * 
 */
public final class SourceRange {

    private int fStart = -1;
    private int fEnd = -1;

    /**
     * Create a new {@link SourceRange}.
     * 
     * @param offset
     *            in the document
     * @param length
     *            of the range
     */
    public SourceRange(int offset, int length) {
        fStart = offset;
        fEnd = length;
    }

    /**
     * Creates new range with offset and length set to -1.
     */
    public SourceRange() {}

    public int getStart() {
        return fStart;
    }

    public int getEnd() {
        return fEnd;
    }

    public void setStart(int start) {
        fStart = start;
    }

    public void setEnd(int end) {
        fEnd = end;
    }

    @Override
    public String toString() {
        return "(" + fStart + "," + fEnd + ")";
    }

}
