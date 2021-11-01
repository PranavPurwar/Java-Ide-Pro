/* Decompiler 3ms, total 393ms, lines 36 */
package com.duy.ide.code.format;

import android.content.Context;
import android.support.annotation.Nullable;
import com.duy.ide.code.api.CodeFormatProvider;
import com.duy.ide.code.api.CodeFormatter;
import com.duy.ide.code.model.JsonFormatter;
import com.duy.ide.code.model.XmlFormatter;
import com.duy.ide.editor.IEditorDelegate;
import java.io.File;

public class CodeFormatProviderImpl implements CodeFormatProvider {
   private Context context;

   public CodeFormatProviderImpl(Context var1) {
      this.context = var1;
   }

   public Context getContext() {
      return this.context;
   }

   @Nullable
   public CodeFormatter getFormatterForFile(File var1, IEditorDelegate var2) {
      if (var1.exists() && !var1.isDirectory()) {
         if (var1.getName().equals(".xml")) {
            return new XmlFormatter();
         } else {
            return var1.getName().endsWith(".json") ? new JsonFormatter() : null;
         }
      } else {
         return null;
      }
   }
}