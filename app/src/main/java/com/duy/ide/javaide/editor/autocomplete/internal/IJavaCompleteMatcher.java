package com.duy.ide.javaide.editor.autocomplete.internal;

import com.android.annotations.NonNull;
import com.duy.ide.code.api.SuggestItem;
import com.duy.ide.editor.internal.suggestion.Editor;
import com.sun.tools.javac.tree.JCTree;

import java.util.ArrayList;
import java.util.List;

public interface IJavaCompleteMatcher {
    boolean process(JCTree.JCCompilationUnit ast, Editor editor, Expression expression, String statement, ArrayList<SuggestItem> result)
            throws Exception;

    void getSuggestion(@NonNull Editor editor,
                       @NonNull String incomplete,
                       @NonNull List<SuggestItem> suggestItems);
}
