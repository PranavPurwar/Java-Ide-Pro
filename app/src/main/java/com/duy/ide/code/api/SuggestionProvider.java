/* Decompiler 0ms, total 468ms, lines 9 */
package com.duy.ide.code.api;

import com.duy.ide.editor.internal.suggestion.Editor;
import java.util.ArrayList;

public interface SuggestionProvider {
   ArrayList<SuggestItem> getSuggestions(Editor var1);
}