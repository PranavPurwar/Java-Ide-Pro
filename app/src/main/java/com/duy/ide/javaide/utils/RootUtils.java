/* Decompiler 4ms, total 496ms, lines 65 */
package com.duy.ide.javaide.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.support.v4.content.FileProvider;
import com.duy.ide.javaide.setting.AppSetting;
import java.io.File;

public class RootUtils {
   public static boolean installApk(Context var0, File var1) {
      if (var1.exists() && var1.isFile() && var1.canRead()) {
         if ((new AppSetting(var0)).installViaRootAccess()) {
            if (installWithoutPrompt(var1)) {
               return true;
            }
         } else {
            openApk(var0, var1);
         }

         return false;
      } else {
         return false;
      }
   }

   private static boolean installWithoutPrompt(File var0) {
      try {
         String var1 = var0.getPath();
         StringBuilder var3 = new StringBuilder();
         var3.append("adb install -r ");
         var3.append(var1);
         String var4 = var3.toString();
         Runtime.getRuntime().exec(new String[]{"su", "-c", var4}).waitFor();
         return true;
      } catch (Exception var2) {
         var2.printStackTrace();
         return false;
      }
   }

   private static void openApk(Context var0, File var1) {
      Uri var3;
      Intent var4;
      if (VERSION.SDK_INT >= 24) {
         StringBuilder var2 = new StringBuilder();
         var2.append(var0.getPackageName());
         var2.append(".provider");
         var3 = FileProvider.getUriForFile(var0, var2.toString(), var1);
         var4 = new Intent("android.intent.action.INSTALL_PACKAGE");
         var4.setData(var3);
         var4.setFlags(1);
         var0.startActivity(var4);
      } else {
         var3 = Uri.fromFile(var1);
         var4 = new Intent("android.intent.action.VIEW");
         var4.setDataAndType(var3, "application/vnd.android.package-archive");
         var4.setFlags(268435456);
         var0.startActivity(var4);
      }

   }
}