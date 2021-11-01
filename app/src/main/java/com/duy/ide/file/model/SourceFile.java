/* Decompiler 7ms, total 880ms, lines 69 */
package com.duy.ide.file.model;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public class SourceFile implements ISourceFile {
   private final File file;
   private final byte[] md5;

   public SourceFile(File var1, byte[] var2) {
      this.file = var1;
      this.md5 = var2;
   }

   @NonNull
   public File getFile() {
      return this.file;
   }

   @NonNull
   public String getPath() {
      return this.file.getPath();
   }

   @NonNull
   public byte[] md5() {
      return this.md5;
   }

   public InputStream openInputStream() throws IOException {
      return new FileInputStream(this.file);
   }

   public FileOutputStream openOutputStream() throws FileNotFoundException {
      return new FileOutputStream(this.file);
   }

   public Reader openReader() throws IOException {
      return new FileReader(this.file);
   }

   public Writer openWriter() throws IOException {
      return new FileWriter(this.file);
   }

   @Nullable
   public Uri toUri() {
      return Uri.fromFile(this.file);
   }
}