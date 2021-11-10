package com.duy.ide.javaide.editor.autocomplete.model;


import android.text.Editable;

import com.android.annotations.NonNull;
import com.duy.ide.code.api.SuggestItem;
import com.duy.ide.editor.internal.suggestion.Editor;
import com.duy.ide.editor.view.IEditAreaView;


public abstract class JavaSuggestItemImpl implements SuggestItem, Cloneable {
    public static final int FIELD_DESC = 0;
    public static final int METHOD_DESC = 1;
    public static final int CLASS_DESC = 2;
    public static final int OTHER_DESC = 3;

    private Editor editor;
    private String incomplete;

    @Override
    public int getSuggestionPriority() {
        return OTHER_DESC;
    }

    public Editor getEditor() {
        return editor;
    }

    public void setEditor(Editor editor) {
        this.editor = editor;
    }

    public String getIncomplete() {
        return incomplete;
    }

    public void setIncomplete(@NonNull String incomplete) {
        this.incomplete = incomplete;
    }


    protected void insertImpl(IEditAreaView editorView, String text) {

        try {
            final int length = getIncomplete().length();
            final int cursor = getEditor().getCursor();
            final int start = cursor - length;

            Editable editable = editorView.getEditableText();
            editable.replace(start, cursor, text);
            editorView.setSelection(start + text.length());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
