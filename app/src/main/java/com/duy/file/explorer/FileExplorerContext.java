/* Decompiler 1ms, total 494ms, lines 21 */
package com.duy.file.explorer;

import android.content.Context;
import android.content.res.Resources;

public class FileExplorerContext {
   private final Context context;

   public FileExplorerContext(Context var1) {
      this.context = var1;
   }

   public Context getContext() {
      return this.context;
   }

   public Resources getResources() {
      return this.context.getResources();
   }
}