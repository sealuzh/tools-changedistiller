package org.evolizer.changedistiller.model.classifiers;

/**
 * Representation of a range in a document with offset and length.
 * 
 * @author fluri, zubi
 * 
 */
public final class SourceRange {

    private int fOffset = -1;
    private int fLength = -1;

    /**
     * Create a new {@link SourceRange}.
     * 
     * @param offset
     *            in the document
     * @param length
     *            of the range
     */
    public SourceRange(int offset, int length) {
        fOffset = offset;
        fLength = length;
    }

    /**
     * Creates new range with offset and length set to -1.
     */
    public SourceRange() {}

    /**
     * Returns the offset of this {@link SourceRange}.
     * 
     * @return offset of this range
     */
    public int getOffset() {
        return fOffset;
    }

    /**
     * Returns the length of this {@link SourceRange}.
     * 
     * @return length of this range
     */
    public int getLength() {
        return fLength;
    }

    /**
     * Sets offset of this {@link SourceRange}.
     * 
     * @param offset
     *            to set
     */
    public void setOffset(int offset) {
        fOffset = offset;
    }

    /**
     * Sets length of this {@link SourceRange}.
     * 
     * @param length
     *            to set
     */
    public void setLength(int length) {
        fLength = length;
    }

}
