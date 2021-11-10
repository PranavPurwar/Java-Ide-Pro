package com.duy.ide.javaide.editor.autocomplete.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.duy.ide.editor.view.IEditAreaView;

public class KeywordDescription extends JavaSuggestItemImpl {
    private final String keyword;

    public KeywordDescription(String keyword) {
        this.keyword = keyword;
    }

    @Nullable
    @Override
    public String getName() {
        return keyword;
    }

    @Nullable
    @Override
    public String getDescription() {
        return null;
    }

    @Nullable
    @Override
    public String getReturnType() {
        return null;
    }

    @Override
    public char getTypeHeader() {
        return 0;
    }

    @Override
    public void onSelectThis(@NonNull IEditAreaView iEditAreaView) {
        //always add space
        insertImpl(iEditAreaView, keyword + " ");
    }
}
