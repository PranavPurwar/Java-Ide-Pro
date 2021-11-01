/* Decompiler 12ms, total 650ms, lines 140 */
package com.duy.ide.javaide.run.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;
import com.duy.android.compiler.java.Java;
import com.duy.common.io.IOUtils;
import com.duy.ide.javaide.JavaApplication;
import com.duy.ide.javaide.activities.BaseActivity;
import com.duy.ide.javaide.editor.autocomplete.parser.JavaParser;
import com.duy.ide.javaide.run.view.ConsoleEditText;
import com.sun.tools.javac.tree.JCTree.JCExpression;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ExecuteActivity extends BaseActivity {
   public static final String DEX_FILE = "DEX_FILE";
   public static final String MAIN_CLASS_FILE = "MAIN_CLASS_FILE";
   private static final String TAG = "ExecuteActivity";
   private ConsoleEditText mConsoleEditText;
   private File mDexFile;
   private final Handler mHandler = new Handler();
   private File mMainClass;

   private void bindView() {
      this.mConsoleEditText = (ConsoleEditText)this.findViewById(2131296384);
   }

   @WorkerThread
   private void consoleStopped() {
      this.mHandler.post(new Runnable() {
         public void run() {
            ExecuteActivity.this.getSupportActionBar().setSubtitle(2131689586);
            ExecuteActivity.this.removeIOFilter();
         }
      });
   }

   @WorkerThread
   private void exec(File var1) throws Throwable {
      String var4 = this.resolveMainClass(var1);
      InputStream var2 = this.mConsoleEditText.getInputStream();
      File var3 = this.getDir("dex", 0);
      this.executeDex(var2, this.mDexFile, var3, var4);
   }

   private void executeDex(InputStream var1, File var2, File var3, String var4) throws Throwable {
      if (var2 == null) {
         throw new RuntimeException("Dex file must be not null");
      } else if (var4 == null) {
         throw new RuntimeException("Main class must be not null");
      } else {
         String var5 = var2.getPath();
         String var6 = var3.getPath();
         Java.run(new String[]{"-jar", var5, var4}, var6, var1);
      }
   }

   private void initInOutStream() {
      JavaApplication var1 = (JavaApplication)this.getApplication();
      var1.addStdErr(this.mConsoleEditText.getErrorStream());
      var1.addStdOut(this.mConsoleEditText.getOutputStream());
   }

   private void removeIOFilter() {
      this.mConsoleEditText.stop();
      JavaApplication var1 = (JavaApplication)this.getApplication();
      var1.removeErrStream(this.mConsoleEditText.getErrorStream());
      var1.removeOutStream(this.mConsoleEditText.getOutputStream());
   }

   private String resolveMainClass(File var1) throws IOException {
      if (!var1.getName().endsWith(".java")) {
         return null;
      } else {
         JCExpression var2 = (new JavaParser()).parse(IOUtils.toString(var1)).getPackageName();
         String var3 = var1.getName().substring(0, var1.getName().indexOf("."));
         StringBuilder var4 = new StringBuilder();
         var4.append(var2);
         var4.append(".");
         var4.append(var3);
         return var4.toString();
      }
   }

   protected void onCreate(@Nullable Bundle var1) {
      super.onCreate(var1);
      this.setContentView(2131427361);
      this.setupToolbar();
      this.bindView();
      this.initInOutStream();
      Intent var2 = this.getIntent();
      if (var2 == null) {
         this.finish();
      } else {
         this.mDexFile = (File)var2.getSerializableExtra("DEX_FILE");
         if (this.mDexFile == null) {
            this.finish();
         } else {
            this.mMainClass = (File)this.getIntent().getSerializableExtra("MAIN_CLASS_FILE");
            if (this.mMainClass == null) {
               this.finish();
            } else {
               this.setTitle(this.mMainClass.getName());
               this.getSupportActionBar().setSubtitle(2131689585);
               (new Thread(new Runnable() {
                  public void run() {
                     try {
                        ExecuteActivity.this.exec(ExecuteActivity.this.mMainClass);
                     } catch (Error var2) {
                        var2.printStackTrace(ExecuteActivity.this.mConsoleEditText.getErrorStream());
                     } catch (Exception var3) {
                        var3.printStackTrace(ExecuteActivity.this.mConsoleEditText.getErrorStream());
                     } catch (Throwable var4) {
                        var4.printStackTrace(ExecuteActivity.this.mConsoleEditText.getErrorStream());
                     }

                     ExecuteActivity.this.consoleStopped();
                  }
               })).start();
            }
         }
      }
   }

   protected void onDestroy() {
      super.onDestroy();
   }

   protected void onStop() {
      super.onStop();
      Log.d("ExecuteActivity", "onStop() called");
      this.removeIOFilter();
   }
}