package edu.gatech.mbsec.adapter.magicdraw.editor;

import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.MemberValue;

/**
 * An annotation's member editor.
 * @param <T> the annotation's member type.
 * @author rherrera
 */
public interface MemberEditor<T extends MemberValue> {
    /**
     * Determines whether a {@link MemberValue} is editable by this editor.
     * @param member the {@code MemberValue} to test.
     * @return {@code true} if this editor can edit {@code member};
     * {@code false} otherwise.
     */
    public boolean canEdit(MemberValue member);
    /**
     * Edits an element and return the modified version.
     * @param old the previous member value.
     * @param name the name of the member.
     * @param pool the underlying constant's pool.
     * @return different instance from {@code old} if the update was done, the
     * same instance if, by some reason, the update was not perfomed.
     */
    public T edit(T old, String name, ConstPool pool);
}