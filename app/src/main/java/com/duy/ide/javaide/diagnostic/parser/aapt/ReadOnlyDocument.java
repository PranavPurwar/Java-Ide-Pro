/* Decompiler 13ms, total 491ms, lines 113 */
package com.duy.ide.javaide.diagnostic.parser.aapt;

import com.duy.ide.diagnostic.model.SourcePosition;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.List;

class ReadOnlyDocument {
   private final String mFileContents;
   private File myFile;
   private long myLastModified;
   private final List<Integer> myOffsets;

   ReadOnlyDocument(File var1) throws IOException {
      String var2 = Files.toString(var1, Charsets.UTF_8);
      String var3 = var2;
      if (var2.startsWith("\ufeff")) {
         var3 = var2.substring(1);
      }

      this.mFileContents = var3;
      this.myFile = var1;
      this.myLastModified = var1.lastModified();
      this.myOffsets = Lists.newArrayListWithExpectedSize(this.mFileContents.length() / 30);

      for(int var4 = 0; var4 < this.mFileContents.length(); ++var4) {
         if (this.mFileContents.charAt(var4) == '\n') {
            this.myOffsets.add(var4 + 1);
         }
      }

   }

   char charAt(int var1) {
      return this.mFileContents.charAt(var1);
   }

   int findText(String var1, int var2) {
      Preconditions.checkPositionIndex(var2, this.mFileContents.length());
      return this.mFileContents.indexOf(var1, var2);
   }

   int findTextBackwards(String var1, int var2) {
      Preconditions.checkPositionIndex(var2, this.mFileContents.length());
      return this.mFileContents.lastIndexOf(var1, var2);
   }

   String getContents() {
      return this.mFileContents;
   }

   public boolean isStale() {
      long var1 = this.myFile.lastModified();
      boolean var3;
      if (var1 != 0L && this.myLastModified >= var1) {
         var3 = false;
      } else {
         var3 = true;
      }

      return var3;
   }

   int length() {
      return this.mFileContents.length();
   }

   int lineNumber(int var1) {
      for(int var2 = 0; var2 < this.myOffsets.size(); ++var2) {
         if (var1 < (Integer)this.myOffsets.get(var2)) {
            return var2;
         }
      }

      return -1;
   }

   int lineOffset(int var1) {
      --var1;
      return var1 >= 0 && var1 < this.myOffsets.size() ? (Integer)this.myOffsets.get(var1) : -1;
   }

   SourcePosition sourcePosition(int var1) {
      int var2 = 0;

      for(int var3 = 0; var3 < this.myOffsets.size(); ++var3) {
         if (var1 < (Integer)this.myOffsets.get(var3)) {
            if (var3 != 0) {
               var2 = (Integer)this.myOffsets.get(var3 - 1);
            }

            return new SourcePosition(var3, var1 - var2, var1);
         }
      }

      return SourcePosition.UNKNOWN;
   }

   String subsequence(int var1, int var2) {
      String var3 = this.mFileContents;
      int var4 = var2;
      if (var2 == -1) {
         var4 = this.mFileContents.length();
      }

      return var3.substring(var1, var4);
   }
}