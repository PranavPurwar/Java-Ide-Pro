/* Decompiler 1ms, total 459ms, lines 13 */
package com.duy.ide.file;

import android.text.SpannableStringBuilder;
import com.jecelyin.editor.v2.io.FileReader;

public interface ReadFileListener {
   SpannableStringBuilder onAsyncReaded(FileReader reader, boolean b);

   void onDone(SpannableStringBuilder builder, boolean b);

   void onStart();
}