package edu.gatech.mbsec.adapter.magicdraw.editor;

import java.util.Objects;
import java.util.Set;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.AnnotationMemberValue;
import javassist.bytecode.annotation.MemberValue;

/**
 * An {@code Annotation} {@link MemberEditor member editor}.
 * @author rherrera
 */
public class AnnotationMemberEditor
        extends WrapperMemberEditor<AnnotationMemberValue> {
    /**
     * The annotation type to search.
     */
    private final String annotation;
    /**
     * The annotation's member to modify.
     */
    private final String member;
    /**
     * Constructs an instance specifying all properties.
     * @param annotation the annotation type to search.
     * @param innerEditor the inner editor of this editor.
     * @param member the annotation's member to modify.
     */
    public AnnotationMemberEditor(String annotation,
            MemberEditor<? extends MemberValue> innerEditor, String member) {
        super(innerEditor);
        this.annotation = Objects.requireNonNull(annotation);
        this.member = Objects.requireNonNull(member);
    }
    /**
     * Convenient constructor to set {@code value} as default {@code member}.
     * @param annotation the annotation type to search.
     * @param innerEditor the inner editor of this editor.
     */
    public AnnotationMemberEditor(String annotation,
            MemberEditor<? extends MemberValue> innerEditor) {
        this(annotation, innerEditor, "value");
    }
    /**
     * Gets the annotation type to search.
     * @return the annotation type to search.
     */
    public String getAnnotation() {
        return annotation;
    }
    /**
     * The annotation's member to modify.
     * @return the annotation's member to modify.
     */
    public String getMember() {
        return member;
    }

    @Override
    public boolean canEdit(MemberValue member) {
        return member instanceof AnnotationMemberValue;
    }
    /**
     * Gets the updated annotation given the old value.
     * @param oldAnnotation the old annotation.
     * @param pool the underlying constant's pool.
     * @return the same {@code oldAnnotation} instance if none change was made;
     * an updated new instance otherwise.
     */
    private Annotation getAnnotation(Annotation oldAnnotation, ConstPool pool) {
        MemberValue value, newValue;
        boolean edited = false;
        MemberEditor editor = super.getInnerEditor();
        Set<String> members = oldAnnotation.getMemberNames();
        Annotation newAnnotation = new Annotation(annotation, pool);
        for (String currentMember : members) {
            value = oldAnnotation.getMemberValue(currentMember);
            if (member.equals(currentMember) && editor.canEdit(value)) {
                newValue = editor.edit(value, member, pool);
                if (newValue != value) {
                    value = newValue;
                    edited = true;
                }
            }
            newAnnotation.addMemberValue(currentMember, value);
        }
        return edited ? newAnnotation : oldAnnotation;
    }

    @Override
    public AnnotationMemberValue edit(AnnotationMemberValue old,
            String name, ConstPool pool) {
        boolean edited = false;
        Annotation newAnnotation = null;
        Annotation oldAnnotation = old.getValue();
        if (annotation.equals(oldAnnotation.getTypeName())) {
            newAnnotation = getAnnotation(oldAnnotation, pool);
            edited = newAnnotation != oldAnnotation;
        }
        return edited ? new AnnotationMemberValue(newAnnotation, pool) : old;
    }

}