package ch.uzh.ifi.seal.changedistiller.model.classifiers;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + fEnd;
		result = prime * result + fStart;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SourceRange other = (SourceRange) obj;
		if (fEnd != other.fEnd)
			return false;
		if (fStart != other.fStart)
			return false;
		return true;
	}
}
