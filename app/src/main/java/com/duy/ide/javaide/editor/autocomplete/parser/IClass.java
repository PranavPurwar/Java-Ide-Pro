package com.duy.ide.javaide.editor.autocomplete.parser;

import com.android.annotations.NonNull;
import com.android.annotations.Nullable;
import com.duy.ide.code.api.SuggestItem;
import com.duy.ide.javaide.editor.autocomplete.model.ConstructorDescription;

import java.util.ArrayList;
import java.util.List;

public interface IClass extends SuggestItem {
    int getModifiers();

    @Nullable
    String getFullClassName();

    @Nullable
    String getSimpleName();

    boolean isInterface();

    boolean isEnum();

    boolean isPrimitive();

    boolean isAnnotation();

    @Nullable
    IMethod getMethod(@NonNull String methodName, @Nullable IClass[] argsType);

    @Nullable
    IField getField(@NonNull String name);

    List<IMethod> getMethods();

    @Nullable
    IClass getSuperclass();

    ArrayList<IField> getFields();

    List<SuggestItem> getMember(String prefix);

    List<ConstructorDescription> getConstructors();
}
