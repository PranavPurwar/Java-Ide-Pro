package com.duy.ide.javaide.editor.format;

import android.content.Context;
import android.support.annotation.Nullable;
import com.duy.ide.code.api.CodeFormatter;
import com.duy.ide.code.format.CodeFormatProviderImpl;
import com.duy.ide.editor.IEditorDelegate;
import java.io.File;

public class JavaIdeCodeFormatProvider extends CodeFormatProviderImpl {
   public JavaIdeCodeFormatProvider(Context context) {
      super(context);
   }

   @Nullable
   public CodeFormatter getFormatterForFile(File file, IEditorDelegate delegate) {
      return (CodeFormatter)(file.isFile() && file.getName().endsWith(".java") ? new JavaFormatter(this.getContext()) : super.getFormatterForFile(file, delegate));
   }
}