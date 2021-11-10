package com.duy.ide.javaide.editor.autocomplete.model;

import android.support.annotation.NonNull;

import com.android.annotations.Nullable;
import com.duy.ide.editor.view.IEditAreaView;
import com.duy.ide.javaide.editor.autocomplete.parser.IClass;
import com.duy.ide.javaide.editor.autocomplete.parser.IField;
import com.duy.ide.javaide.editor.autocomplete.parser.JavaClassManager;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


public class FieldDescription extends JavaSuggestItemImpl implements Member, IField {
    @NonNull
    private String mName;
    @NonNull
    private IClass mType;
    private int mModifiers;
    @Nullable
    private String mValue;

    public FieldDescription(int modifiers, IClass type, @NonNull String name, String initValue) {
        mName = name;
        mType = type;
        mModifiers = modifiers;
        mValue = initValue;
    }

    public FieldDescription(Field field) {
        mName = field.getName();
        mType = JavaClassManager.getInstance().getClassWrapper(field.getType());
        mModifiers = field.getModifiers();

        if (Modifier.isStatic(mModifiers)) {
            try {
                boolean primitive = field.getType().isPrimitive();
                Object o = field.get(null);
                if (primitive) {
                    mValue = o.toString();
                } else {
                    mValue = o.getClass().getSimpleName();
                }
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void onSelectThis(@NonNull IEditAreaView editorView) {
        insertImpl(editorView, mName);
    }


    @Override
    public char getTypeHeader() {
        return 'f'; //field
    }

    @Override
    public String getName() {
        if (mValue == null) {
            return mName;
        } else {
            return mName + "(" + mValue + ")";
        }
    }

    @Override
    public String getDescription() {
        return mValue;
    }

    @Override
    public String getReturnType() {
        if (mType == null) {
            return "";
        }
        return mType.getSimpleName();
    }

    @Override
    public int getSuggestionPriority() {
        return JavaSuggestItemImpl.FIELD_DESC;
    }

    @Override
    public String toString() {
        return mName;
    }


    @Override
    public int getModifiers() {
        return mModifiers;
    }

    @Override
    public String getFieldName() {
        return mName;
    }

    @Override
    public IClass getFieldType() {
        return mType;
    }

    @Override
    public int getFieldModifiers() {
        return mModifiers;
    }

    @Override
    public Object getFieldValue() {
        return mValue;
    }
}
