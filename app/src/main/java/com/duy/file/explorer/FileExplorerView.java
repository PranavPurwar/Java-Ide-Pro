/* Decompiler 0ms, total 427ms, lines 17 */
package com.duy.file.explorer;

import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;

public interface FileExplorerView {
   void filter(String var1);

   void finish();

   void refresh();

   void setSelectAll(boolean var1);

   ActionMode startActionMode(Callback var1);
}