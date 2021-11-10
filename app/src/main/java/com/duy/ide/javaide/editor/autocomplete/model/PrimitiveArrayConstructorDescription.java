package com.duy.ide.javaide.editor.autocomplete.model;

import android.support.annotation.NonNull;
import android.text.Editable;

import com.duy.ide.editor.view.IEditAreaView;


public class PrimitiveArrayConstructorDescription extends JavaSuggestItemImpl {
    private String name;

    public PrimitiveArrayConstructorDescription(String name) {
        this.name = name;
    }

    @Override
    public void onSelectThis(@NonNull IEditAreaView editorView) {
        try {
            final int length = getIncomplete().length();
            int cursor = getEditor().getCursor();
            final int start = cursor - length;

            Editable editable = editorView.getEditableText();
            editable.replace(start, cursor, name + "[]");
            editorView.setSelection(start + name.length() + 1 /*between two bracket*/);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return name + "[]";
    }

    @Override
    public char getTypeHeader() {
        return 'a';
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getReturnType() {
        return "";
    }


    @Override
    public String toString() {
        return name + "[]";
    }

}
