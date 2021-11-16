package com.duy.ide.javaide.editor.autocomplete.internal;

import android.support.annotation.Nullable;
import android.text.Editable;
import android.util.Log;
import android.widget.EditText;

import com.duy.ide.javaide.editor.autocomplete.util.EditorUtil;
import com.duy.ide.javaide.editor.autocomplete.parser.JavaUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.duy.ide.javaide.editor.autocomplete.internal.PatternFactory.firstMatch;
import static com.duy.ide.javaide.editor.autocomplete.internal.PatternFactory.lastMatch;



public class PackageImporter {
    private static final String TAG = "ImportUtil";

    /**
     * Add import statement if import does not already exist.
     */
    public static void importClass(final Editable editor, String className) {
        String packageName = JavaUtil.getPackageName(className);
        if (getImportedClassName(editor, className) == null
                && !packageName.isEmpty()
                && !packageName.equals("java.lang")
                && !packageName.equals(EditorUtil.getCurrentPackage(editor))) {
            organizeImports(editor, "import " + className + ";");
        }
    }

    public static String getImportedClassName(EditText editor, @Nullable String className) {
        return getImportedClassName(editor, className);
    }

    public static String getImportedClassName(final CharSequence src, @Nullable final String className) {
        if (className == null) return null;

        Pattern pattern = PatternFactory.makeImportClass(className);
        Matcher matcher = pattern.matcher(src);
        if (matcher.find()) {
            return matcher.group(2);
        }
        return PatternFactory.match(src, pattern);
    }


    public static void organizeImports(Editable editor, String importStr) {
        importStr = importStr.replace("$", ".");
        Log.d(TAG, "organizeImports() called with: editor = [" + editor + "], importStr = [" + importStr + "]");

        ArrayList<String> imports = getImports(editor);
        Log.d(TAG, "organizeImports imports = " + imports);

        imports.add(importStr);
        Collections.sort(imports, new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                return s.compareTo(t1);
            }
        });
        StringBuilder imp = new StringBuilder();
        String lastPkg = "";
        for (int i = 0; i < imports.size(); i++) {
            String current = imports.get(i);
            String currentPkg = "";
            if (current.contains(".") && !lastPkg.isEmpty()) {
                currentPkg = current.substring(0, current.indexOf(".")).replaceAll("\\s+", "");
                if (!currentPkg.equals(lastPkg)) {
                    imp.append("\n");
                }
            }
            if (i == imports.size() - 1) {
                imp.append(current);
            } else {
                imp.append(current).append("\n");
            }
            lastPkg = currentPkg;
        }
        int first = firstMatch(editor, PatternFactory.IMPORT);
        int last = PatternFactory.lastMatch(editor, PatternFactory.IMPORT);
        if (first >= 0 && last > first) {
            editor.replace(first, last, imp);
        } else {
            int i = lastMatch(editor, PatternFactory.PACKAGE);
            if (i < 0) {
                editor.insert(0, imp); //insert new
            } else {
                editor.insert(i, "\n\n" + imp + "\n");
            }

        }
    }

    public static ArrayList<String> getImports(Editable editor) {
        return PatternFactory.allMatch(editor, PatternFactory.IMPORT);
    }

}
