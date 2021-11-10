package com.duy.ide.javaide.editor.autocomplete.internal;

import com.duy.ide.code.api.SuggestItem;
import com.duy.ide.editor.internal.suggestion.Editor;
import com.duy.ide.javaide.editor.autocomplete.parser.IClass;
import com.duy.ide.javaide.editor.autocomplete.parser.IMethod;
import com.duy.ide.javaide.editor.autocomplete.parser.JavaClassManager;
import com.duy.ide.javaide.editor.autocomplete.parser.JavaDexClassLoader;
import com.duy.ide.javaide.utils.DLog;
import com.sun.tools.javac.tree.JCTree;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CompleteString extends JavaCompleteMatcherImpl {
    public static final Pattern STRING_DOT =
            Pattern.compile("\"\\s*\\.\\s*$", Pattern.MULTILINE);
    public static final Pattern STRING_DOT_EXPR
            = Pattern.compile("\"\\s*\\.\\s*(" + METHOD_NAME.pattern() + ")$");
    private static final String TAG = "CompleteString";
    private final JavaDexClassLoader mClassLoader;

    public CompleteString(JavaDexClassLoader classLoader) {
        mClassLoader = classLoader;
    }

    @Override
    public boolean process(JCTree.JCCompilationUnit ast, Editor editor, Expression expression, String statement, ArrayList<SuggestItem> suggestItems) {
        Matcher matcher = STRING_DOT.matcher(statement);
        if (matcher.find()) {
            if (DLog.DEBUG) DLog.d(TAG, "process: string dot found");
            getSuggestion(editor, "", suggestItems);
            return true;
        }
        matcher = STRING_DOT_EXPR.matcher(statement);
        if (matcher.find()) {
            if (DLog.DEBUG) DLog.d(TAG, "process: string dot expr found");
            String incompleteMethod = matcher.group(1);
            getSuggestion(editor, incompleteMethod, suggestItems);
            return true;
        }
        return false;
    }

    /**
     * Complete string only filter method, not filter field or constructor
     */
    @Override
    public void getSuggestion(Editor editor, String incomplete, List<SuggestItem> suggestItems) {
        JavaClassManager reader = mClassLoader.getClassReader();
        IClass stringClass = reader.getParsedClass(String.class.getName());
        assert stringClass != null;
        ArrayList<SuggestItem> methods = new ArrayList<>();
        for (IMethod method : stringClass.getMethods()) {
            if (method.getMethodName().startsWith(incomplete)) {
                suggestItems.add(method);
            }
        }
        setInfo(methods, editor, incomplete);
        suggestItems.addAll(methods);
    }
}
