package edu.gatech.mbsec.adapter.magicdraw.editor;

import java.util.Objects;
import java.util.Set;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.MemberValue;

/**
 * A run-time annotation editor.
 * Searches an specific annotation of one element to modify one of its members.
 * @author rherrera
 */
public class AnnotationEditor {
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
     * @param member the annotation's member to modify.
     */
    public AnnotationEditor (String annotation, String member) {
        this.annotation = Objects.requireNonNull(annotation);
        this.member = Objects.requireNonNull(member);
    }
    /**
     * Convenient constructor to set {@code value} as default {@code member}.
     * @param annotation the annotation type to search.
     */
    public AnnotationEditor (String annotation) {
        this(annotation, "value");
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
    /**
     * Gets a replace for an annotation updating the target member.
     * @param editor the member's editor.
     * @param old the original annotation to be replaced.
     * @param pool the underlying constant pool.
     * @return the same {@code old} instance if none change was made
     * (the target member was not found, etc); a new instance otherwise.
     */
    protected Annotation getAnnotation(MemberEditor editor, Annotation old,
            ConstPool pool) {
        MemberValue value;
        MemberValue newValue;
        boolean edited = false;
        Set<String> members = old.getMemberNames();
        Annotation replace = new Annotation(annotation, pool);
        for (String currentMember : members) {
            value = old.getMemberValue(currentMember);
            if (member.equals(currentMember)) {
                newValue = editor.edit(value, member, pool);
                if (newValue != value) {
                    value = newValue;
                    edited = true;
                }
            }
            replace.addMemberValue(currentMember, value);
        }
        return edited ? replace : old;
    }
    /**
     * Changes the value of the target annotation's member if found.
     * @param info the annotated element's information.
     * @param editor the member's editor.
     * @return {@code true} if the edition was made, {@code false} otherwise.
     */
    public boolean edit(AttributeInfo info, MemberEditor editor) {
        Annotation newAnnotation, annotations[];
        AnnotationsAttribute annotated;
        ConstPool pool;
        boolean edited = false;
        if (info instanceof AnnotationsAttribute) {
            pool = info.getConstPool();
            annotated = (AnnotationsAttribute)info;
            annotations = annotated.getAnnotations();
            if (annotations != null) {
                for(int i = 0; i<annotations.length; i++) {
                    if (annotation.equals(annotations[i].getTypeName())) {
                        newAnnotation = getAnnotation(editor, annotations[i], pool);
                        if (newAnnotation != annotations[i]) {
                            annotations[i] = newAnnotation;
                            edited = true;
                        }
                    }
                }
                annotated.setAnnotations(annotations);
            }
        }
        return edited;
    }

}