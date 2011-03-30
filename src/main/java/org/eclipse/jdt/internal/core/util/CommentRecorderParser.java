/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
// CHECKSTYLE:OFF
package org.eclipse.jdt.internal.core.util;

import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.parser.Scanner;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;

/**
 * Internal parser used for parsing source to create DOM AST nodes.
 * 
 * @since 3.0
 */
public class CommentRecorderParser extends Parser {

    // support for comments
    int[] commentStops = new int[10];
    int[] commentStarts = new int[10];
    int commentPtr = -1; // no comment test with commentPtr value -1
    protected final static int CommentIncrement = 100;

    /**
     * @param problemReporter
     * @param optimizeStringLiterals
     */
    public CommentRecorderParser(ProblemReporter problemReporter, boolean optimizeStringLiterals) {
        super(problemReporter, optimizeStringLiterals);
    }

    // old javadoc style check which doesn't include all leading comments into declaration
    // for backward compatibility with 2.1 DOM
    @Override
    public void checkComment() {

        // discard obsolete comments while inside methods or fields initializer (see bug 74369)
        if (!(diet && (dietInt == 0)) && (scanner.commentPtr >= 0)) {
            flushCommentsDefinedPriorTo(endStatementPosition);
        }
        boolean deprecated = false;
        boolean checkDeprecated = false;
        int lastCommentIndex = -1;

        // since jdk1.2 look only in the last java doc comment...
        nextComment: for (lastCommentIndex = scanner.commentPtr; lastCommentIndex >= 0; lastCommentIndex--) {
            // look for @deprecated into the first javadoc comment preceeding the declaration
            int commentSourceStart = scanner.commentStarts[lastCommentIndex];
            // javadoc only (non javadoc comment have negative start and/or end positions.)
            if ((commentSourceStart < 0)
                    || ((modifiersSourceStart != -1) && (modifiersSourceStart < commentSourceStart))
                    || (scanner.commentStops[lastCommentIndex] < 0)) {
                continue nextComment;
            }
            checkDeprecated = true;
            int commentSourceEnd = scanner.commentStops[lastCommentIndex] - 1; // stop is one over
            // do not report problem before last parsed comment while recovering code...
            if (javadocParser.shouldReportProblems) {
                javadocParser.reportProblems = (currentElement == null) || (commentSourceEnd > lastJavadocEnd);
            } else {
                javadocParser.reportProblems = false;
            }
            deprecated = javadocParser.checkDeprecation(lastCommentIndex);
            javadoc = javadocParser.docComment;
            if (currentElement == null) {
                lastJavadocEnd = commentSourceEnd;
            }
            break nextComment;
        }
        if (deprecated) {
            checkAndSetModifiers(ClassFileConstants.AccDeprecated);
        }
        // modify the modifier source start to point at the first comment
        if ((lastCommentIndex >= 0) && checkDeprecated) {
            modifiersSourceStart = scanner.commentStarts[lastCommentIndex];
            if (modifiersSourceStart < 0) {
                modifiersSourceStart = -modifiersSourceStart;
            }
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.compiler.parser.Parser#consumeClassHeader()
     */
    @Override
    protected void consumeClassHeader() {
        pushOnCommentsStack(0, scanner.commentPtr);
        super.consumeClassHeader();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.compiler.parser.Parser#consumeEmptyTypeDeclaration()
     */
    @Override
    protected void consumeEmptyTypeDeclaration() {
        pushOnCommentsStack(0, scanner.commentPtr);
        super.consumeEmptyTypeDeclaration();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.compiler.parser.Parser#consumeInterfaceHeader()
     */
    @Override
    protected void consumeInterfaceHeader() {
        pushOnCommentsStack(0, scanner.commentPtr);
        super.consumeInterfaceHeader();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.compiler.parser.Parser#endParse(int)
     */
    @Override
    protected CompilationUnitDeclaration endParse(int act) {
        CompilationUnitDeclaration unit = super.endParse(act);
        if (unit.comments == null) {
            pushOnCommentsStack(0, scanner.commentPtr);
            unit.comments = getCommentsPositions();
        }
        return unit;
    }

    /* (non-Javadoc)
     * Save all source comments currently stored before flushing them.
     * @see org.eclipse.jdt.internal.compiler.parser.Parser#flushCommentsDefinedPriorTo(int)
     */
    @Override
    public int flushCommentsDefinedPriorTo(int position) {

        int lastCommentIndex = scanner.commentPtr;
        if (lastCommentIndex < 0) {
            return position; // no comment
        }

        // compute the index of the first obsolete comment
        int index = lastCommentIndex;
        int validCount = 0;
        while (index >= 0) {
            int commentEnd = scanner.commentStops[index];
            if (commentEnd < 0) {
                commentEnd = -commentEnd; // negative end position for non-javadoc comments
            }
            if (commentEnd <= position) {
                break;
            }
            index--;
            validCount++;
        }
        // if the source at <position> is immediately followed by a line comment, then
        // flush this comment and shift <position> to the comment end.
        if (validCount > 0) {
            int immediateCommentEnd = 0;
            while ((index < lastCommentIndex) && ((immediateCommentEnd = -scanner.commentStops[index + 1]) > 0)) { // only
                                                                                                                   // tolerating
                                                                                                                   // non-javadoc
                                                                                                                   // comments
                                                                                                                   // (non-javadoc
                                                                                                                   // comment
                                                                                                                   // end
                                                                                                                   // positions
                                                                                                                   // are
                                                                                                                   // negative)
                // is there any line break until the end of the immediate comment ? (thus only tolerating line comment)
                immediateCommentEnd--; // comment end in one char too far
                if (org.eclipse.jdt.internal.compiler.util.Util.getLineNumber(
                        position,
                        scanner.lineEnds,
                        0,
                        scanner.linePtr) != org.eclipse.jdt.internal.compiler.util.Util.getLineNumber(
                        immediateCommentEnd,
                        scanner.lineEnds,
                        0,
                        scanner.linePtr)) {
                    break;
                }
                position = immediateCommentEnd;
                validCount--; // flush this comment
                index++;
            }
        }

        if (index < 0) {
            return position; // no obsolete comment
        }
        pushOnCommentsStack(0, index); // store comment before flushing them

        switch (validCount) {
            case 0:
                // do nothing
                break;
            // move valid comment infos, overriding obsolete comment infos
            case 2:
                scanner.commentStarts[0] = scanner.commentStarts[index + 1];
                scanner.commentStops[0] = scanner.commentStops[index + 1];
                scanner.commentTagStarts[0] = scanner.commentTagStarts[index + 1];
                scanner.commentStarts[1] = scanner.commentStarts[index + 2];
                scanner.commentStops[1] = scanner.commentStops[index + 2];
                scanner.commentTagStarts[1] = scanner.commentTagStarts[index + 2];
                break;
            case 1:
                scanner.commentStarts[0] = scanner.commentStarts[index + 1];
                scanner.commentStops[0] = scanner.commentStops[index + 1];
                scanner.commentTagStarts[0] = scanner.commentTagStarts[index + 1];
                break;
            default:
                System.arraycopy(scanner.commentStarts, index + 1, scanner.commentStarts, 0, validCount);
                System.arraycopy(scanner.commentStops, index + 1, scanner.commentStops, 0, validCount);
                System.arraycopy(scanner.commentTagStarts, index + 1, scanner.commentTagStarts, 0, validCount);
        }
        scanner.commentPtr = validCount - 1;
        return position;
    }

    /*
     * Build a n*2 matrix of comments positions.
     * For each position, 0 is for start position and 1 for end position of the comment.
     */
    public int[][] getCommentsPositions() {
        int[][] positions = new int[commentPtr + 1][2];
        for (int i = 0, max = commentPtr; i <= max; i++) {
            positions[i][0] = commentStarts[i];
            positions[i][1] = commentStops[i];
        }
        return positions;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.compiler.parser.Parser#initialize()
     */
    @Override
    public void initialize(boolean initializeNLS) {
        super.initialize(initializeNLS);
        commentPtr = -1;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jdt.internal.compiler.parser.Parser#initialize()
     */
    @Override
    public void initialize() {
        super.initialize();
        commentPtr = -1;
    }

    /* (non-Javadoc)
     * Create and store a specific comment recorder scanner.
     * @see org.eclipse.jdt.internal.compiler.parser.Parser#initializeScanner()
     */
    @Override
    public void initializeScanner() {
        scanner =
                new Scanner(
                        false /*comment*/,
                        false /*whitespace*/,
                        options.getSeverity(CompilerOptions.NonExternalizedString) != ProblemSeverities.Ignore /*nls*/,
                        options.sourceLevel /*sourceLevel*/,
                        options.taskTags/*taskTags*/,
                        options.taskPriorities/*taskPriorities*/,
                        options.isTaskCaseSensitive/*taskCaseSensitive*/);
    }

    /*
     * Push all stored comments in stack.
     */
    private void pushOnCommentsStack(int start, int end) {

        for (int i = start; i <= end; i++) {
            // First see if comment hasn't been already stored
            int scannerStart = scanner.commentStarts[i] < 0 ? -scanner.commentStarts[i] : scanner.commentStarts[i];
            int commentStart =
                    commentPtr == -1 ? -1 : (commentStarts[commentPtr] < 0
                            ? -commentStarts[commentPtr]
                            : commentStarts[commentPtr]);
            if ((commentStart == -1) || (scannerStart > commentStart)) {
                int stackLength = commentStarts.length;
                if (++commentPtr >= stackLength) {
                    System.arraycopy(
                            commentStarts,
                            0,
                            commentStarts = new int[stackLength + CommentIncrement],
                            0,
                            stackLength);
                    System.arraycopy(
                            commentStops,
                            0,
                            commentStops = new int[stackLength + CommentIncrement],
                            0,
                            stackLength);
                }
                commentStarts[commentPtr] = scanner.commentStarts[i];
                commentStops[commentPtr] = scanner.commentStops[i];
            }
        }
    }

    /* (non-Javadoc)
     * Save all source comments currently stored before flushing them.
     * @see org.eclipse.jdt.internal.compiler.parser.Parser#resetModifiers()
     */
    @Override
    protected void resetModifiers() {
        pushOnCommentsStack(0, scanner.commentPtr);
        super.resetModifiers();
    }
}
// CHECKSTYLE:ON
