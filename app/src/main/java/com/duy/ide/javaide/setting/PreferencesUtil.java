package com.duy.ide.javaide.setting;

import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.support.v7.app.AppCompatDelegate;

public class PreferencesUtil {
   private static OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new OnPreferenceChangeListener() {
      public boolean onPreferenceChange(Preference var1, Object var2) {
         String var3 = var2.toString();
         if (var1 instanceof ListPreference) {
            ListPreference var6 = (ListPreference)var1;
            int var4 = var6.findIndexOfValue(var3);
            CharSequence var7;
            if (var4 >= 0) {
               var7 = var6.getEntries()[var4];
            } else {
               var7 = null;
            }

            var1.setSummary(var7);
         } else if (!(var1 instanceof RingtonePreference)) {
            if (var1 instanceof EditTextPreference) {
               EditTextPreference var5 = (EditTextPreference)var1;
               var5.setSummary(var5.getText());
            } else {
               var1.setSummary(var3);
            }
         }

         return true;
      }
   };
   private AppCompatDelegate mDelegate;

   public static void bindPreferenceSummaryToValue(Preference var0) {
      if (var0 != null) {
         var0.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
         sBindPreferenceSummaryToValueListener.onPreferenceChange(var0, PreferenceManager.getDefaultSharedPreferences(var0.getContext()).getString(var0.getKey(), ""));
      }
   }
}