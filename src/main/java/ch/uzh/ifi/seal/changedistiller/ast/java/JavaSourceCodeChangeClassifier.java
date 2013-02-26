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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ch.uzh.ifi.seal.changedistiller.distilling.SourceCodeChangeClassifier;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.ChangeType;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.EntityType;
import ch.uzh.ifi.seal.changedistiller.model.classifiers.java.JavaEntityType;
import ch.uzh.ifi.seal.changedistiller.model.entities.Delete;
import ch.uzh.ifi.seal.changedistiller.model.entities.Insert;
import ch.uzh.ifi.seal.changedistiller.model.entities.Move;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeChange;
import ch.uzh.ifi.seal.changedistiller.model.entities.SourceCodeEntity;
import ch.uzh.ifi.seal.changedistiller.model.entities.StructureEntityVersion;
import ch.uzh.ifi.seal.changedistiller.model.entities.Update;

/**
 * Implementation of {@link SourceCodeChangeClassifier} for the Java language.
 * 
 * @author Beat Fluri
 */
public class JavaSourceCodeChangeClassifier implements SourceCodeChangeClassifier {

    private static final String COLON = ":";
    private static final String FINAL = "final";
    private static final String PRIVATE = "private";
    private static final String PROTECTED = "protected";
    private static final String PUBLIC = "public";
    private static final String VOID_RETURN = ": void";

    private List<Insert> fInserts;
    private List<Delete> fDeletes;
    private List<Move> fMoves;
    private List<Update> fUpdates;

    private List<Insert> fInsertsToDelete;

    @Override
    public List<SourceCodeChange> classifySourceCodeChanges(List<? extends SourceCodeChange> sourceCodeChanges) {
    	List<SourceCodeChange> classifiedChanges = new LinkedList<SourceCodeChange>();
    	
        splitOperations(sourceCodeChanges);
        
        fInsertsToDelete = new LinkedList<Insert>();
        SourceCodeChange scc = null;
        for (Iterator<Insert> it = fInserts.iterator(); it.hasNext();) {
            Insert ins = it.next();
            if (!fInsertsToDelete.contains(ins)) {
                scc = classify(ins);
                if ((scc != null) && !classifiedChanges.contains(scc)) {
                    classifiedChanges.add(scc);
                    it.remove();
                }
            }
        }
        for (Insert ins : fInsertsToDelete) {
            fInserts.remove(ins);
        }
        fInsertsToDelete.clear();
        for (Iterator<Delete> it = fDeletes.iterator(); it.hasNext();) {
            Delete del = it.next();
            scc = classify(del);
            if ((scc != null) && !classifiedChanges.contains(scc)) {
                classifiedChanges.add(scc);
                it.remove();
            }
        }
        for (Iterator<Move> it = fMoves.iterator(); it.hasNext();) {
            Move mov = it.next();
            scc = classify(mov);
            if ((scc != null) && !classifiedChanges.contains(scc)) {
                classifiedChanges.add(scc);
                it.remove();
            }
        }
        for (Iterator<Update> it = fUpdates.iterator(); it.hasNext();) {
            Update upd = it.next();
            scc = classify(upd);
            if ((scc != null) && !classifiedChanges.contains(scc)) {
                classifiedChanges.add(scc);
                it.remove();
            }
        }

        return classifiedChanges;
    }

    private SourceCodeChange classify(Insert insert) {
        SourceCodeChange result = null;

        if (insert.getChangeType() != ChangeType.UNCLASSIFIED_CHANGE) {
            return insert;
        }

        // ugly hack ;)
        if (insert.getChangedEntity().getType() == JavaEntityType.THEN_STATEMENT) {
            return null;
        }

        if ((insert.getParentEntity().getType() != null)
                && (insert.getParentEntity().getType() == JavaEntityType.MODIFIERS)) {
            result = extractModifiersChange(insert);

        } else if (insert.getChangedEntity().getType() == JavaEntityType.METHOD) {
            insert.setChangeType(ChangeType.ADDITIONAL_FUNCTIONALITY);
            result = insert;
        } else if (insert.getChangedEntity().getType() == JavaEntityType.FIELD) {
            insert.setChangeType(ChangeType.ADDITIONAL_OBJECT_STATE);
            result = insert;
        } else if (insert.getChangedEntity().getType() == JavaEntityType.CLASS) {
            insert.setChangeType(ChangeType.ADDITIONAL_CLASS);
            result = insert;
        } else if (insert.getRootEntity().getType() == JavaEntityType.METHOD) {
            result = handleMethodSignatureChange(insert);
            if (result == null) {
                result = handleNormalInsert(insert);
            }
        } else if (insert.getRootEntity().getType() == JavaEntityType.FIELD) {
            result = handleFieldDeclarationChange(insert);
        } else if (insert.getRootEntity().getType() == JavaEntityType.CLASS) {
            result = handleTypeDeclarationChange(insert);
            if (result == null) {
                result = handleInheritanceChange(insert);
            }
        }
        return result;
    }

    private SourceCodeChange handleInheritanceChange(Insert insert) {
        SourceCodeChange result = null;
        if (insert.getChangedEntity().getType().isType()) {
            if (insert.getParentEntity().getType() == JavaEntityType.SUPER_INTERFACE_TYPES) {
                insert.setChangeType(ChangeType.PARENT_INTERFACE_INSERT);
                result = insert;
            } else {
                boolean check = true;
                Delete del = null;
                for (Iterator<Delete> it = fDeletes.iterator(); it.hasNext() && check;) {
                    del = it.next();
                    if ((del.getRootEntity().getType() == JavaEntityType.CLASS)
                            && (del.getParentEntity().getType() != JavaEntityType.SUPER_INTERFACE_TYPES)
                            && del.getChangedEntity().getType().isType()) {
                        check = false;
                    }
                }
                if (check) {
                    insert.setChangeType(ChangeType.PARENT_CLASS_INSERT);
                    result = insert;
                } else {
                    result =
                            new Update(
                                    insert.getRootEntity(),
                                    del.getChangedEntity(),
                                    insert.getChangedEntity(),
                                    insert.getParentEntity());
                    result.setChangeType(ChangeType.PARENT_CLASS_CHANGE);
                    fDeletes.remove(del);
                }
            }
        }
        return result;
    }

    private SourceCodeChange handleFieldDeclarationChange(Insert insert) {
        SourceCodeChange result = null;
        // may lead to incorrect result (never happened so far); better: check for each
        // possible kind of type
        if (insert.getChangedEntity().getType().isType()) {
            Delete del =
                    findSpDeleteOperation(
                            insert.getRootEntity().getType(),
                            insert.getRootEntity().getUniqueName(),
                            insert.getParentEntity().getType(),
                            insert.getParentEntity().getUniqueName());
            if (del != null) {
                result =
                        new Update(
                                insert.getRootEntity(),
                                del.getChangedEntity(),
                                insert.getChangedEntity(),
                                insert.getParentEntity());
                fDeletes.remove(del);
                result.setChangeType(ChangeType.ATTRIBUTE_TYPE_CHANGE);
            }
        } else if (insert.getChangedEntity().getType() == JavaEntityType.JAVADOC) {
            Delete del =
                    findDeleteOperation(
                            insert.getRootEntity().getType(),
                            insert.getRootEntity().getUniqueName(),
                            insert.getParentEntity().getType(),
                            insert.getParentEntity().getUniqueName(),
                            insert.getChangedEntity().getType(),
                            null);
            if (del != null) {
                result =
                        new Update(
                                insert.getRootEntity(),
                                del.getChangedEntity(),
                                insert.getChangedEntity(),
                                insert.getParentEntity());
                result.setChangeType(ChangeType.DOC_UPDATE);
                fDeletes.remove(del);
            } else {
                insert.setChangeType(ChangeType.DOC_INSERT);
                result = insert;
            }
        }
        return result;
    }

    private SourceCodeChange handleFieldDeclarationChange(Delete delete) {
        SourceCodeChange result = null;
        if (delete.getChangedEntity().getType() == JavaEntityType.JAVADOC) {
            delete.setChangeType(ChangeType.DOC_DELETE);
            result = delete;
        }
        return result;
    }

    private SourceCodeChange handleTypeDeclarationChange(Delete delete) {
        SourceCodeChange result = null;
        if (delete.getChangedEntity().getType() == JavaEntityType.JAVADOC) {
            delete.setChangeType(ChangeType.DOC_DELETE);
            result = delete;
        }
        return result;
    }

    private SourceCodeChange handleTypeDeclarationChange(Insert insert) {
        SourceCodeChange result = null;
        if (insert.getChangedEntity().getType() == JavaEntityType.JAVADOC) {
            Delete del =
                    findDeleteOperation(
                            insert.getRootEntity().getType(),
                            insert.getRootEntity().getUniqueName(),
                            insert.getParentEntity().getType(),
                            insert.getParentEntity().getUniqueName(),
                            insert.getChangedEntity().getType(),
                            null);
            if (del != null) {
                result =
                        new Update(
                                insert.getRootEntity(),
                                del.getChangedEntity(),
                                insert.getChangedEntity(),
                                insert.getParentEntity());
                result.setChangeType(ChangeType.DOC_UPDATE);
                fDeletes.remove(del);
            } else {
                insert.setChangeType(ChangeType.DOC_INSERT);
                result = insert;
            }
        }
        return result;
    }

    private SourceCodeChange handleTypeDeclarationChange(Update update) {
        SourceCodeChange result = null;
        if (update.getChangedEntity().getType() == JavaEntityType.JAVADOC) {
            update.setChangeType(ChangeType.DOC_UPDATE);
            result = update;
        }
        return result;
    }

    private SourceCodeChange handleMethodSignatureChange(Insert insert) {
        SourceCodeChange result = null;

        if (insert.getChangedEntity().getType() == JavaEntityType.JAVADOC) {
            Delete del =
                    findDeleteOperation(
                            insert.getRootEntity().getType(),
                            insert.getRootEntity().getUniqueName(),
                            insert.getParentEntity().getType(),
                            insert.getParentEntity().getUniqueName(),
                            insert.getChangedEntity().getType(),
                            null);
            if (del != null) {
                result =
                        new Update(
                                insert.getRootEntity(),
                                del.getChangedEntity(),
                                insert.getChangedEntity(),
                                insert.getParentEntity());
                result.setChangeType(ChangeType.DOC_UPDATE);
                fDeletes.remove(del);
            } else {
                insert.setChangeType(ChangeType.DOC_INSERT);
                result = insert;
            }
        } else if (insert.getParentEntity().getType() == JavaEntityType.PARAMETERS) {
            result = extractParameterChange(insert);
        } else if (insert.getParentEntity().getType() == JavaEntityType.METHOD_DECLARATION) {
            result = extractReturnChange(insert);
        }
        return result;
    }

    private SourceCodeChange extractReturnChange(Insert insert) {
        SourceCodeChange result = null;
        // may lead to incorrect result (never happened so far); better: check for each
        // possible kind of type
        if (insert.getChangedEntity().getType().isType()) {
            if (insert.getChangedEntity().getUniqueName().endsWith(VOID_RETURN)) {
                Delete del =
                        findSpDeleteOperation(
                                insert.getRootEntity().getType(),
                                insert.getRootEntity().getUniqueName(),
                                insert.getParentEntity().getType(),
                                insert.getParentEntity().getUniqueName());

                del.setChangeType(ChangeType.RETURN_TYPE_DELETE);
                result = del;
            } else {
                Delete del = null;
                boolean check = true;
                // if a non-void type deletion in method declaration occurred
                // => RETURN_TYPE_CHANGE
                for (Iterator<Delete> it = fDeletes.iterator(); it.hasNext() && check;) {
                    del = it.next();
                    if ((insert.getRootEntity().getType() == del.getRootEntity().getType())
                            && insert.getRootEntity().getUniqueName().equals(del.getRootEntity().getUniqueName())
                            && (del.getParentEntity().getType() == JavaEntityType.METHOD_DECLARATION)
                            && del.getParentEntity().getUniqueName().equals(insert.getParentEntity().getUniqueName())
                            && del.getChangedEntity().getType().isType()
                            && !del.getChangedEntity().getUniqueName().matches(".*: void")) {
                        check = false;
                    }
                }
                if (!check) {
                    result =
                            new Update(
                                    insert.getRootEntity(),
                                    del.getChangedEntity(),
                                    insert.getChangedEntity(),
                                    insert.getParentEntity());
                    result.setChangeType(ChangeType.RETURN_TYPE_CHANGE);
                    fDeletes.remove(del);
                } else {
                    insert.setChangeType(ChangeType.RETURN_TYPE_INSERT);
                    result = insert;
                }
            }
        }
        return result;
    }

    private SourceCodeChange extractParameterChange(Insert insert) {
        SourceCodeChange result = null;
        if (insert.getChangedEntity().getType() == JavaEntityType.PARAMETER) {
            // SingleVariableDeclaration has changed, but the type node (child)
            // remains the same => PARAMETER_RENAMING
            Move mov =
                    findMoveOperation(
                            insert.getRootEntity().getType(),
                            insert.getRootEntity().getUniqueName(),
                            JavaEntityType.PARAMETER,
                            null,
                            JavaEntityType.PARAMETER,
                            insert.getChangedEntity().getUniqueName(),
                            null,
                            null);

            Delete del =
                    findDeleteOperation(
                            insert.getRootEntity().getType(),
                            insert.getRootEntity().getUniqueName(),
                            JavaEntityType.PARAMETERS,
                            "",
                            JavaEntityType.PARAMETER,
                            insert.getChangedEntity().getUniqueName());
            // parameter renaming
            if (mov != null) {
                Delete d =
                        findDeleteOperation(
                                insert.getRootEntity().getType(),
                                insert.getRootEntity().getUniqueName(),
                                JavaEntityType.PARAMETERS,
                                "",
                                JavaEntityType.PARAMETER,
                                mov.getParentEntity().getUniqueName());
                if (d == null) {
                    insert.setChangeType(ChangeType.PARAMETER_INSERT);
                    result = insert;
                } else {
                    result =
                            new Update(
                                    insert.getRootEntity(),
                                    insert.getChangedEntity(),
                                    d.getChangedEntity(),
                                    insert.getParentEntity());
                    result.setChangeType(ChangeType.PARAMETER_RENAMING);
                    fMoves.remove(mov);
                    fDeletes.remove(d);
                }

                // SingleVariableDeclaration remains the same but the type
                // node (child) are not equal => PARAMETER_TYPE_CHANGE
            } else if (del != null) {
                Delete dell =
                        findDeleteOperation(
                                insert.getRootEntity().getType(),
                                insert.getRootEntity().getUniqueName(),
                                del.getChangedEntity().getType(),
                                del.getChangedEntity().getUniqueName(),
                                null,
                                null);
                if (dell == null) {
                    insert.setChangeType(ChangeType.PARAMETER_INSERT);
                    result = insert;
                } else {

                    // WTF how to remove the insert?

                    Insert i =
                            findInsertOperation(insert.getRootEntity().getType(), insert.getRootEntity()
                                    .getUniqueName(), insert.getChangedEntity().getType(), insert.getChangedEntity()
                                    .getUniqueName(), null, null);
                    if (i == null) {
                        insert.setChangeType(ChangeType.PARAMETER_INSERT);
                        result = insert;
                    } else {
                        result =
                                new Update(
                                        insert.getRootEntity(),
                                        dell.getChangedEntity(),
                                        i.getChangedEntity(),
                                        insert.getChangedEntity());
                        result.setChangeType(ChangeType.PARAMETER_TYPE_CHANGE);
                        fDeletes.remove(del);
                        fDeletes.remove(dell);
                        fInsertsToDelete.add(i);
                    }
                }
            } else {
                insert.setChangeType(ChangeType.PARAMETER_INSERT);
                result = insert;
            }
        }
        return result;
    }

    private SourceCodeChange extractModifiersChange(Insert insert) {
        SourceCodeChange result = null;

        if (insert.getChangedEntity().getUniqueName().equals(FINAL)) {
            return handleFinalChange(insert);
        } else if (insert.getChangedEntity().getUniqueName().equals(PUBLIC)) {
            result = extractIncreasingAccessibilityChange(insert);
        } else if (insert.getChangedEntity().getUniqueName().equals(PRIVATE)) {
            result = extractDecreasingAccessibilityChange(insert);
        } else if (insert.getChangedEntity().getUniqueName().equals(PROTECTED)) {
            Delete delPublic =
                    findDeleteOperation(
                            insert.getRootEntity().getType(),
                            insert.getRootEntity().getUniqueName(),
                            JavaEntityType.MODIFIERS,
                            "",
                            JavaEntityType.MODIFIER,
                            PUBLIC);
            Delete delPrivate =
                    findDeleteOperation(
                            insert.getRootEntity().getType(),
                            insert.getRootEntity().getUniqueName(),
                            JavaEntityType.MODIFIERS,
                            "",
                            JavaEntityType.MODIFIER,
                            PRIVATE);

            // indeed there are other cases in which protected can be inserted,
            // but these cases are covered with other operations
            if ((delPublic == null) && (delPrivate == null)) {
                insert.setChangeType(ChangeType.INCREASING_ACCESSIBILITY_CHANGE);
                result = insert;
            }
        }
        return result;
    }

    private SourceCodeChange handleFinalChange(Insert insert) {
        if (insert.getRootEntity().getType() == JavaEntityType.CLASS) {
            insert.setChangeType(ChangeType.REMOVING_CLASS_DERIVABILITY);
        } else if (insert.getRootEntity().getType() == JavaEntityType.METHOD) {
            insert.setChangeType(ChangeType.REMOVING_METHOD_OVERRIDABILITY);
        } else if (insert.getRootEntity().getType() == JavaEntityType.FIELD) {
            insert.setChangeType(ChangeType.REMOVING_ATTRIBUTE_MODIFIABILITY);
        } else {
            return null;
        }
        return insert;
    }

    private SourceCodeChange handleNormalInsert(Insert insert) {
        SourceCodeChange result = null;
        if (insert.getChangedEntity().getType() == JavaEntityType.ELSE_STATEMENT) {
            insert.setChangeType(ChangeType.ALTERNATIVE_PART_INSERT);
            result = insert;
        } else if ((insert.getChangedEntity().getType() == JavaEntityType.BLOCK_COMMENT)
                || (insert.getChangedEntity().getType() == JavaEntityType.LINE_COMMENT)) {
            insert.setChangeType(ChangeType.COMMENT_INSERT);
            result = insert;
        } else if (insert.getChangedEntity().getType().isStatement()) {
            insert.setChangeType(ChangeType.STATEMENT_INSERT);
            result = insert;
        }
        return result;
    }

    private SourceCodeChange extractIncreasingAccessibilityChange(Insert insert) {
        insert.setChangeType(ChangeType.INCREASING_ACCESSIBILITY_CHANGE);
        SourceCodeChange result = null;

        Delete delProtected =
                findDeleteOperation(
                        insert.getRootEntity().getType(),
                        insert.getRootEntity().getUniqueName(),
                        JavaEntityType.MODIFIERS,
                        "",
                        JavaEntityType.MODIFIER,
                        PROTECTED);
        Delete delPrivate =
                findDeleteOperation(
                        insert.getRootEntity().getType(),
                        insert.getRootEntity().getUniqueName(),
                        JavaEntityType.MODIFIERS,
                        "",
                        JavaEntityType.MODIFIER,
                        PRIVATE);
        if (delProtected != null) {
            result =
                    new Update(
                            insert.getRootEntity(),
                            delProtected.getChangedEntity(),
                            insert.getChangedEntity(),
                            insert.getParentEntity());
            result.setChangeType(ChangeType.INCREASING_ACCESSIBILITY_CHANGE);
            fDeletes.remove(delProtected);
        } else if (delPrivate != null) {
            result =
                    new Update(
                            insert.getRootEntity(),
                            delPrivate.getChangedEntity(),
                            insert.getChangedEntity(),
                            insert.getParentEntity());
            result.setChangeType(ChangeType.INCREASING_ACCESSIBILITY_CHANGE);
            fDeletes.remove(delPrivate);
        } else {
            result = insert;
        }
        return result;
    }

    private SourceCodeChange extractDecreasingAccessibilityChange(Insert insert) {
        insert.setChangeType(ChangeType.DECREASING_ACCESSIBILITY_CHANGE);
        SourceCodeChange result = null;

        Delete delProtected =
                findDeleteOperation(
                        insert.getRootEntity().getType(),
                        insert.getRootEntity().getUniqueName(),
                        JavaEntityType.MODIFIERS,
                        "",
                        JavaEntityType.MODIFIER,
                        PROTECTED);
        Delete delPublic =
                findDeleteOperation(
                        insert.getRootEntity().getType(),
                        insert.getRootEntity().getUniqueName(),
                        JavaEntityType.MODIFIERS,
                        "",
                        JavaEntityType.MODIFIER,
                        PUBLIC);
        if (delProtected != null) {
            result =
                    new Update(
                            insert.getRootEntity(),
                            delProtected.getChangedEntity(),
                            insert.getChangedEntity(),
                            insert.getParentEntity());
            result.setChangeType(ChangeType.DECREASING_ACCESSIBILITY_CHANGE);
            fDeletes.remove(delProtected);
        } else if (delPublic != null) {
            result =
                    new Update(
                            insert.getRootEntity(),
                            delPublic.getChangedEntity(),
                            insert.getChangedEntity(),
                            insert.getParentEntity());
            fDeletes.remove(delPublic);
            result.setChangeType(ChangeType.DECREASING_ACCESSIBILITY_CHANGE);
        } else {
            result = insert;
        }
        return result;
    }

    private SourceCodeChange classify(Delete delete) {
        SourceCodeChange result = null;

        if (delete.getChangeType() != ChangeType.UNCLASSIFIED_CHANGE) {
            return delete;
        }

        // ugly hack ;)
        if (delete.getChangedEntity().getType() == JavaEntityType.THEN_STATEMENT) {
            return null;
        }

        if ((delete.getParentEntity().getType() != null)
                && (delete.getParentEntity().getType() == JavaEntityType.MODIFIERS)) {
            result = extractModifiersChange(delete);

        } else if (delete.getChangedEntity().getType() == JavaEntityType.METHOD) {
            delete.setChangeType(ChangeType.REMOVED_FUNCTIONALITY);
            result = delete;
        } else if (delete.getChangedEntity().getType() == JavaEntityType.FIELD) {
            delete.setChangeType(ChangeType.REMOVED_OBJECT_STATE);
            result = delete;

        } else if (delete.getChangedEntity().getType() == JavaEntityType.CLASS) {
            delete.setChangeType(ChangeType.REMOVED_CLASS);
            result = delete;
        } else if (delete.getRootEntity().getType() == JavaEntityType.METHOD) {
            result = handleMethodSignatureChange(delete);
            if (result == null) {
                result = handleNormalDelete(delete);
            }
        } else if (delete.getRootEntity().getType() == JavaEntityType.FIELD) {
            result = handleFieldDeclarationChange(delete);
        } else if (delete.getRootEntity().getType() == JavaEntityType.CLASS) {
            result = handleTypeDeclarationChange(delete);
            if (result == null) {
                result = handleInheritanceChange(delete);
            }
        }
        return result;
    }

    private SourceCodeChange handleInheritanceChange(Delete delete) {
        if (delete.getChangedEntity().getType().isType()) {
            if (delete.getParentEntity().getType() == JavaEntityType.SUPER_INTERFACE_TYPES) {
                delete.setChangeType(ChangeType.PARENT_INTERFACE_DELETE);
            } else {
                delete.setChangeType(ChangeType.PARENT_CLASS_DELETE);
            }
        }
        return delete;
    }

    private SourceCodeChange handleMethodSignatureChange(Delete delete) {
        if (delete.getChangedEntity().getType() == JavaEntityType.JAVADOC) {
            delete.setChangeType(ChangeType.DOC_DELETE);
        } else if (delete.getParentEntity().getType() == JavaEntityType.PARAMETERS) {
            if (delete.getChangedEntity().getType() == JavaEntityType.PARAMETER) {
                delete.setChangeType(ChangeType.PARAMETER_DELETE);
            }
        } else if (delete.getParentEntity().getType() == JavaEntityType.METHOD_DECLARATION) {
            if (delete.getChangedEntity().getType().isType()) {
                // whenever void as return type is deleted, a concrete return type was inserted. therefore we can ignore
                // this delete change
                if (delete.getChangedEntity().getUniqueName().endsWith(VOID_RETURN)) {
                    return null;
                } else {
                    delete.setChangeType(ChangeType.RETURN_TYPE_DELETE);
                }
            }
        } else {
            return null;
        }
        return delete;
    }

    private SourceCodeChange extractModifiersChange(Delete delete) {
        SourceCodeChange result = delete;

        if (delete.getChangedEntity().getUniqueName().equals(FINAL)) {
            return handleFinalChange(delete);
        } else if (delete.getChangedEntity().getUniqueName().equals(PRIVATE)) {
            result = extractIncreasingAccessibilityChange(delete);
        } else if (delete.getChangedEntity().getUniqueName().equals(PUBLIC)) {
            result = extractDecreasingAccessibilityChange(delete);
        } else if (delete.getChangedEntity().getUniqueName().equals(PROTECTED)) {
            Insert insPublic =
                    findInsertOperation(
                            delete.getRootEntity().getType(),
                            delete.getRootEntity().getUniqueName(),
                            JavaEntityType.MODIFIERS,
                            "",
                            JavaEntityType.MODIFIER,
                            PUBLIC);
            Insert insPrivate =
                    findInsertOperation(
                            delete.getRootEntity().getType(),
                            delete.getRootEntity().getUniqueName(),
                            JavaEntityType.MODIFIERS,
                            "",
                            JavaEntityType.MODIFIER,
                            PRIVATE);
            if ((insPublic == null) && (insPrivate == null)) {
                delete.setChangeType(ChangeType.DECREASING_ACCESSIBILITY_CHANGE);
            }
        }
        return result;
    }

    private SourceCodeChange extractDecreasingAccessibilityChange(Delete delete) {
        delete.setChangeType(ChangeType.DECREASING_ACCESSIBILITY_CHANGE);
        SourceCodeChange result;

        Insert insProtected =
                findInsertOperation(
                        delete.getRootEntity().getType(),
                        delete.getRootEntity().getUniqueName(),
                        JavaEntityType.MODIFIERS,
                        "",
                        JavaEntityType.MODIFIER,
                        PROTECTED);
        Insert insPrivate =
                findInsertOperation(
                        delete.getRootEntity().getType(),
                        delete.getRootEntity().getUniqueName(),
                        JavaEntityType.MODIFIERS,
                        "",
                        JavaEntityType.MODIFIER,
                        PRIVATE);
        if (insProtected != null) {
            result =
                    new Update(
                            delete.getRootEntity(),
                            delete.getChangedEntity(),
                            insProtected.getChangedEntity(),
                            insProtected.getParentEntity());
            fInserts.remove(insProtected);
        } else if (insPrivate != null) {
            result =
                    new Update(
                            delete.getRootEntity(),
                            delete.getChangedEntity(),
                            insPrivate.getChangedEntity(),
                            insPrivate.getParentEntity());
            fInserts.remove(insPrivate);
        } else {
            result = delete;
        }
        result.setChangeType(ChangeType.DECREASING_ACCESSIBILITY_CHANGE);
        return result;
    }

    private SourceCodeChange extractIncreasingAccessibilityChange(Delete delete) {
        delete.setChangeType(ChangeType.INCREASING_ACCESSIBILITY_CHANGE);
        SourceCodeChange result;

        Insert insProtected =
                findInsertOperation(
                        delete.getRootEntity().getType(),
                        delete.getRootEntity().getUniqueName(),
                        JavaEntityType.MODIFIERS,
                        "",
                        JavaEntityType.MODIFIER,
                        PROTECTED);
        findInsertOperation(
                delete.getRootEntity().getType(),
                delete.getRootEntity().getUniqueName(),
                JavaEntityType.MODIFIERS,
                "",
                JavaEntityType.MODIFIER,
                PUBLIC);
        if (insProtected != null) {
            result =
                    new Update(
                            delete.getRootEntity(),
                            delete.getChangedEntity(),
                            insProtected.getChangedEntity(),
                            insProtected.getParentEntity());
            result.setChangeType(ChangeType.INCREASING_ACCESSIBILITY_CHANGE);
            fInserts.remove(insProtected);
        } else {
            result = delete;
        }
        return result;
    }

    private SourceCodeChange handleFinalChange(Delete delete) {
        if (delete.getRootEntity().getType() == JavaEntityType.CLASS) {
            delete.setChangeType(ChangeType.ADDING_CLASS_DERIVABILITY);
        } else if (delete.getRootEntity().getType() == JavaEntityType.METHOD) {
            delete.setChangeType(ChangeType.ADDING_METHOD_OVERRIDABILITY);
        } else if (delete.getRootEntity().getType() == JavaEntityType.FIELD) {
            delete.setChangeType(ChangeType.ADDING_ATTRIBUTE_MODIFIABILITY);
        } else {
            return null;
        }
        return delete;
    }

    private SourceCodeChange handleNormalDelete(Delete delete) {
        SourceCodeChange result = null;
        if (delete.getChangedEntity().getType() == JavaEntityType.ELSE_STATEMENT) {
            delete.setChangeType(ChangeType.ALTERNATIVE_PART_DELETE);
            result = delete;
        } else if ((delete.getChangedEntity().getType() == JavaEntityType.BLOCK_COMMENT)
                || (delete.getChangedEntity().getType() == JavaEntityType.LINE_COMMENT)) {
            delete.setChangeType(ChangeType.COMMENT_DELETE);
            result = delete;
        } else if (delete.getChangedEntity().getType().isStatement()) {
            delete.setChangeType(ChangeType.STATEMENT_DELETE);
            result = delete;
        }
        return result;
    }

    private SourceCodeChange classify(Move move) {
        SourceCodeChange result = null;

        if (move.getChangeType() != ChangeType.UNCLASSIFIED_CHANGE) {
            return move;
        }

        // ugly hack ;)
        if (move.getChangedEntity().getType() == JavaEntityType.THEN_STATEMENT) {
            return null;
        }

        if (move.getRootEntity().getType() == JavaEntityType.METHOD) {
            result = handleMethodSignatureChange(move);
            if (result == null) {
                result = handleNormalMove(move);
            }
        }
        return result;
    }

    private SourceCodeChange handleMethodSignatureChange(Move move) {
        if ((move.getParentEntity().getType() == JavaEntityType.PARAMETERS)
                && (move.getNewParentEntity().getType() == JavaEntityType.PARAMETERS)
                && (move.getChangedEntity().getType() == JavaEntityType.PARAMETER)) {
            move.setChangeType(ChangeType.PARAMETER_ORDERING_CHANGE);
        } else {
            return null;
        }
        return move;
    }

    private SourceCodeChange handleNormalMove(Move move) {
        SourceCodeChange result = null;
        if (move.getChangedEntity().getType().isStatement()) {
            if (move.getParentEntity().getUniqueName().equals(move.getNewParentEntity().getUniqueName())
                    && (move.getParentEntity().getType() == move.getNewParentEntity().getType())) {
                move.setChangeType(ChangeType.STATEMENT_ORDERING_CHANGE);
                result = move;
            } else {
                move.setChangeType(ChangeType.STATEMENT_PARENT_CHANGE);
                result = move;
            }
        }
        if ((move.getChangedEntity().getType() == JavaEntityType.BLOCK_COMMENT)
                || (move.getChangedEntity().getType() == JavaEntityType.LINE_COMMENT)) {
            move.setChangeType(ChangeType.COMMENT_MOVE);
            result = move;
        }
        return result;
    }

    private SourceCodeChange classify(Update update) {
        SourceCodeChange result = null;

        if (update.getChangeType() != ChangeType.UNCLASSIFIED_CHANGE) {
            return update;
        }

        if (update.getRootEntity().getType() == JavaEntityType.METHOD) {
            result = extractRenaming(update);
            if (result == null) {
                result = handleMethodSignatureChange(update);
                if (result == null) {
                    result = handleNormalUpdate(update);
                }
            }
        } else if (update.getRootEntity().getType() == JavaEntityType.CLASS) {
            result = extractRenaming(update);
            if (result == null) {
                result = handleTypeDeclarationChange(update);
                if (result == null) {
                    result = handleInheritanceChange(update);
                }
            }
        } else if (update.getRootEntity().getType() == JavaEntityType.FIELD) {
            result = extractRenaming(update);
            if (result == null) {
                result = handleFieldDeclarationChange(update);
            }
        }
        return result;
    }

    private SourceCodeChange handleInheritanceChange(Update update) {
        if (update.getNewEntity().getType().isType()) {
            if (update.getParentEntity().getType() == JavaEntityType.SUPER_INTERFACE_TYPES) {
                update.setChangeType(ChangeType.PARENT_INTERFACE_CHANGE);
            } else {
                update.setChangeType(ChangeType.PARENT_CLASS_CHANGE);
            }
        } else {
            return null;
        }
        return update;
    }

    private SourceCodeChange handleFieldDeclarationChange(Update update) {
        if (update.getNewEntity().getType().isType()) {
            update.setChangeType(ChangeType.ATTRIBUTE_TYPE_CHANGE);
        } else if (update.getChangedEntity().getType() == JavaEntityType.JAVADOC) {
            update.setChangeType(ChangeType.DOC_UPDATE);
        } else {
            return null;
        }
        return update;
    }

    private SourceCodeChange handleMethodSignatureChange(Update upd) {
        SourceCodeChange result = null;
        if (upd.getNewEntity().getType().isType()) {
            if (upd.getParentEntity().getType() == JavaEntityType.PARAMETER) {
                String[] oldSplit = upd.getChangedEntity().getUniqueName().split(COLON);
                String[] newSplit = upd.getNewEntity().getUniqueName().split(COLON);
                if ((oldSplit.length > 1) && (newSplit.length > 1) && !oldSplit[1].equals(newSplit[1])) {
                    // MW: BUG FIX for IndexOutOfBoundsException
                    // BF: use 1 as index!!
                    upd.setChangeType(ChangeType.PARAMETER_TYPE_CHANGE);
                    result = upd;
                }
            } else {
                if (upd.getNewEntity().getUniqueName().endsWith(VOID_RETURN)) {
                    result = new Delete(upd.getRootEntity(), upd.getChangedEntity(), upd.getParentEntity());
                    result.setChangeType(ChangeType.RETURN_TYPE_DELETE);
                } else if (upd.getChangedEntity().getUniqueName().endsWith(VOID_RETURN)) {
                    result = new Insert(upd.getRootEntity(), upd.getNewEntity(), upd.getParentEntity());
                    result.setChangeType(ChangeType.RETURN_TYPE_INSERT);
                } else {
                    upd.setChangeType(ChangeType.RETURN_TYPE_CHANGE);
                    result = upd;
                }
            }
        } else if (upd.getNewEntity().getType() == JavaEntityType.PARAMETER) {
            upd.setChangeType(ChangeType.PARAMETER_RENAMING);
            result = upd;
        } else if (upd.getChangedEntity().getType() == JavaEntityType.JAVADOC) {
            upd.setChangeType(ChangeType.DOC_UPDATE);
            result = upd;
        }
        return result;
    }

    private SourceCodeChange extractRenaming(Update update) {
        if (update.getNewEntity().getType() == JavaEntityType.METHOD) {
            update.setChangeType(ChangeType.METHOD_RENAMING);
        } else if (update.getNewEntity().getType() == JavaEntityType.FIELD) {
            update.setChangeType(ChangeType.ATTRIBUTE_RENAMING);
        } else if (update.getNewEntity().getType() == JavaEntityType.CLASS) {
            update.setChangeType(ChangeType.CLASS_RENAMING);
        } else {
            return null;
        }
        return update;
    }

    private SourceCodeChange handleNormalUpdate(Update update) {
        SourceCodeChange result = null;
        switch ((JavaEntityType) update.getNewEntity().getType()) {
            case IF_STATEMENT:
            case FOR_STATEMENT:
            case WHILE_STATEMENT:
            case DO_STATEMENT:
            case FOREACH_STATEMENT:
                update.setChangeType(ChangeType.CONDITION_EXPRESSION_CHANGE);
                result = update;
                break;
            case THEN_STATEMENT:
            case ELSE_STATEMENT:
                result = null;
                break;
            default:
                if ((update.getChangedEntity().getType() == JavaEntityType.BLOCK_COMMENT)
                        || (update.getChangedEntity().getType() == JavaEntityType.LINE_COMMENT)) {
                    update.setChangeType(ChangeType.COMMENT_UPDATE);
                    result = update;
                } else if (update.getChangedEntity().getType().isStatement()) {
                    update.setChangeType(ChangeType.STATEMENT_UPDATE);
                    result = update;
                }
        }
        return result;
    }

    private Delete findSpDeleteOperation(
            EntityType structureEntityType,
            String structureEntityName,
            EntityType parentEntityType,
            String parentEntityName) {
        for (Delete del : fDeletes) {
            if (isEqual(del.getRootEntity(), structureEntityType, structureEntityName)
                    && isEqual(del.getParentEntity(), parentEntityType, parentEntityName)
                    && del.getChangedEntity().getType().isType()) {
                return del;
            }
        }
        return null;
    }

    private Delete findDeleteOperation(
            EntityType structureEntityType,
            String structureEntityName,
            EntityType parentEntityType,
            String parentEntityName,
            EntityType entityType,
            String entityName) {
        for (Delete del : fDeletes) {
            if (isEqual(del.getRootEntity(), structureEntityType, structureEntityName)
                    && isEqual(del.getParentEntity(), parentEntityType, parentEntityName)
                    && isEqual(del.getChangedEntity(), entityType, entityName)) {
                return del;
            }
        }
        return null;
    }

    private Insert findInsertOperation(
            EntityType structureEntityType,
            String structureEntityName,
            EntityType parentEntityType,
            String parentEntityName,
            EntityType entityType,
            String entityName) {
        for (Insert ins : fInserts) {
            if (isEqual(ins.getRootEntity(), structureEntityType, structureEntityName)
                    && isEqual(ins.getParentEntity(), parentEntityType, parentEntityName)
                    && isEqual(ins.getChangedEntity(), entityType, entityName)) {
                return ins;
            }
        }
        return null;
    }

    private Move findMoveOperation(
            EntityType structureEntityType,
            String structureEntityName,
            EntityType oldParentEntityType,
            String oldParentEntityName,
            EntityType newParentEntityType,
            String newParentEntityName,
            EntityType entityType,
            String entityName) {
        for (Move mov : fMoves) {
            if (isEqual(mov.getRootEntity(), structureEntityType, structureEntityName)
                    && isEqual(mov.getParentEntity(), oldParentEntityType, oldParentEntityName)
                    && isEqual(mov.getNewParentEntity(), newParentEntityType, newParentEntityName)
                    && isEqual(mov.getChangedEntity(), entityType, entityName)) {
                return mov;
            }
        }
        return null;
    }

    private boolean isEqual(SourceCodeEntity entity, EntityType expectedEntityType, String expectedEntityName) {
        boolean type = false;
        boolean name = false;
        if (expectedEntityType == null) {
            type = true;
        } else {
            type = entity.getType() == expectedEntityType;
        }
        if (expectedEntityName == null) {
            name = true;
        } else {
            name = entity.getUniqueName().equals(expectedEntityName);
        }
        return type && name;

    }

    private boolean isEqual(StructureEntityVersion entity, EntityType expectedEntityType, String expectedEntityName) {
        boolean type = false;
        boolean name = false;
        if (expectedEntityType == null) {
            type = true;
        } else {
            type = entity.getType() == expectedEntityType;
        }
        if (expectedEntityName == null) {
            name = true;
        } else {
            name = entity.getUniqueName().equals(expectedEntityName);
        }
        return type && name;

    }

    private void splitOperations(List<? extends SourceCodeChange> operations) {
        fInserts = new LinkedList<Insert>();
        fDeletes = new LinkedList<Delete>();
        fMoves = new LinkedList<Move>();
        fUpdates = new LinkedList<Update>();
        for (SourceCodeChange op : operations) {
            if (isConsistent(op)) {
                if (op instanceof Insert) {
                    fInserts.add((Insert) op);
                } else if (op instanceof Delete) {
                    fDeletes.add((Delete) op);
                } else if (op instanceof Move) {
                    fMoves.add((Move) op);
                } else {
                    fUpdates.add((Update) op);
                }
            }
        }
    }

    private boolean isConsistent(SourceCodeChange op) {
        boolean result = op.getChangedEntity() != null;
        result &= op.getParentEntity() != null;
        result &= op.getRootEntity() != null;
        if (op instanceof Move) {
            result &= ((Move) op).getNewEntity() != null;
            result &= ((Move) op).getNewParentEntity() != null;
        } else if (op instanceof Update) {
            result &= ((Update) op).getNewEntity() != null;
        }
        return result;
    }

}
