/* Decompiler 46ms, total 570ms, lines 147 */
package com.duy.ide.javaide.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.android.sdklib.IAndroidTarget;
import com.android.sdklib.SdkManager;
import com.android.sdklib.internal.project.ProjectCreator;
import com.android.sdklib.internal.project.ProjectCreator.OutputLevel;
import com.android.utils.StdLogger;
import com.android.utils.StdLogger.Level;
import com.duy.android.compiler.env.Environment;
import com.duy.android.compiler.utils.FileUtils;
import com.duy.ide.javaide.JavaIdeActivity;
import com.duy.ide.javaide.utils.DLog;
import com.duy.ide.R;
import java.io.File;
import java.util.Arrays;

public class SplashScreenActivity extends AppCompatActivity {
   private static final int MY_PERMISSIONS_REQUEST = 11;
   private static final int REQUEST_INSTALL_SYSTEM = 12;
   private static final String TAG = "SplashScreenActivity";

   private void installFailed() {
   }

   private void installSystem() {
      this.startActivityForResult(new Intent(this, InstallActivity.class), 12);
   }

   private boolean permissionGranted() {
      boolean var1;
      if (ContextCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") != 0 && ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
         var1 = false;
      } else {
         var1 = true;
      }

      return var1;
   }

   private void requestPermissions() {
      ActivityCompat.requestPermissions(this, new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"}, 11);
   }

   private void startMainActivity() {
      new Handler().postDelayed(new Runnable() {
         public void run() {
            Intent var1 = new Intent(SplashScreenActivity.this, JavaIdeActivity.class);
            var1.addFlags(65536);
            SplashScreenActivity.this.overridePendingTransition(0, 0);
            SplashScreenActivity.this.startActivity(var1);
            SplashScreenActivity.this.finish();
         }
      }, 400L);
   }

   private boolean systemInstalled() {
      return Environment.isSdkInstalled(this);
   }

   private void testCreateProject() {
      StdLogger var1 = new StdLogger(Level.VERBOSE);
      String var2 = Environment.getSdkDir(this).getAbsolutePath();
      SdkManager var3 = SdkManager.createManager(var2, var1);
      ProjectCreator var10 = new ProjectCreator(var3, var2, OutputLevel.VERBOSE, var1);
      IAndroidTarget[] var4 = var3.getTargets();
      StringBuilder var11;
      if (DLog.DEBUG) {
         var11 = new StringBuilder();
         var11.append("targets = ");
         var11.append(Arrays.toString(var4));
         DLog.d("SplashScreenActivity", var11.toString());
      }

      int var5 = var4.length;
      int var6 = 0;

      IAndroidTarget var12;
      for(var12 = null; var6 < var5; ++var6) {
         IAndroidTarget var7 = var4[var6];
         if (var7.getVersion().getApiLevel() == 27) {
            var12 = var7;
         }
      }

      File var8 = new File(Environment.getSdkAppDir(), "DemoAndroid");
      FileUtils.deleteQuietly(var8);
      var10.createGradleProject(var8.getAbsolutePath(), "DemoAndroid", "com.duy.example", "MainActivity", var12, false, "1.0");
      File[] var9 = var8.listFiles();
      if (DLog.DEBUG) {
         var11 = new StringBuilder();
         var11.append("files = ");
         var11.append(Arrays.toString(var9));
         DLog.d("SplashScreenActivity", var11.toString());
      }

   }

   protected void onActivityResult(int var1, int var2, Intent var3) {
      super.onActivityResult(var1, var2, var3);
      if (var1 == 12) {
         if (var2 == -1) {
            this.startMainActivity();
         } else {
            this.installFailed();
         }
      }

   }

   public void onCreate(Bundle var1) {
      super.onCreate(var1);
      this.setContentView(R.layout.splash);
      PreferenceManager.setDefaultValues(this, R.xml.pref_settings, false);
      if (!this.permissionGranted()) {
         this.requestPermissions();
      } else if (this.systemInstalled()) {
         this.startMainActivity();
      } else {
         this.installSystem();
      }

   }

   public void onRequestPermissionsResult(int var1, @NonNull String[] var2, @NonNull int[] var3) {
      if (var1 == 11) {
         if (var3.length > 0 && var3[0] == 0 && var3[1] == 0) {
            if (this.systemInstalled()) {
               this.startMainActivity();
            } else {
               this.installSystem();
            }
         } else {
            Toast.makeText(this, 2131689837, 0).show();
         }
      }

   }
}