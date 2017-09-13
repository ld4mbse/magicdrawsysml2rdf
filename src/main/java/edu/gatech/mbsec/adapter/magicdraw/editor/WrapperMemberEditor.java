package edu.gatech.mbsec.adapter.magicdraw.editor;

import java.util.Objects;
import javassist.bytecode.annotation.MemberValue;

/**
 * Base clase for {@link MemberEditor editor} having an inner editor.
 * @param <T> the annotation's member type.
 * @author rherrera
 */
public abstract class WrapperMemberEditor<T extends MemberValue>
        implements MemberEditor<T> {
    /**
     * The inner editor of this editor.
     */
    private final MemberEditor innerEditor;
    /**
     * Constructs an instance specifying the inner editor of this instace.
     * @param innerEditor the inner editor of this instance.
     */
    protected WrapperMemberEditor(MemberEditor<? extends MemberValue> innerEditor) {
        this.innerEditor = Objects.requireNonNull(innerEditor);
    }
    /**
     * Gets the inner editor of this instance.
     * @return the inner editor of this instance.
     */
    public MemberEditor getInnerEditor() {
        return innerEditor;
    }
}