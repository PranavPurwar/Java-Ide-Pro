/* Decompiler 2ms, total 479ms, lines 20 */
package com.duy.ide.javaide.editor.format;

import android.content.Context;
import android.support.annotation.Nullable;
import com.duy.ide.code.api.CodeFormatter;
import com.duy.ide.code.format.CodeFormatProviderImpl;
import com.duy.ide.editor.IEditorDelegate;
import java.io.File;

public class JavaIdeCodeFormatProvider extends CodeFormatProviderImpl {
   public JavaIdeCodeFormatProvider(Context var1) {
      super(var1);
   }

   @Nullable
   public CodeFormatter getFormatterForFile(File var1, IEditorDelegate var2) {
      return (CodeFormatter)(var1.isFile() && var1.getName().endsWith(".java") ? new JavaFormatter(this.getContext()) : super.getFormatterForFile(var1, var2));
   }
}