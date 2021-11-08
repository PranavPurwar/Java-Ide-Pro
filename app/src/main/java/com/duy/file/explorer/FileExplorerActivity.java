/* Decompiler 174ms, total 988ms, lines 386 */
package com.duy.file.explorer;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.duy.file.explorer.io.JecFile;
import com.duy.file.explorer.io.LocalFile;
import com.duy.file.explorer.listener.OnClipboardDataChangedListener;
import com.duy.ide.editor.editor.R.id;
import com.duy.ide.editor.editor.R.layout;
import com.duy.ide.editor.editor.R.menu;
import com.duy.ide.editor.editor.R.string;
import com.duy.ide.editor.editor.databinding.ActivityFileExplorerBinding;
import com.jecelyin.editor.v2.Preferences;
import com.jecelyin.editor.v2.ThemeSupportActivity;
import java.io.File;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;

public class FileExplorerActivity extends ThemeSupportActivity implements OnClickListener, OnClipboardDataChangedListener {
   public static final String EXTRA_ENCODING = "encoding";
   public static final String EXTRA_HOME_PATH = "home_path";
   public static final String EXTRA_INIT_PATH = "dest_file";
   public static final String EXTRA_MODE = "mode";
   public static final int MODE_PICK_FILE = 1;
   public static final int MODE_PICK_PATH = 2;
   private ActivityFileExplorerBinding binding;
   private FileClipboard fileClipboard;
   private String fileEncoding = null;
   private FileListPagerFragment mFileListPagerFragment;
   private String mHomePath;
   private String mLastPath;
   private int mMode;
   private MenuItem mPasteMenu;
   private SearchView mSearchView;

   @Nullable
   public static String getFile(Intent var0) {
      return var0.getStringExtra("file");
   }

   @Nullable
   public static String getFileEncoding(Intent var0) {
      return var0.getStringExtra("encoding");
   }

   private void initFileView() {
      LocalFile var1 = new LocalFile(this.mLastPath);
      this.mFileListPagerFragment = (FileListPagerFragment)this.getSupportFragmentManager().findFragmentByTag(FileListPagerFragment.class.getName());
      if (this.mFileListPagerFragment == null) {
         this.mFileListPagerFragment = FileListPagerFragment.newFragment(var1);
      }

      this.getSupportFragmentManager().beginTransaction().replace(id.content, this.mFileListPagerFragment, FileListPagerFragment.class.getName()).commit();
   }

   private void onSave() {
      String var1 = this.binding.filenameEditText.getText().toString().trim();
      if (TextUtils.isEmpty(var1)) {
         this.binding.filenameEditText.setError(this.getString(string.can_not_be_empty));
      } else if (TextUtils.isEmpty(this.mLastPath)) {
         this.binding.filenameEditText.setError(this.getString(string.unknown_path));
      } else {
         File var2 = new File(this.mLastPath);
         File var3 = var2;
         if (var2.isFile()) {
            var3 = var2.getParentFile();
         }

         Exception var10000;
         label47: {
            boolean var10001;
            File var4;
            try {
               var4 = new File(var1);
            } catch (Exception var7) {
               var10000 = var7;
               var10001 = false;
               break label47;
            }

            var2 = var4;

            try {
               if (!var4.exists()) {
                  var2 = new File(var3, var1);
               }
            } catch (Exception var6) {
               var10000 = var6;
               var10001 = false;
               break label47;
            }

            try {
               this.setResult(var2);
               return;
            } catch (Exception var5) {
               var10000 = var5;
               var10001 = false;
            }
         }

         Exception var8 = var10000;
         Toast.makeText(this, var8.getMessage(), 0).show();
      }
   }

   private void onShowEncodingList() {
      SortedMap var1 = Charset.availableCharsets();
      Set var2 = var1.keySet();
      int var3 = var1.size();
      int var4 = 1;
      final String[] var6 = new String[var3 + 1];
      String var5 = this.getString(string.auto_detection_encoding);
      var3 = 0;
      var6[0] = var5;

      for(Iterator var8 = var2.iterator(); var8.hasNext(); ++var4) {
         String var7 = (String)var8.next();
         if (var7.equals(this.fileEncoding)) {
            var3 = var4;
         }

         var6[var4] = var7;
      }

      (new Builder(this)).setTitle(string.encoding).setSingleChoiceItems(var6, var3, new android.content.DialogInterface.OnClickListener() {
         public void onClick(DialogInterface var1, int var2) {
            String var10001 = var6[var2];
            FileExplorerActivity.this.binding.fileEncodingTextView.setText(var10001);
            if (var2 > 0) {
               FileExplorerActivity.this.fileEncoding = var6[var2];
            }

            var1.dismiss();
         }
      }).show();
   }

   private void setResult(File var1) {
      Intent var2 = new Intent();
      var2.putExtra("file", var1.getPath());
      var2.putExtra("encoding", this.fileEncoding);
      this.setResult(-1, var2);
      this.finish();
   }

   public static void startPickFileActivity(Activity var0, @Nullable String var1, @Nullable String var2, int var3) {
      startPickFileActivity(var0, var1, var2, (String)null, var3);
   }

   public static void startPickFileActivity(@NonNull Activity var0, @Nullable String var1, @Nullable String var2, @Nullable String var3, int var4) {
      Intent var5 = new Intent(var0, FileExplorerActivity.class);
      var5.putExtra("mode", 1);
      var5.putExtra("dest_file", var1);
      var5.putExtra("home_path", var2);
      var5.putExtra("encoding", var3);
      var0.startActivityForResult(var5, var4);
   }

   public static void startPickPathActivity(Activity var0, String var1, String var2, int var3) {
      Intent var4 = new Intent(var0, FileExplorerActivity.class);
      var4.putExtra("mode", 2);
      var4.putExtra("dest_file", var1);
      var4.putExtra("encoding", var2);
      var0.startActivityForResult(var4, var3);
   }

   public static void startPickPathActivity(Fragment var0, String var1, String var2, int var3) {
      Intent var4 = new Intent(var0.getContext(), FileExplorerActivity.class);
      var4.putExtra("mode", 2);
      var4.putExtra("dest_file", var1);
      var4.putExtra("encoding", var2);
      var0.startActivityForResult(var4, var3);
   }

   public FileClipboard getFileClipboard() {
      if (this.fileClipboard == null) {
         this.fileClipboard = new FileClipboard();
      }

      return this.fileClipboard;
   }

   public void onBackPressed() {
      if (!this.mSearchView.isIconified()) {
         this.mSearchView.setIconified(true);
      } else {
         super.onBackPressed();
      }

   }

   public void onClick(View var1) {
      int var2 = var1.getId();
      if (var2 == id.btn_select) {
         this.onSave();
      } else if (var2 == id.file_encoding_textView) {
         this.onShowEncodingList();
      }

   }

   public void onClipboardDataChanged() {
      if (this.mPasteMenu != null) {
         this.mPasteMenu.setVisible(this.getFileClipboard().canPaste());
      }
   }

   protected void onCreate(Bundle var1) {
      super.onCreate(var1);
      this.binding = (ActivityFileExplorerBinding)DataBindingUtil.setContentView(this, layout.activity_file_explorer);
      Intent var2 = this.getIntent();
      this.mMode = var2.getIntExtra("mode", 1);
      this.mHomePath = var2.getStringExtra("home_path");
      if (TextUtils.isEmpty(this.mHomePath)) {
         this.mHomePath = Environment.getExternalStorageDirectory().getPath();
      }

      this.setSupportActionBar((Toolbar)this.findViewById(id.toolbar));
      this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      int var3;
      if (this.mMode == 1) {
         var3 = string.select_file;
      } else {
         var3 = string.select_path;
      }

      this.setTitle(var3);
      this.mLastPath = Preferences.getInstance(this).getLastOpenPath();
      if (TextUtils.isEmpty(this.mLastPath)) {
         this.mLastPath = Environment.getExternalStorageDirectory().getPath();
      } else {
         File var5 = new File(this.mLastPath);
         if (!var5.exists() || !var5.canRead()) {
            this.mLastPath = Environment.getExternalStorageDirectory().getPath();
         }
      }

      String var6 = var2.getStringExtra("dest_file");
      if (!TextUtils.isEmpty(var6)) {
         File var4 = new File(var6);
         if (var4.isFile()) {
            var6 = var4.getParent();
         } else {
            var6 = var4.getPath();
         }

         this.mLastPath = var6;
         this.binding.filenameEditText.setText(var4.getName());
      } else {
         this.binding.filenameEditText.setText(this.getString(string.untitled_file_name));
      }

      this.initFileView();
      this.binding.btnSelect.setOnClickListener(this);
      this.binding.fileEncodingTextView.setOnClickListener(this);
      String var7 = var2.getStringExtra("encoding");
      this.fileEncoding = var7;
      var6 = var7;
      if (TextUtils.isEmpty(var7)) {
         var6 = this.getString(string.auto_detection_encoding);
      }

      this.binding.fileEncodingTextView.setText(var6);
      LinearLayout var8 = this.binding.filenameLayout;
      byte var9;
      if (this.mMode == 1) {
         var9 = 8;
      } else {
         var9 = 0;
      }

      var8.setVisibility(var9);
      this.getFileClipboard().setOnClipboardDataChangedListener(this);
   }

   public boolean onCreateOptionsMenu(Menu var1) {
      this.getMenuInflater().inflate(menu.menu_explorer, var1);
      this.mSearchView = (SearchView)var1.findItem(id.action_search).getActionView();
      this.mSearchView.setOnQueryTextListener(new OnQueryTextListener() {
         public boolean onQueryTextChange(String var1) {
            if (FileExplorerActivity.this.mFileListPagerFragment != null) {
               FileExplorerActivity.this.mFileListPagerFragment.filter(var1);
            }

            return false;
         }

         public boolean onQueryTextSubmit(String var1) {
            return false;
         }
      });
      Preferences var2 = Preferences.getInstance(this);
      var1.findItem(id.show_hidden_files_menu).setChecked(var2.isShowHiddenFiles());
      this.mPasteMenu = var1.findItem(id.paste_menu);
      int var3;
      switch(var2.getFileSortType()) {
      case 1:
         var3 = id.sort_by_datetime_menu;
         break;
      case 2:
         var3 = id.sort_by_size_menu;
         break;
      case 3:
         var3 = id.sort_by_type_menu;
         break;
      default:
         var3 = id.sort_by_name_menu;
      }

      var1.findItem(var3).setChecked(true);
      return super.onCreateOptionsMenu(var1);
   }

   protected void onDestroy() {
      super.onDestroy();
   }

   public boolean onOptionsItemSelected(MenuItem var1) {
      Preferences var2 = Preferences.getInstance(this);
      int var3 = var1.getItemId();
      if (var3 == id.show_hidden_files_menu) {
         var1.setChecked(var1.isChecked() ^ true);
         var2.setShowHiddenFiles(var1.isChecked());
      } else if (var3 == id.sort_by_name_menu) {
         var1.setChecked(true);
         var2.setFileSortType(0);
      } else if (var3 == id.sort_by_datetime_menu) {
         var1.setChecked(true);
         var2.setFileSortType(1);
      } else if (var3 == id.sort_by_size_menu) {
         var1.setChecked(true);
         var2.setFileSortType(2);
      } else if (var3 == id.sort_by_type_menu) {
         var1.setChecked(true);
         var2.setFileSortType(3);
      } else if (var3 == id.action_goto_home) {
         LocalFile var4 = new LocalFile(this.mHomePath);
         this.mFileListPagerFragment.switchToPath(var4);
      }

      return super.onOptionsItemSelected(var1);
   }

   boolean onSelectFile(JecFile var1) {
      if (var1.isFile()) {
         if (this.mMode == 1) {
            Intent var2 = new Intent();
            var2.putExtra("file", var1.getPath());
            var2.putExtra("encoding", this.fileEncoding);
            this.setResult(-1, var2);
            this.finish();
         }

         return true;
      } else {
         if (var1.isDirectory()) {
            this.mLastPath = var1.getPath();
            this.binding.filenameEditText.setText(var1.getPath());
         }

         return false;
      }
   }
}