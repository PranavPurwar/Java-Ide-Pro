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

   private void openFileByAnotherApp(File file) {
      Exception var10000;
      label50: {
         Uri var2;
         boolean var10001;
         label43: {
            try {
               if (VERSION.SDK_INT >= 24) {
                  var2 = FileProvider.getUriForFile(this, this.getPackageName() + ".provider", file);
                  break label43;
               }
            } catch (Exception e) {
               var10000 = e;
               var10001 = false;
               break label50;
            }

            try {
               var2 = Uri.fromFile(file);
            } catch (Exception e) {
               var10000 = e;
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
            var9 = FileUtils.fileExt(file.getPath());
         } catch (Exception e) {
            var10000 = e;
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
         } catch (Exception e) {
            var10000 = e;
            var10001 = false;
         }
      }

      Exception except = var10000;
      Toast.makeText(this, except.getMessage(), 1).show();
   }

   public void createAndroidProject() {
      DialogNewAndroidProject.newInstance().show(this.getSupportFragmentManager(), "DialogNewAndroidProject");
   }

   public void createJavaProject() {
      DialogNewJavaProject.newInstance().show(this.getSupportFragmentManager(), "DialogNewProject");
   }

   public void createNewClass(@Nullable File path) {
      File mPath = path;
      if (path == null) {
         File current = this.getCurrentFile();
         mPath = path;
         if (current != null) {
            mPath = current.getParentFile();
         }
      }

      if (this.mProject != null && mPath != null) {
         DialogNewClass.newInstance(this.mProject, this.mProject.getPackageName(), mPath).show(this.getSupportFragmentManager(), "DialogNewClass");
      } else {
         this.toast("Can not create new class");
      }

   }

   public void doOpenFile(File file) {
      if (FileUtils.canEdit(file)) {
         this.openFile(file.getPath());
         this.closeDrawers();
      } else {
         this.openFileByAnotherApp(file);
      }

   }

   @Nullable
   protected File getCurrentFile() {
      EditorDelegate delegate = this.getCurrentEditorDelegate();
      return delegate != null ? delegate.getDocument().getFile() : null;
   }

   protected int getRootLayoutId() {
      return 2131427357;
   }

   public int getThemeId() {
      return 2131755016;
   }

   protected void initLeftNavigationView(@NonNull NavigationView view) {
      super.initLeftNavigationView(view);
      if (this.mProject == null) {
         this.mProject = JavaProjectManager.getLastProject(this);
      }

      FolderStructureFragment structure = FolderStructureFragment.newInstance(this.mProject);
      ((ViewGroup)view.findViewById(2131296487)).removeAllViews();
      this.getSupportFragmentManager().beginTransaction().replace(2131296487, structure, "FolderStructureFragment").commit();
      this.mFilePresenter = new ProjectFilePresenter(structure);
   }

   protected void onActivityResult(int var1, int var2, Intent var3) {
      super.onActivityResult(var1, var2, var3);
      File project;
      StringBuilder var10;
      if (var1 != 58) {
         if (var1 == 704 && var2 == -1) {
            AndroidProjectManager var4 = new AndroidProjectManager(this);
            String var8 = FileExplorerActivity.getFile(var3);

            try {
               project = new File(var8);
               this.onProjectCreated(var4.loadProject(project, true));
            } catch (Exception var7) {
               var7.printStackTrace();
               var10 = new StringBuilder();
               var10.append("Can not import project. Error: ");
               var10.append(var7.getMessage());
               Toast.makeText(this, var10.toString(), 0).show();
            }
         }
      } else if (var2 == -1) {
         String var11 = FileExplorerActivity.getFile(var3);
         if (var11 == null) {
            return;
         }

         JavaProjectManager manager = new JavaProjectManager(this);

         try {
            project = new File(var11);
            this.onProjectCreated(manager.loadProject(project, true));
         } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Cannot import project. Error: " + e.getMessage(), 0).show();
         }
      }

   }

   protected void onCreate(@Nullable Bundle bundle) {
      super.onCreate(bundle);
      this.setupToolbar();
      this.createProjectIfNeed();
   }

   public void onFileCreated(File file) {
      this.mFilePresenter.refresh(this.mProject);
      this.openFile(file.getPath());
   }

   public void onFileDeleted(File file) {
      Pair pair = this.mTabManager.getEditorDelegate(file);
      if (pair != null) {
         this.mTabManager.closeTab((Integer)pair.first);
      }

   }

   protected void onPause() {
      super.onPause();
      if (this.mProject != null) {
         JavaProjectManager.saveProject(this, this.mProject);
      }

   }

   public void onProjectCreated(@NonNull JavaProject project) {
      Log.d("BaseEditorActivity", "onProjectCreated() called with: projectFile = [" + project + "]");
      this.mProject = project;
      JavaProjectManager.saveProject(this, project);
      this.mTabManager.closeAllTab();
      this.mFilePresenter.show(project, true);
      this.mDiagnosticPresenter.hidePanel();
      this.mDiagnosticPresenter.clear();
      this.openDrawer(8388611);
      this.startAutoCompleteService();
   }

   public void openAndroidProject() {
      FileExplorerActivity.startPickPathActivity(this, Environment.getSdkAppDir().getAbsolutePath(), "UTF-8", 704);
   }

   public void openDrawer(int pos) {
      try {
         this.mDrawerLayout.openDrawer(pos);
      } catch (Exception ignored) {
      }

   }

   public void openJavaProject() {
      FileExplorerActivity.startPickPathActivity(this, Environment.getSdkAppDir().getAbsolutePath(), "UTF-8", 58);
   }

   public void setupToolbar() {
   }

   public void showDialogNew(@Nullable File file) {
      DialogSelectType.newInstance(file, new OnFileTypeSelectListener() {
         public void onTypeSelected(File file, String type) {
         }
      }).show(this.getSupportFragmentManager(), "DialogNewAndroidProject");
   }

   protected abstract void startAutoCompleteService();

   protected void toast(String message) {
      Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
   }
}