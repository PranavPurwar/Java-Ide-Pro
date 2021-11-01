package com.duy.ide.javaide.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.jecelyin.editor.v2.Preferences;

public class IdePreferenceManager {
   private static final String KEY_SET_DEFAULT = "set_default_values";

   public static void setDefaultValues(Context var0) {
      SharedPreferences var1 = PreferenceManager.getDefaultSharedPreferences(var0);
      if (!var1.getBoolean("set_default_values", false)) {
         var1.edit().putBoolean("set_default_values", true).apply();
         PreferenceManager.setDefaultValues(var0, 2131886082, false);
         PreferenceManager.setDefaultValues(var0, 2131886083, false);
         PreferenceManager.setDefaultValues(var0, 2131886084, false);
         Preferences var2 = Preferences.getInstance(var0);
         var2.setAppTheme(1);
         var2.setEditorTheme("allure-contrast.json.properties");
      }
   }
}