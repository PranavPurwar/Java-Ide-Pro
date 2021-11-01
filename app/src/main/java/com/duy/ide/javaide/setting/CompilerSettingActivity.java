/* Decompiler 2ms, total 518ms, lines 20 */
package com.duy.ide.javaide.setting;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import com.duy.ide.javaide.activities.BaseActivity;

public class CompilerSettingActivity extends BaseActivity {
   protected void onCreate(@Nullable Bundle var1) {
      super.onCreate(var1);
      this.setContentView(2131427355);
      this.setSupportActionBar((Toolbar)this.findViewById(2131296705));
      this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      FragmentTransaction var2 = this.getFragmentManager().beginTransaction();
      var2.replace(2131296387, new CompilerSettingFragment());
      var2.commit();
   }
}