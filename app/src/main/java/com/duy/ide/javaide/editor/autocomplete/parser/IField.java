package com.duy.ide.javaide.editor.autocomplete.parser;

import com.android.annotations.NonNull;
import com.android.annotations.Nullable;
import com.duy.ide.code.api.SuggestItem;

public interface IField extends SuggestItem {
    @NonNull
    String getFieldName();

    @NonNull
    IClass getFieldType();

    int getFieldModifiers();

    @Nullable
    Object getFieldValue();
}
