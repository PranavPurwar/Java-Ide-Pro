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
   public AppSetting(@NonNull Context context) {
      this.context = context;
      this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
      this.editor = this.sharedPreferences.edit();
   }

   public boolean getBoolean(String key, boolean boo) {
      try {
         boolean result = this.sharedPreferences.getBoolean(key, boo);
         return result;
      } catch (Exception ignored) {
         return boo;
      }
   }

   public int getFormatType() {
      return this.getInt(this.context.getString(2131689726), 0);
   }

   public int getInt(String key, int normal) {
      try {
         return this.sharedPreferences.getInt(key, normal);
      } catch (Exception ignored) {
         try {
            return Integer.parseInt(this.getString(key));
         } catch (Exception unused) {
            return normal;
         }
      }
   }

   public String getString(String key) {
      try {
         return this.sharedPreferences.getString(key, "");
      } catch (Exception ignored) {
         return "";
      }
   }

   public boolean installViaRootAccess() {
      return this.getBoolean(this.context.getString(2131689877), false);
   }
}