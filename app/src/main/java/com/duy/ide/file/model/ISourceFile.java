/* Decompiler 0ms, total 456ms, lines 16 */
package com.duy.ide.file.model;

import android.support.annotation.NonNull;
import java.io.File;

public interface ISourceFile extends IFileObject {
   @NonNull
   File getFile();

   @NonNull
   String getPath();

   @NonNull
   byte[] md5();
}