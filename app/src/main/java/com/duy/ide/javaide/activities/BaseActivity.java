/* Decompiler 5ms, total 1794ms, lines 23 */
package com.duy.ide.javaide.activities;

import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.support.annotation.StyleRes;
import android.support.v7.widget.Toolbar;
import com.jecelyin.editor.v2.ThemeSupportActivity;
import com.duy.ide.R;

public class BaseActivity extends ThemeSupportActivity implements OnSharedPreferenceChangeListener {
   @StyleRes
   public int getThemeId() {
      return R.style.AppThemeDark;
   }

   public void setupToolbar() {
      Toolbar var1 = (Toolbar)this.findViewById(R.id.toolbar);
      if (var1 != null) {
         this.setSupportActionBar(var1);
         this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      }

   }
}