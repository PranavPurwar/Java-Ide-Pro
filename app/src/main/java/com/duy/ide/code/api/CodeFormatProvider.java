/* Decompiler 0ms, total 459ms, lines 11 */
package com.duy.ide.code.api;

import android.support.annotation.Nullable;
import com.duy.ide.editor.IEditorDelegate;
import java.io.File;

public interface CodeFormatProvider {
   @Nullable
   CodeFormatter getFormatterForFile(File var1, IEditorDelegate var2);
}