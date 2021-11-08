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
      public boolean onPreferenceChange(Preference pref, Object obj) {
         if (pref instanceof ListPreference) {
            ListPreference var6 = (ListPreference)pref;
            int var4 = var6.findIndexOfValue(obj.toString());
            CharSequence var7;
            if (var4 >= 0) {
               var7 = var6.getEntries()[var4];
            } else {
               var7 = null;
            }

            pref.setSummary(var7);
         } else if (!(pref instanceof RingtonePreference)) {
            if (pref instanceof EditTextPreference) {
               EditTextPreference var5 = (EditTextPreference)pref;
               var5.setSummary(var5.getText());
            } else {
               pref.setSummary(obj.toString());
            }
         }

         return true;
      }
   };
   private AppCompatDelegate mDelegate;

   public static void bindPreferenceSummaryToValue(Preference pref) {
      if (pref != null) {
         pref.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
         sBindPreferenceSummaryToValueListener.onPreferenceChange(pref, PreferenceManager.getDefaultSharedPreferences(pref.getContext()).getString(pref.getKey(), ""));
      }
   }
}