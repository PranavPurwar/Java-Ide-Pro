package com.duy.ide.javaide.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.support.v4.content.FileProvider;
import com.duy.ide.javaide.setting.AppSetting;
import java.io.File;

public class RootUtils {

/*
 * try installing the built apk
 * @returns boolean whether the apk was installed or not
 * @param context the context object
 * @param file the apk file to install
 */

   public static boolean installApk(Context context, File file) {
      if (file.exists() && file.isFile() && file.canRead()) {
         if (new AppSetting(context).installViaRootAccess()) {
            if (installWithoutPrompt(file)) {
               return true;
            }
         } else {
            openApk(context, file);
         }
         return false;
      } else {
         return false;
      }
   }

/*
 * Install built apk on rooted devices
 * it runs adb to install the apk, skipping apk verification
 * @returns whether the apk was installed or not
 * @param file the built apk as an file object
 */

   private static boolean installWithoutPrompt(File file) {
      try {
         String path = file.getPath();
         Runtime.getRuntime().exec(new String[]{"su", "-c", "adb install -r " + path}).waitFor();
         return true;
      } catch (Exception e) {
         e.printStackTrace();
         return false;
      }
   }

/*
 * Request to install the built apk
 * @param context context to use for retrieving the package name
 * @param file the built apk as an file object
 */
   private static void openApk(Context context, File file) {
      Uri uri;
      Intent intent;
      if (VERSION.SDK_INT >= 24) {
         uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
         intent = new Intent("android.intent.action.INSTALL_PACKAGE");
         intent.setData(uri);
         intent.setFlags(1);
         context.startActivity(intent);
      } else {
         uri = Uri.fromFile(file);
         intent = new Intent("android.intent.action.VIEW");
         intent.setDataAndType(uri, "application/vnd.android.package-archive");
         intent.setFlags(268435456);
         context.startActivity(intent);
      }

   }
}