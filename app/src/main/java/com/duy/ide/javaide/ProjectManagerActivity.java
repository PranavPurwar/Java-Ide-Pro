package com.duy.ide.javaide;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import com.duy.android.compiler.env.Environment;
import com.duy.android.compiler.project.AndroidProjectManager;
import com.duy.android.compiler.project.JavaProject;
import com.duy.android.compiler.project.JavaProjectManager;
import com.duy.file.explorer.FileExplorerActivity;
import com.duy.ide.core.api.IdeActivity;
import com.duy.ide.editor.EditorDelegate;
import com.duy.ide.javaide.projectview.ProjectFilePresenter;
import com.duy.ide.javaide.projectview.ProjectFileContract.Presenter;
import com.duy.ide.javaide.projectview.dialog.DialogNewAndroidProject;
import com.duy.ide.javaide.projectview.dialog.DialogNewClass;
import com.duy.ide.javaide.projectview.dialog.DialogNewJavaProject;
import com.duy.ide.javaide.projectview.dialog.DialogSelectType;
import com.duy.ide.javaide.projectview.dialog.DialogNewJavaProject.OnCreateProjectListener;
import com.duy.ide.javaide.projectview.dialog.DialogSelectType.OnFileTypeSelectListener;
import com.duy.ide.javaide.projectview.view.fragments.FolderStructureFragment;
import com.duy.ide.javaide.utils.FileUtils;
import java.io.File;
import java.io.IOException;

public abstract class ProjectManagerActivity extends IdeActivity implements FileChangeListener, OnCreateProjectListener {
   private static final int REQUEST_OPEN_ANDROID_PROJECT = 704;
   private static final int REQUEST_OPEN_JAVA_PROJECT = 58;
   private static final String TAG = "BaseEditorActivity";
   protected Presenter mFilePresenter;
   protected JavaProject mProject;

   private void createProjectIfNeed() {
      if (this.mProject == null) {
         this.createJavaProject();
      }

   }

   private void openFileByAnotherApp(File var1) {
      Exception var10000;
      label50: {
         Uri var2;
         boolean var10001;
         label43: {
            try {
               if (VERSION.SDK_INT >= 24) {
                  StringBuilder var11 = new StringBuilder();
                  var11.append(this.getPackageName());
                  var11.append(".provider");
                  var2 = FileProvider.getUriForFile(this, var11.toString(), var1);
                  break label43;
               }
            } catch (Exception var8) {
               var10000 = var8;
               var10001 = false;
               break label50;
            }

            try {
               var2 = Uri.fromFile(var1);
            } catch (Exception var7) {
               var10000 = var7;
               var10001 = false;
               break label50;
            }
         }

         MimeTypeMap var3;
         Intent var4;
         String var9;
         try {
            var3 = MimeTypeMap.getSingleton();
            var4 = new Intent("android.intent.action.VIEW");
            var9 = FileUtils.fileExt(var1.getPath());
         } catch (Exception var6) {
            var10000 = var6;
            var10001 = false;
            break label50;
         }

         if (var9 == null) {
            var9 = "";
         }

         try {
            var4.setDataAndType(var2, var3.getMimeTypeFromExtension(var9));
            var4.setFlags(268435456);
            var4.addFlags(1);
            this.startActivity(var4);
            return;
         } catch (Exception var5) {
            var10000 = var5;
            var10001 = false;
         }
      }

      Exception var10 = var10000;
      Toast.makeText(this, var10.getMessage(), 1).show();
   }

   public void createAndroidProject() {
      DialogNewAndroidProject.newInstance().show(this.getSupportFragmentManager(), "DialogNewAndroidProject");
   }

   public void createJavaProject() {
      DialogNewJavaProject.newInstance().show(this.getSupportFragmentManager(), "DialogNewProject");
   }

   public void createNewClass(@Nullable File var1) {
      File var2 = var1;
      if (var1 == null) {
         File var3 = this.getCurrentFile();
         var2 = var1;
         if (var3 != null) {
            var2 = var3.getParentFile();
         }
      }

      if (this.mProject != null && var2 != null) {
         DialogNewClass.newInstance(this.mProject, this.mProject.getPackageName(), var2).show(this.getSupportFragmentManager(), "DialogNewClass");
      } else {
         this.toast("Can not create new class");
      }

   }

   public void doOpenFile(File var1) {
      if (FileUtils.canEdit(var1)) {
         this.openFile(var1.getPath());
         this.closeDrawers();
      } else {
         this.openFileByAnotherApp(var1);
      }

   }

   @Nullable
   protected File getCurrentFile() {
      EditorDelegate var1 = this.getCurrentEditorDelegate();
      return var1 != null ? var1.getDocument().getFile() : null;
   }

   protected int getRootLayoutId() {
      return 2131427357;
   }

   public int getThemeId() {
      return 2131755016;
   }

   protected void initLeftNavigationView(@NonNull NavigationView var1) {
      super.initLeftNavigationView(var1);
      if (this.mProject == null) {
         this.mProject = JavaProjectManager.getLastProject(this);
      }

      FolderStructureFragment var2 = FolderStructureFragment.newInstance(this.mProject);
      ((ViewGroup)var1.findViewById(2131296487)).removeAllViews();
      this.getSupportFragmentManager().beginTransaction().replace(2131296487, var2, "FolderStructureFragment").commit();
      this.mFilePresenter = new ProjectFilePresenter(var2);
   }

   protected void onActivityResult(int var1, int var2, Intent var3) {
      super.onActivityResult(var1, var2, var3);
      StringBuilder var10;
      if (var1 != 58) {
         if (var1 == 704 && var2 == -1) {
            AndroidProjectManager var4 = new AndroidProjectManager(this);
            String var5 = FileExplorerActivity.getFile(var3);

            try {
               File var8 = new File(var5);
               this.onProjectCreated(var4.loadProject(var8, true));
            } catch (Exception var7) {
               var7.printStackTrace();
               var10 = new StringBuilder();
               var10.append("Can not import project. Error: ");
               var10.append(var7.getMessage());
               Toast.makeText(this, var10.toString(), 0).show();
            }
         }
      } else if (var2 == -1) {
         String var9 = FileExplorerActivity.getFile(var3);
         if (var9 == null) {
            return;
         }

         JavaProjectManager var11 = new JavaProjectManager(this);

         try {
            File var12 = new File(var9);
            this.onProjectCreated(var11.loadProject(var12, true));
         } catch (IOException var6) {
            var6.printStackTrace();
            var10 = new StringBuilder();
            var10.append("Can not import project. Error: ");
            var10.append(var6.getMessage());
            Toast.makeText(this, var10.toString(), 0).show();
         }
      }

   }

   protected void onCreate(@Nullable Bundle var1) {
      super.onCreate(var1);
      this.setupToolbar();
      this.createProjectIfNeed();
   }

   public void onFileCreated(File var1) {
      this.mFilePresenter.refresh(this.mProject);
      this.openFile(var1.getPath());
   }

   public void onFileDeleted(File var1) {
      Pair var2 = this.mTabManager.getEditorDelegate(var1);
      if (var2 != null) {
         this.mTabManager.closeTab((Integer)var2.first);
      }

   }

   protected void onPause() {
      super.onPause();
      if (this.mProject != null) {
         JavaProjectManager.saveProject(this, this.mProject);
      }

   }

   public void onProjectCreated(@NonNull JavaProject var1) {
      StringBuilder var2 = new StringBuilder();
      var2.append("onProjectCreated() called with: projectFile = [");
      var2.append(var1);
      var2.append("]");
      Log.d("BaseEditorActivity", var2.toString());
      this.mProject = var1;
      JavaProjectManager.saveProject(this, var1);
      this.mTabManager.closeAllTab();
      this.mFilePresenter.show(var1, true);
      this.mDiagnosticPresenter.hidePanel();
      this.mDiagnosticPresenter.clear();
      this.openDrawer(8388611);
      this.startAutoCompleteService();
   }

   public void openAndroidProject() {
      FileExplorerActivity.startPickPathActivity(this, Environment.getSdkAppDir().getAbsolutePath(), "UTF-8", 704);
   }

   public void openDrawer(int var1) {
      try {
         this.mDrawerLayout.openDrawer(var1);
      } catch (Exception var3) {
      }

   }

   public void openJavaProject() {
      FileExplorerActivity.startPickPathActivity(this, Environment.getSdkAppDir().getAbsolutePath(), "UTF-8", 58);
   }

   public void setupToolbar() {
   }

   public void showDialogNew(@Nullable File var1) {
      DialogSelectType.newInstance(var1, new OnFileTypeSelectListener() {
         public void onTypeSelected(File var1, String var2) {
         }
      }).show(this.getSupportFragmentManager(), "DialogNewAndroidProject");
   }

   protected abstract void startAutoCompleteService();

   protected void toast(String var1) {
      Toast.makeText(this, var1, 0).show();
   }
}