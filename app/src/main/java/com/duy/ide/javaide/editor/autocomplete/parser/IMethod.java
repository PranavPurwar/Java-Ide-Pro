package com.duy.ide.javaide.editor.autocomplete.parser;

import com.android.annotations.NonNull;
import com.duy.ide.code.api.SuggestItem;

public interface IMethod extends SuggestItem {
    @NonNull
    String getMethodName();

    @NonNull
    IClass getMethodReturnType();

    @NonNull
    IClass[] getMethodParameterTypes();

    int getModifiers();
}
