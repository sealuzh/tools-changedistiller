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

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.uzh.ifi.seal.changedistiller.ast.java.Comment.CommentType;

/**
 * Removes dead code comments and joins successive line comments.
 * 
 * @author Beat Fluri
 * 
 */
public class CommentCleaner {

    private static final String NON_NLS = "NON-NLS";
    private static Pattern sSourceCodePattern = Pattern
            .compile("(?s).*([aA-zZ0-9]*\\.)*[aA-zZ0-9]+?\\((?s).*?\\);(?s).*" + "| (?s).+=(?s).+?;(?s).*"
                    + "| (?s).*if\\s*\\((?s).*\\)\\s*\\{*\\s*(?s).*"
                    + "| (?s).*?try\\s*\\{(?s).*?\\}\\s*catch\\s*\\((?s).*?\\)\\s*\\{(?s).*?\\}(?s).*");

    private List<Comment> fComments;
    private Comment fPreviousComment;
    private Comment fVisitedComment;
    private boolean fClean;
    private int fCommentsCount;
    private String fSource;

    /**
     * Creates a new comment visitor.
     * 
     * @param source
     *            whos comments are visited
     */
    public CommentCleaner(String source) {
        fSource = source;
        fComments = new LinkedList<Comment>();
    }

    /**
     * Adjust the length of the of the visited node to include the given comment.
     * 
     * @param comment
     *            the comment that is added to the visited comment.
     */
    private void addToPreviousCommentBlock(Comment comment) {
        if (commentDoesNotContainNonNLS(comment)) {
            int newLength = comment.getLength();
            if (fVisitedComment != null) {
                newLength = newLength + (comment.sourceStart() - fVisitedComment.sourceStart());
                fVisitedComment.sourceStart = fVisitedComment.sourceStart();
                fVisitedComment.sourceEnd = fVisitedComment.sourceStart() + newLength;
                fVisitedComment
                        .setComment(fSource.substring(fVisitedComment.sourceStart(), fVisitedComment.sourceEnd()));
            } else {
                fVisitedComment = comment;
            }
            fPreviousComment = comment;
            fComments.add(fVisitedComment);
        }
    }

    /**
     * Returns the comments processed.
     * 
     * @return the comments processed
     */
    public List<Comment> getComments() {
        // fComments might still contain commented source code
        if (!fClean) {
            removeCommentedSourceCode();
        }
        return fComments;
    }

    public int getNumberOfCommentsProcessed() {
        return fCommentsCount;
    }

    /**
     * Processes the given {@link Comment}. If the previous comment was a line comment and the current is too, both are
     * counted as "in the same block". {@link Comment.CommentType#JAVA_DOC JAVA_DOC} comments are ignored.
     * 
     * @param comment
     *            to process
     */
    public void process(Comment comment) {
        if (comment.isJavadocComment()) {
            return;
        }
        fCommentsCount++;
        joinAndStoreConsecutiveLineComments(comment);
    }

    private void joinAndStoreConsecutiveLineComments(Comment comment) {
        prepareConsecutiveComments(comment);
        addToPreviousCommentBlock(comment);
    }

    private void prepareConsecutiveComments(Comment comment) {
        fVisitedComment = null;
        if (previousCommentExistsAndIsLineComment() && comment.isLineComment() && commentDoesNotContainNonNLS(comment)) {
            int positionAfterPreviousComment = fPreviousComment.sourceEnd() + 1;
            int length = comment.sourceStart() - positionAfterPreviousComment;
            // If only whitespace is between previous comment and current comment, the comments are in the same block
            if (hasWithspaceInSourcePart(positionAfterPreviousComment, length)) {
                fVisitedComment = fComments.remove(fComments.size() - 1);
            }
        }
    }

    private boolean commentDoesNotContainNonNLS(Comment comment) {
        return !getCommentString(comment).contains(NON_NLS);
    }

    private boolean hasWithspaceInSourcePart(int start, int length) {
        if (length < 0) {
            return true;
        }
        String sourcePart = fSource.substring(start, start + length);
        sourcePart = sourcePart.replaceAll("\\s", "");
        return sourcePart.isEmpty();
    }

    private boolean previousCommentExistsAndIsLineComment() {
        return (fPreviousComment != null) && (fPreviousComment.isLineComment());
    }

    private String getCommentString(Comment node) {
        return fSource.substring(node.sourceStart(), node.sourceEnd());
    }

    // Uses regex patterns to guess whether a comment is commented source code and - if this is the case - removes it
    // from the set of comments.
    private void removeCommentedSourceCode() {
        List<Comment> cleanComments = new LinkedList<Comment>();
        for (Comment comment : fComments) {
            Matcher matcher = sSourceCodePattern.matcher(getCommentString(comment));
            // Javadocs often contain source code examples
            if ((comment.getType() == CommentType.JAVA_DOC) || !matcher.matches()) {
                cleanComments.add(comment);
            }
        }
        fComments = cleanComments;
        fClean = true;
    }
}
