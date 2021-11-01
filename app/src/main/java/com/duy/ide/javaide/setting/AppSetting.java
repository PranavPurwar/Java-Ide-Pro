/* Decompiler 4ms, total 806ms, lines 67 */
package com.duy.ide.javaide.setting;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

public class AppSetting {
   @NonNull
   protected Context context;
   @NonNull
   protected Editor editor;
   @NonNull
   private SharedPreferences sharedPreferences;

   @SuppressLint({"CommitPrefEdits"})
   public AppSetting(@NonNull Context var1) {
      this.context = var1;
      this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(var1);
      this.editor = this.sharedPreferences.edit();
   }

   public boolean getBoolean(String var1, boolean var2) {
      try {
         boolean var3 = this.sharedPreferences.getBoolean(var1, var2);
         return var3;
      } catch (Exception var4) {
         return var2;
      }
   }

   public int getFormatType() {
      return this.getInt(this.context.getString(2131689726), 0);
   }

   public int getInt(String var1, int var2) {
      int var3;
      try {
         var3 = this.sharedPreferences.getInt(var1, var2);
         return var3;
      } catch (Exception var6) {
         try {
            var3 = Integer.parseInt(this.getString(var1));
            return var3;
         } catch (Exception var5) {
            return var2;
         }
      }
   }

   public String getString(String var1) {
      try {
         var1 = this.sharedPreferences.getString(var1, "");
      } catch (Exception var2) {
         var1 = "";
      }

      return var1;
   }

   public boolean installViaRootAccess() {
      return this.getBoolean(this.context.getString(2131689877), false);
   }
}