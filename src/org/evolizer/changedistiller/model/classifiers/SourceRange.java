package org.evolizer.changedistiller.model.classifiers;

/**
 * Representation of a range in a document with start and end.
 * 
 * @author Beat Fluri
 * @author zubi
 * 
 */
public final class SourceRange {

    private int fStart;
    private int fEnd;

    /**
     * Create a new {@link SourceRange}.
     * 
     * @param start
     *            in the document
     * @param end
     *            in the document
     */
    public SourceRange(int start, int end) {
        fStart = start;
        fEnd = end;
    }

    /**
     * Creates new range with start and end set to -1.
     */
    public SourceRange() {
        fStart = -1;
        fEnd = -1;
    }

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
