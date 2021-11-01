/* Decompiler 5ms, total 2253ms, lines 67 */
package com.duy.ide.javaide.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;

public class StoreUtil {
   public static void gotoPlayStore(Activity var0, String var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append("market://details?id=");
      var2.append(var1);
      Intent var4 = new Intent("android.intent.action.VIEW", Uri.parse(var2.toString()));
      var4.addFlags(1207959552);

      try {
         var0.startActivity(var4);
      } catch (ActivityNotFoundException var3) {
         var2 = new StringBuilder();
         var2.append("http://play.google.com/store/apps/details?id=");
         var2.append(var1);
         var0.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(var2.toString())));
      }

   }

   public static void gotoPlayStore(Activity var0, String var1, int var2) {
      StringBuilder var3 = new StringBuilder();
      var3.append("market://details?id=");
      var3.append(var1);
      Intent var5 = new Intent("android.intent.action.VIEW", Uri.parse(var3.toString()));
      var5.addFlags(1207959552);

      try {
         var0.startActivityForResult(var5, var2);
      } catch (ActivityNotFoundException var4) {
         var3 = new StringBuilder();
         var3.append("http://play.google.com/store/apps/details?id=");
         var3.append(var1);
         var0.startActivityForResult(new Intent("android.intent.action.VIEW", Uri.parse(var3.toString())), var2);
      }

   }

   public static void moreApp(Activity var0) {
      Intent var1 = new Intent("android.intent.action.VIEW", Uri.parse("market://search?q=pub:Trần Lê Duy"));
      var1.addFlags(1207959552);

      try {
         var0.startActivity(var1);
      } catch (ActivityNotFoundException var2) {
         var0.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://play.google.com/store/search?q=pub:Trần Lê Duy")));
      }

   }

   public static void shareApp(Activity var0, String var1) {
      Intent var2 = new Intent("android.intent.action.SEND");
      StringBuilder var3 = new StringBuilder();
      var3.append("http://play.google.com/store/apps/details?id=");
      var3.append(var1);
      var2.putExtra("android.intent.extra.TEXT", var3.toString());
      var2.setType("text/plain");
      var0.startActivity(var2);
   }
}