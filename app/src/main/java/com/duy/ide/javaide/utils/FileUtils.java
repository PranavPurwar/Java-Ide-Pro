/* Decompiler 6ms, total 517ms, lines 56 */
package com.duy.ide.javaide.utils;

import java.io.File;

public class FileUtils {
   public static boolean canEdit(File var0) {
      boolean var1 = false;
      boolean var2 = var1;
      if (var0.canWrite()) {
         var2 = var1;
         if (hasExtension(var0, ".java", ".xml", ".txt", ".gradle", ".json")) {
            var2 = true;
         }
      }

      return var2;
   }

   public static String fileExt(String var0) {
      String var1 = var0;
      if (var0.contains("?")) {
         var1 = var0.substring(0, var0.indexOf("?"));
      }

      if (var1.lastIndexOf(".") == -1) {
         return null;
      } else {
         var1 = var1.substring(var1.lastIndexOf(".") + 1);
         var0 = var1;
         if (var1.contains("%")) {
            var0 = var1.substring(0, var1.indexOf("%"));
         }

         var1 = var0;
         if (var0.contains("/")) {
            var1 = var0.substring(0, var0.indexOf("/"));
         }

         return var1.toLowerCase();
      }
   }

   public static boolean hasExtension(File var0, String... var1) {
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         String var4 = var1[var3];
         if (var0.getPath().toLowerCase().endsWith(var4.toLowerCase())) {
            return true;
         }
      }

      return false;
   }
}