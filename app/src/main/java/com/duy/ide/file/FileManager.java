/* Decompiler 3ms, total 1760ms, lines 32 */
package com.duy.ide.file;

import android.content.Context;
import com.jecelyin.common.utils.SysUtils;
import java.io.File;
import java.io.IOException;

public class FileManager {
   private Context context;

   public FileManager(Context context) {
      this.context = context;
   }

   public File createNewFile(String fileName) {
      File file = new File(this.getApplicationDir(), fileName);
      file.getParentFile().mkdirs();

      try {
         file.createNewFile();
      } catch (IOException e) {
         e.printStackTrace();
      }

      return null;
   }

   public File getApplicationDir() {
      return new File(SysUtils.getAppStoragePath(this.context));
   }
}