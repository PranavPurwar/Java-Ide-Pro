package com.duy.ide.javaide.editor.autocomplete.parser;

import android.support.annotation.Nullable;

import com.android.annotations.NonNull;
import com.duy.common.interfaces.Filter;

import java.util.List;

public interface IClassManager {
    void update(IClass clazz);

    void remove(String fullClassName);

    @NonNull
    List<IClass> find(@NonNull String simpleNamePrefix,
                      @Nullable Filter<IClass> filter);
}
