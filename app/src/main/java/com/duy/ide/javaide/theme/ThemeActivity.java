package com.duy.ide.javaide.theme;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.duy.common.purchase.InAppPurchaseHelper;
import com.duy.common.purchase.Premium;
import com.duy.ide.R;
import com.duy.ide.editor.theme.EditorThemeFragment;
import com.duy.ide.editor.theme.model.EditorTheme;
import com.jecelyin.editor.v2.Preferences;
import com.jecelyin.editor.v2.ThemeSupportActivity;

public class ThemeActivity extends ThemeSupportActivity
        implements EditorThemeFragment.EditorThemeAdapter.OnThemeSelectListener {
    private Preferences mPreferences;
    private InAppPurchaseHelper mInAppPurchaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java_editor_theme);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(R.string.editor_theme);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPreferences = Preferences.getInstance(this);
        mInAppPurchaseHelper = new InAppPurchaseHelper(this);
        mPreferences.registerOnSharedPreferenceChangeListener(this);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content, new EditorThemeFragment())
                .commit();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        super.onSharedPreferenceChanged(sharedPreferences, key);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onEditorThemeSelected(EditorTheme theme) {
        if (Premium.isPremiumUser(this)) {
            mPreferences.setEditorTheme(theme.getFileName());
            String text = getString(R.string.selected_editor_theme, theme.getName());
            Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
        } else {
            PremiumDialog premiumDialog = new PremiumDialog(this, mInAppPurchaseHelper);
            premiumDialog.show();
        }
    }

}
