package com.duy.ide.javaide.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.jecelyin.editor.v2.Preferences;

public class IdePreferenceManager {
   private static final String KEY_SET_DEFAULT = "set_default_values";

   public static void setDefaultValues(Context context) {
      SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(context);
      if (!shared.getBoolean("set_default_values", false)) {
         shared.edit().putBoolean("set_default_values", true).apply();
         PreferenceManager.setDefaultValues(context, 2131886082, false);
         PreferenceManager.setDefaultValues(context, 2131886083, false);
         PreferenceManager.setDefaultValues(context, 2131886084, false);
         Preferences prefs = Preferences.getInstance(context);
         prefs.setAppTheme(1);
         prefs.setEditorTheme("allure-contrast.json.properties");
      }
   }
}