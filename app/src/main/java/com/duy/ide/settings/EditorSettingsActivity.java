/* Decompiler 12ms, total 467ms, lines 126 */
package com.duy.ide.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import com.duy.ide.editor.editor.R.id;
import com.duy.ide.editor.editor.R.layout;
import com.duy.ide.editor.editor.R.string;
import com.duy.ide.editor.editor.R.xml;
import com.jecelyin.editor.v2.Preferences;
import com.jecelyin.editor.v2.ThemeSupportActivity;

public class EditorSettingsActivity extends ThemeSupportActivity {
   public static void open(Activity var0, int var1) {
      var0.startActivityForResult(new Intent(var0, EditorSettingsActivity.class), var1);
   }

   protected void onCreate(Bundle var1) {
      super.onCreate(var1);
      this.setContentView(layout.activity_editor_setting);
      this.setSupportActionBar((Toolbar)this.findViewById(id.toolbar));
      this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      this.setTitle(string.settings);
      this.getFragmentManager().beginTransaction().replace(id.content, new EditorSettingsActivity.SettingsFragment()).commit();
   }

   public boolean onOptionsItemSelected(MenuItem var1) {
      if (var1.getItemId() == 16908332) {
         this.finish();
         return true;
      } else {
         return super.onOptionsItemSelected(var1);
      }
   }

   public static class SettingsFragment extends PreferenceFragment implements OnPreferenceClickListener {
      private static OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new OnPreferenceChangeListener() {
         public boolean onPreferenceChange(Preference var1, Object var2) {
            if (var2 == null) {
               return true;
            } else {
               String var3 = var2.toString();
               String var4 = var1.getKey();
               if (var1 instanceof ListPreference) {
                  ListPreference var6 = (ListPreference)var1;
                  int var5 = var6.findIndexOfValue(var3);
                  CharSequence var7;
                  if (var5 >= 0) {
                     var7 = var6.getEntries()[var5];
                  } else {
                     var7 = null;
                  }

                  var1.setSummary(var7);
               } else if (var1 instanceof CheckBoxPreference) {
                  ((CheckBoxPreference)var1).setChecked((Boolean)var2);
               } else if ("pref_highlight_file_size_limit".equals(var4)) {
                  StringBuilder var8 = new StringBuilder();
                  var8.append(var3);
                  var8.append(" KB");
                  var1.setSummary(var8.toString());
               } else {
                  var1.setSummary(String.valueOf(var3));
               }

               return true;
            }
         }
      };

      private static void bindPreferenceSummaryToValue(Preference var0) {
         var0.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
         String var1 = var0.getKey();
         Object var2 = Preferences.getInstance(var0.getContext()).getValue(var1);
         sBindPreferenceSummaryToValueListener.onPreferenceChange(var0, var2);
      }

      private static void dependBindPreference(PreferenceGroup var0) {
         int var1 = var0.getPreferenceCount();
         Preferences var2 = Preferences.getInstance(var0.getContext());

         for(int var3 = 0; var3 < var1; ++var3) {
            Preference var4 = var0.getPreference(var3);
            String var5 = var4.getKey();
            if (var4 instanceof PreferenceGroup) {
               dependBindPreference((PreferenceGroup)var4);
            } else if (!var4.getClass().equals(Preference.class)) {
               Object var6 = var2.getValue(var5);
               if (!(var4 instanceof ListPreference)) {
                  if (var4 instanceof EditTextPreference) {
                     ((EditTextPreference)var4).setText(String.valueOf(var6));
                  } else if (var4 instanceof CheckBoxPreference) {
                     ((CheckBoxPreference)var4).setChecked(Boolean.valueOf(String.valueOf(var6)));
                  }
               }

               if (!"pref_symbol".equals(var5)) {
                  bindPreferenceSummaryToValue(var4);
               }
            }
         }

      }

      public void onCreate(Bundle var1) {
         super.onCreate(var1);
         this.addPreferencesFromResource(xml.preference_editor);
         dependBindPreference(this.getPreferenceScreen());
      }

      public boolean onPreferenceClick(Preference var1) {
         return true;
      }
   }
}