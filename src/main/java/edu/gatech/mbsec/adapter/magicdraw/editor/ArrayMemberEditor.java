package edu.gatech.mbsec.adapter.magicdraw.editor;

import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.MemberValue;

/**
 * An {@code array} {@link MemberEditor member editor}.
 * @author rherrera
 */
public class ArrayMemberEditor extends WrapperMemberEditor<ArrayMemberValue> {
    /**
     * Constructs an instance specifying the element's editor.
     * @param elementEditor the element's editor of this editor.
     */
    public ArrayMemberEditor(MemberEditor<? extends MemberValue> elementEditor) {
        super(elementEditor);
    }

    @Override
    public boolean canEdit(MemberValue member) {
        return member instanceof ArrayMemberValue;
    }

    @Override
    public ArrayMemberValue edit(ArrayMemberValue old, String name,
            ConstPool pool) {
        boolean edited = false;
        MemberEditor elementEditor = super.getInnerEditor();
        ArrayMemberValue value = new ArrayMemberValue(pool);
        MemberValue newValue, values[] = old.getValue();
        for(int i = 0; i < values.length; i++) {
            if (elementEditor.canEdit(values[i])) {
                newValue = elementEditor.edit(values[i], name+"["+i+"]", pool);
                if (newValue != values[i]) {
                    values[i] = newValue;
                    edited = true;
                }
            }
        }
        value.setValue(values);
        return edited ? value : old;
    }
}