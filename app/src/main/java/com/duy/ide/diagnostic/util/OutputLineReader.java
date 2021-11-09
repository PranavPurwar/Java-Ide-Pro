package com.duy.ide.diagnostic.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.regex.Pattern;

public class OutputLineReader {
   private static final Pattern LINE_BREAK = Pattern.compile("\\r?\\n");
   private final int myLineCount;
   @NonNull
   private final String[] myLines;
   private int myPosition;

   public OutputLineReader(@NonNull String str) {
      this.myLines = LINE_BREAK.split(str);
      this.myLineCount = this.myLines.length;
   }

   public int getLineCount() {
      return this.myLineCount;
   }

   public boolean hasNextLine() {
      if (myPosition >= myLineCount - 1) {
         return false;
      }
      return true;
   }

   @Nullable
   public String peek(int pos) {
      pos += this.myPosition;
      return pos >= 0 && pos < this.myLineCount ? this.myLines[pos] : null;
   }

   public void pushBack(@NonNull String unused) {
      --this.myPosition;
   }

   @Nullable
   public String readLine() {
      if (this.myPosition >= 0 && this.myPosition < this.myLineCount) {
         String[] lines = this.myLines;
         int next = this.myPosition++;
         return lines[next];
      } else {
         return null;
      }
   }

   public void skipNextLine() {
      ++this.myPosition;
   }
}