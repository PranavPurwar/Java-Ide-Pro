/* Decompiler 0ms, total 407ms, lines 23 */
package com.duy.ide.code.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.duy.ide.editor.view.IEditAreaView;

public interface SuggestItem {
   @Nullable
   String getDescription();

   @Nullable
   String getName();

   @Nullable
   String getReturnType();

   int getSuggestionPriority();

   char getTypeHeader();

   void onSelectThis(@NonNull IEditAreaView var1);
}