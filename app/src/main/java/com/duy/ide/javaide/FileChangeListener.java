/* Decompiler 0ms, total 479ms, lines 12 */
package com.duy.ide.javaide;

import java.io.File;

public interface FileChangeListener {
   void doOpenFile(File var1);

   void onFileCreated(File var1);

   void onFileDeleted(File var1);
}