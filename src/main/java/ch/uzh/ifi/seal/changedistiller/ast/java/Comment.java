package ch.uzh.ifi.seal.changedistiller.ast.java;

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

import org.eclipse.jdt.internal.compiler.ast.ASTNode;

/**
 * AST node representing any kind of comment.
 * 
 * @author Beat Fluri
 */
public class Comment extends ASTNode {

    private String fComment;
    private CommentType fType;

    /**
     * Creates a new comment.
     * 
     * @param type
     *            of the comment
     * @param sourceStart
     *            in the source file
     * @param sourceEnd
     *            in the source file
     * @param comment
     *            as text
     */
    public Comment(CommentType type, int sourceStart, int sourceEnd, String comment) {
        fType = type;
        this.sourceStart = sourceStart;
        this.sourceEnd = sourceEnd;
        fComment = comment;
    }

    @Override
    public StringBuffer print(int indent, StringBuffer output) {
        printIndent(indent, output);
        output.append(fComment);
        return output;
    }

    public CommentType getType() {
        return fType;
    }

    public String getComment() {
        return fComment;
    }

    /**
     * Type of comments that java provides.
     * 
     * @author Beat Fluri
     */
    public enum CommentType {
        LINE_COMMENT,
        BLOCK_COMMENT,
        JAVA_DOC
    }

    public int getLength() {
        return sourceEnd() - sourceStart();
    }

    public void setComment(String comment) {
        fComment = comment;
    }

    public boolean isLineComment() {
        return getType() == CommentType.LINE_COMMENT;
    }

    public boolean isJavadocComment() {
        return getType() == CommentType.JAVA_DOC;
    }

}
