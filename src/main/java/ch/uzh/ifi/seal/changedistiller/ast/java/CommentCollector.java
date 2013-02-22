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

import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;

import ch.uzh.ifi.seal.changedistiller.ast.java.Comment.CommentType;

/**
 * Collects comments for a {@link CompilationUnitDeclaration}.
 * 
 * @author Beat Fluri
 */
public class CommentCollector {

    private CompilationUnitDeclaration fCompilationUnit;
    private String fSource;
    private List<Comment> fComments;

    /**
     * Creates a new comment collector.
     * 
     * @param compilationUnit
     *            from which the comments should be collected
     * @param source
     *            of the compilation unit
     */
    public CommentCollector(CompilationUnitDeclaration compilationUnit, String source) {
        fCompilationUnit = compilationUnit;
        fSource = source;
    }

    /**
     * Collects the comments of the {@link CompilationUnitDeclaration}.
     */
    public void collect() {
        if (isNotYetCollected()) {
            fComments = new LinkedList<Comment>();
            for (int[] positions : fCompilationUnit.comments) {
                fComments.add(createComment(positions));
            }
        }
    }

    // Logic taken from org.eclipse.jdt.core.dom.ASTConverter
    private Comment createComment(int[] positions) {
        Comment comment = null;
        int start = positions[0];
        int end = positions[1];
        // Javadoc comments have positive end position
        if (end > 0) {
            comment = new Comment(CommentType.JAVA_DOC, start, end, fSource.substring(start, end));
        } else {
            end = -end;
            // we cannot know without testing chars again
            if (start == 0) {
                if (fSource.charAt(1) == '/') {
                    comment = new Comment(CommentType.LINE_COMMENT, start, end, fSource.substring(start, end));
                } else {
                    comment = new Comment(CommentType.BLOCK_COMMENT, start, end, fSource.substring(start, end));
                }
            } else if (start > 0) { // Block comment have positive start position
                comment = new Comment(CommentType.BLOCK_COMMENT, start, end, fSource.substring(start, end));
            } else { // Line comment have negative start and end position
                start = -start;
                comment = new Comment(CommentType.LINE_COMMENT, start, end, fSource.substring(start, end));
            }
        }
        return comment;
    }

    private boolean isNotYetCollected() {
        return (fComments == null) || fComments.isEmpty();
    }

    public List<Comment> getComments() {
        return fComments;
    }

}
