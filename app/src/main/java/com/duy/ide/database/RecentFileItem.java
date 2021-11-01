/* Decompiler 4ms, total 616ms, lines 50 */
package com.duy.ide.database;

public class RecentFileItem {
   public String encoding;
   public boolean isLastOpen;
   public int offset;
   public String path;
   public long time;

   public String getEncoding() {
      return this.encoding;
   }

   public int getOffset() {
      return this.offset;
   }

   public String getPath() {
      return this.path;
   }

   public long getTime() {
      return this.time;
   }

   public boolean isLastOpen() {
      return this.isLastOpen;
   }

   public void setEncoding(String var1) {
      this.encoding = var1;
   }

   public void setLastOpen(boolean var1) {
      this.isLastOpen = var1;
   }

   public void setOffset(int var1) {
      this.offset = var1;
   }

   public void setPath(String var1) {
      this.path = var1;
   }

   public void setTime(long var1) {
      this.time = var1;
   }
}