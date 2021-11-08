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

   public void setEncoding(String encoding) {
      this.encoding = encoding;
   }

   public void setLastOpen(boolean lastOpen) {
      this.isLastOpen = lastOpen;
   }

   public void setOffset(int off) {
      this.offset = off;
   }

   public void setPath(String path) {
      this.path = path;
   }

   public void setTime(long time) {
      this.time = time;
   }
}