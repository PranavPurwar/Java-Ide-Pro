/* Decompiler 0ms, total 649ms, lines 12 */
package com.duy.ide.file;

import android.support.annotation.UiThread;

public interface SaveListener {
   @UiThread
   void onSaveFailed(Exception exception);

   @UiThread
   void onSavedSuccess();
}