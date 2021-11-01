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

   public CodeFormatProviderImpl(Context context) {
      this.context = context;
   }

   public Context getContext() {
      return this.context;
   }

   @Nullable
   public CodeFormatter getFormatterForFile(File file, IEditorDelegate var2) {
      if (file.exists() && file.isFile()) {
         if (file.getName().equals(".xml")) {
            return new XmlFormatter();
         } else {
            return file.getName().endsWith(".json") ? new JsonFormatter() : null;
         }
      } else {
         return null;
      }
   }
}