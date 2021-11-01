package com.duy.ide.javaide.setting;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import com.duy.common.preferences.PreferencesNative;

public class CompilerSettingFragment extends PreferenceFragment {
   public void onCreate(@Nullable Bundle var1) {
      super.onCreate(var1);
      this.addPreferencesFromResource(2131886082);
      PreferencesNative.bindPreferenceSummaryToValue(this.findPreference(this.getString(2131689732)));
      PreferencesNative.bindPreferenceSummaryToValue(this.findPreference(this.getString(2131689734)));
      PreferencesNative.bindPreferenceSummaryToValue(this.findPreference(this.getString(2131689733)));
   }
}