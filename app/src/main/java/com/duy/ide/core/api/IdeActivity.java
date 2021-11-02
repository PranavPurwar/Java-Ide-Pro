package com.duy.ide.core.api;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Build.VERSION;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.SimpleDrawerListener;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;
import android.widget.Toast;
import com.duy.common.StoreUtil;
import com.duy.common.io.IOUtils;
import com.duy.file.explorer.FileExplorerActivity;
import com.duy.ide.code.api.CodeFormatProvider;
import com.duy.ide.code.format.CodeFormatProviderImpl;
import com.duy.ide.database.ITabDatabase;
import com.duy.ide.database.SQLHelper;
import com.duy.ide.diagnostic.DiagnosticPresenter;
import com.duy.ide.diagnostic.DiagnosticContract.Presenter;
import com.duy.ide.diagnostic.view.DiagnosticFragment;
import com.duy.ide.editor.EditorDelegate;
import com.duy.ide.editor.IEditorDelegate;
import com.duy.ide.editor.IEditorStateListener;
import com.duy.ide.editor.editor.R.drawable;
import com.duy.ide.editor.editor.R.id;
import com.duy.ide.editor.editor.R.string;
import com.duy.ide.editor.task.SaveAllTask;
import com.duy.ide.file.FileManager;
import com.duy.ide.file.SaveListener;
import com.duy.ide.file.dialogs.DialogNewFile;
import com.duy.ide.file.dialogs.DialogNewFile.OnCreateFileListener;
import com.duy.ide.settings.EditorSettingsActivity;
import com.jecelyin.common.utils.DLog;
import com.jecelyin.common.utils.SysUtils;
import com.jecelyin.common.utils.UIUtils;
import com.jecelyin.common.utils.UIUtils.OnClickCallback;
import com.jecelyin.editor.v2.Preferences;
import com.jecelyin.editor.v2.ThemeSupportActivity;
import com.jecelyin.editor.v2.common.Command;
import com.jecelyin.editor.v2.common.Command.CommandEnum;
import com.jecelyin.editor.v2.dialog.CharsetsDialog;
import com.jecelyin.editor.v2.dialog.GotoLineDialog;
import com.jecelyin.editor.v2.dialog.LangListDialog;
import com.jecelyin.editor.v2.manager.MenuManager;
import com.jecelyin.editor.v2.manager.RecentFilesManager;
import com.jecelyin.editor.v2.manager.TabManager;
import com.jecelyin.editor.v2.manager.RecentFilesManager.OnFileItemClickListener;
import com.jecelyin.editor.v2.widget.SymbolBarLayout;
import com.jecelyin.editor.v2.widget.SymbolBarLayout.OnSymbolCharClickListener;
import com.jecelyin.editor.v2.widget.menu.MenuFactory;
import com.jecelyin.editor.v2.widget.menu.MenuGroup;
import com.jecelyin.editor.v2.widget.menu.MenuItemInfo;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.SimplePanelSlideListener;
import java.io.File;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map.Entry;
import org.gjt.sp.jedit.Catalog;
import org.gjt.sp.jedit.Mode;

public abstract class IdeActivity extends ThemeSupportActivity implements OnMenuItemClickListener, IEditorStateListener, OnSharedPreferenceChangeListener {
   protected static final int RC_OPEN_FILE = 1;
   private static final int RC_PERMISSION_WRITE_STORAGE = 5001;
   private static final int RC_SAVE_AS = 3;
   private static final int RC_SETTINGS = 5;
   protected DiagnosticPresenter mDiagnosticPresenter;
   protected DrawerLayout mDrawerLayout;
   private long mExitTime;
   private final Handler mHandler = new Handler();
   private IdeActivity.KeyBoardEventListener mKeyBoardListener;
   protected Preferences mPreferences;
   public SlidingUpPanelLayout mSlidingUpPanelLayout;
   @Nullable
   protected SymbolBarLayout mSymbolBarLayout;
   @Nullable
   protected SmartTabLayout mTabLayout;
   protected TabManager mTabManager;
   protected Toolbar mToolbar;
   @Nullable
   protected TextView mTxtDocumentInfo;

   private void createNewEditor(File file, int pos, String name) {
      if (this.mTabManager.newTab(file, pos, name)) {
         SQLHelper.getInstance(this).addRecentFile(file.getPath(), name);
      }
   }

   private void initEditorView() {
      this.mTabManager = new TabManager(this, (ViewPager)this.findViewById(id.editor_view_pager));
      this.mTabManager.createEditor();
   }

   private void initMenuView() {
      NavigationView view = (NavigationView)this.findViewById(id.right_navigation_view);
      this.onCreateNavigationMenu(view.getMenu());
      view.setNavigationItemSelectedListener(new OnNavigationItemSelectedListener() {
         public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            return IdeActivity.this.onOptionsItemSelected(item);
         }
      });
   }

   private void initToolbar() {
      this.mToolbar = (Toolbar)this.findViewById(id.toolbar);
      this.setSupportActionBar(this.mToolbar);
      this.setTitle("");
   }

   private void intiDiagnosticView() {
      if (this.mTabManager == null) {
         throw new RuntimeException("Create TabManager before call intiDiagnosticView");
      } else {
         final View panel = this.findViewById(id.btn_toggle_panel);
         this.mSlidingUpPanelLayout = (SlidingUpPanelLayout)this.findViewById(id.diagnostic_panel);
         this.mSlidingUpPanelLayout.addPanelSlideListener(new SimplePanelSlideListener() {
            public void onPanelSlide(View view, float value) {
               panel.animate().rotation(value * 180.0F).start();
            }
         });
         FragmentManager manager = this.getSupportFragmentManager();
         String className = DiagnosticFragment.class.getSimpleName();
         DiagnosticFragment diagnostic = (DiagnosticFragment)manager.findFragmentByTag(className);
         if (diagnostic == null) {
            diagnostic = DiagnosticFragment.newInstance();
         }

         manager.beginTransaction().replace(id.container_diagnostic_list_view, diagnostic, className).commit();
         this.mDiagnosticPresenter = new DiagnosticPresenter(diagnostic, this, this.mTabManager, this.mHandler);
         this.populateDiagnostic(this.mDiagnosticPresenter);
      }
   }

   private void openText(CharSequence sequence) {
      if (!TextUtils.isEmpty(sequence)) {
         FileManager fileManager = new FileManager(this);
         File file = fileManager.createNewFile("untitled_" + System.currentTimeMillis() + ".txt");
         try {
            IOUtils.writeAndClose(sequence.toString(), file);
            this.mTabManager.newTab(file);
         } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), 0).show();
         }

      }
   }

   private void processIntent() {
      try {
         if (!this.processIntentImpl()) {
            UIUtils.alert(this, this.getString(string.cannt_handle_intent_x, new Object[]{this.getIntent().toString()}));
         }
      } catch (Throwable e) {
         DLog.e(e);
         int var2 = string.handle_intent_x_error;
         StringBuilder var3 = new StringBuilder();
         var3.append(this.getIntent().toString());
         var3.append("\n");
         var3.append(e.getMessage());
         UIUtils.alert(this, this.getString(var2, new Object[]{var3.toString()}));
      }

   }

   private boolean processIntentImpl() throws Throwable {
      Intent var1 = this.getIntent();
      StringBuilder var2 = new StringBuilder();
      var2.append("intent=");
      var2.append(var1);
      DLog.d(var2.toString());
      if (var1 == null) {
         return true;
      } else {
         String var6 = var1.getAction();
         if (var6 != null && !"android.intent.action.MAIN".equals(var6)) {
            if (!"android.intent.action.VIEW".equals(var6) && !"android.intent.action.EDIT".equals(var6)) {
               if ("android.intent.action.SEND".equals(var6) && var1.getExtras() != null) {
                  Bundle var7 = var1.getExtras();
                  CharSequence var9 = var7.getCharSequence("android.intent.extra.TEXT");
                  if (var9 != null) {
                     this.openText(var9);
                     return true;
                  }

                  Object var10 = var7.get("android.intent.extra.STREAM");
                  if (var10 != null && var10 instanceof Uri) {
                     this.openFile(((Uri)var10).getPath());
                     return true;
                  }
               }
            } else if (var1.getScheme() != null && var1.getData() != null) {
               if (var1.getScheme().equals("content")) {
                  InputStream var8 = this.getContentResolver().openInputStream(var1.getData());

                  try {
                     this.openText(IOUtils.toString(var8));
                  } catch (OutOfMemoryError var3) {
                     UIUtils.toast(this, string.out_of_memory_error);
                  }

                  return true;
               }

               if (var1.getScheme().equals("file")) {
                  Uri var4 = var1.getData();
                  String var5;
                  if (var4 != null) {
                     var5 = var4.getPath();
                  } else {
                     var5 = null;
                  }

                  if (!TextUtils.isEmpty(var5)) {
                     this.openFile(var5);
                     return true;
                  }
               }
            }

            return false;
         } else {
            return true;
         }
      }
   }

   private void setScreenOrientation() {
      int var1 = this.mPreferences.getScreenOrientation();
      if (var1 == 0) {
         this.setRequestedOrientation(-1);
      } else if (1 == var1) {
         this.setRequestedOrientation(0);
      } else if (2 == var1) {
         this.setRequestedOrientation(1);
      }

   }

   protected boolean closeDrawers() {
      if (this.mDrawerLayout != null) {
         if (this.mDrawerLayout.isDrawerOpen(8388611)) {
            this.mDrawerLayout.closeDrawer(8388611);
            return true;
         }

         if (this.mDrawerLayout.isDrawerOpen(8388613)) {
            this.mDrawerLayout.closeDrawer(8388613);
            return true;
         }
      }

      return false;
   }

   public void createNewFile() {
      if (VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
         this.requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 5001);
      } else {
         String[] extensions = this.getSupportedFileExtensions();
         EditorDelegate delegate = this.getCurrentEditorDelegate();
         String path;
         if (delegate != null) {
            path = delegate.getPath();
            if (new File(path).isFile()) {
               path = new File(path).getParent();
            }
         } else {
            path = Environment.getExternalStorageDirectory().getPath();
         }

         DialogNewFile.newInstance(extensions, path, new OnCreateFileListener() {
            public void onFileCreated(@NonNull File file) {
               IdeActivity.this.mTabManager.newTab(file);
            }
         }).show(this.getSupportFragmentManager(), DialogNewFile.class.getSimpleName());
      }
   }

   public void doCommand(Command command) {
      EditorDelegate delegate = this.getCurrentEditorDelegate();
      if (delegate != null) {
         delegate.doCommand(command);
         if (command.what == CommandEnum.HIGHLIGHT && this.mToolbar != null) {
            this.mToolbar.setTitle(delegate.getToolbarText());
         }
      }

   }

   public void doCommandForAllEditor(Command command) {
      Iterator it = this.mTabManager.getEditorPagerAdapter().getAllEditor().iterator();

      while(it.hasNext()) {
         ((IEditorDelegate)it.next()).doCommand(command);
      }

   }

   @Nullable
   protected CodeFormatProvider getCodeFormatProvider() {
      return new CodeFormatProviderImpl(this);
   }

   protected EditorDelegate getCurrentEditorDelegate() {
      return this.mTabManager != null && this.mTabManager.getEditorPagerAdapter() != null ? this.mTabManager.getEditorPagerAdapter().getCurrentEditorDelegate() : null;
   }

   public String getCurrentLang() {
      EditorDelegate delegate = this.getCurrentEditorDelegate();
      return delegate == null ? null : delegate.getLang();
   }

   @LayoutRes
   protected abstract int getRootLayoutId();

   protected String[] getSupportedFileExtensions() {
      return new String[]{".txt", ".json"};
   }

   public TabManager getTabManager() {
      return this.mTabManager;
   }

   protected void hideSoftInput() {
      this.doCommand(new Command(CommandEnum.HIDE_SOFT_INPUT));
   }

   protected void initLeftNavigationView(@NonNull NavigationView navView) {
   }

   public void insertText(CharSequence sequence) {
      if (sequence != null) {
         Command command = new Command(CommandEnum.INSERT_TEXT);
         command.object = sequence;
         this.doCommand(command);
      }
   }

   @CallSuper
   protected void onActivityResult(int var1, int var2, Intent var3) {
      super.onActivityResult(var1, var2, var3);
      if (var2 == -1) {
         if (var1 != 1) {
            if (var1 == 3) {
               String var4 = FileExplorerActivity.getFile(var3);
               if (var4 == null) {
                  return;
               }

               String var6 = FileExplorerActivity.getFileEncoding(var3);
               EditorDelegate var5 = this.getCurrentEditorDelegate();
               if (var5 != null) {
                  var5.saveInBackground(new File(var4), var6);
                  ITabDatabase var7 = SQLHelper.getInstance(this);
                  var7.addRecentFile(var4, var6);
                  var7.updateRecentFile(var4, false);
               }
            }
         } else if (var3 != null) {
            this.openFile(FileExplorerActivity.getFile(var3), FileExplorerActivity.getFileEncoding(var3), var3.getIntExtra("offset", 0));
         }

      }
   }

   @CallSuper
   public void onBackPressed() {
      if (!this.closeDrawers()) {
         if (this.mTabManager.onDestroy()) {
            if (System.currentTimeMillis() - this.mExitTime > 2000L) {
               UIUtils.toast(this, string.press_again_will_exit);
               this.mExitTime = System.currentTimeMillis();
            } else {
               super.onBackPressed();
            }
         }

      }
   }

   protected void onCreate(Bundle bundle) {
      super.onCreate(bundle);
      this.setContentView(this.getRootLayoutId());
      this.initToolbar();
      MenuManager.init(this);
      this.mPreferences = Preferences.getInstance(this);
      this.mPreferences.registerOnSharedPreferenceChangeListener(this);
      this.mTabLayout = (SmartTabLayout)this.findViewById(id.tab_layout);
      this.mTxtDocumentInfo = (TextView)this.findViewById(id.txt_document_info);
      this.mDrawerLayout = (DrawerLayout)this.findViewById(id.drawer_layout);
      this.mDrawerLayout.setKeepScreenOn(this.mPreferences.isKeepScreenOn());
      this.mDrawerLayout.addDrawerListener(new SimpleDrawerListener() {
         public void onDrawerOpened(View view) {
            super.onDrawerOpened(view);
            EditorDelegate delegate = IdeActivity.this.getCurrentEditorDelegate();
            if (delegate != null) {
               delegate.getEditText().clearFocus();
            }

            IdeActivity.this.mDrawerLayout.requestFocus();
            IdeActivity.this.hideSoftInput();
         }
      });
      ActionBarDrawerToggle listener = new ActionBarDrawerToggle(this, this.mDrawerLayout, this.mToolbar, string.open_drawer, string.close_drawer);
      this.mDrawerLayout.addDrawerListener(listener);
      listener.syncState();
      this.mSymbolBarLayout = (SymbolBarLayout)this.findViewById(id.symbolBarLayout);
      if (this.mSymbolBarLayout != null) {
         this.mSymbolBarLayout.setOnSymbolCharClickListener(new OnSymbolCharClickListener() {
            public void onClick(View unused, String text) {
               IdeActivity.this.insertText(text);
            }
         });
      }

      this.setScreenOrientation();
      ((TextView)this.findViewById(id.versionTextView)).setText(SysUtils.getVersionName(this));
      this.initMenuView();
      this.initEditorView();
      this.intiDiagnosticView();
      this.initLeftNavigationView((NavigationView)this.findViewById(id.left_navigation_view));
      this.processIntent();
      this.mKeyBoardListener = new IdeActivity.KeyBoardEventListener(this);
      this.mDrawerLayout.getViewTreeObserver().addOnGlobalLayoutListener(this.mKeyBoardListener);
   }

   @CallSuper
   protected void onCreateNavigationMenu(Menu menu) {
      MenuFactory var2 = MenuFactory.getInstance(this);
      MenuGroup[] var3 = new MenuGroup[2];
      MenuGroup var4 = MenuGroup.VIEW;
      int var5 = 0;
      var3[0] = var4;
      var3[1] = MenuGroup.OTHER;

      for(int var6 = var3.length; var5 < var6; ++var5) {
         MenuGroup var7 = var3[var5];
         if (var7 != MenuGroup.TOP) {
            SubMenu var9 = menu.addSubMenu(var7.getTitleId());
            Iterator var8 = var2.getMenuItemsWithoutToolbarMenu(var7).iterator();

            while(var8.hasNext()) {
               MenuItemInfo var10 = (MenuItemInfo)var8.next();
               var9.add(2, var10.getItemId(), var10.getOrder(), var10.getTitleResId()).setIcon(var10.getIconResId());
            }
         }
      }

   }

   @CallSuper
   public boolean onCreateOptionsMenu(Menu var1) {
      MenuFactory var2 = MenuFactory.getInstance(this);
      Iterator var3 = var2.getToolbarIcon().iterator();

      while(var3.hasNext()) {
         MenuItemInfo var4 = (MenuItemInfo)var3.next();
         MenuItem var5 = var1.add(1, var4.getItemId(), var4.getOrder(), var4.getTitleResId());
         var5.setIcon(MenuManager.makeToolbarNormalIcon(this, var4.getIconResId()));
         var5.setShowAsAction(2);
      }

      MenuGroup[] var11 = new MenuGroup[]{MenuGroup.FILE, MenuGroup.EDIT};
      int var6 = var11.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         MenuGroup var13 = var11[var7];
         if (var13 != MenuGroup.TOP) {
            SubMenu var12 = var1.addSubMenu(1, var13.getMenuId(), 0, var13.getTitleId());
            var12.getItem().setShowAsAction(6);
            Iterator var8 = var2.getMenuItemsWithoutToolbarMenu(var13).iterator();

            while(var8.hasNext()) {
               MenuItemInfo var14 = (MenuItemInfo)var8.next();
               MenuItem var9 = var12.add(1, var14.getItemId(), var14.getOrder(), var14.getTitleResId());
               var9.setIcon(MenuManager.makeMenuNormalIcon(this, var14.getIconResId()));
               var9.setShowAsAction(2);
            }
         }
      }

      MenuItem var10 = var1.add(1, id.m_menu, 0, this.getString(string.more_menu));
      var10.setIcon(MenuManager.makeToolbarNormalIcon(this, drawable.ic_more_horiz_white_24dp));
      var10.setShowAsAction(2);
      return super.onCreateOptionsMenu(var1);
   }

   protected void onDestroy() {
      this.mDrawerLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this.mKeyBoardListener);
      super.onDestroy();
   }

   @CallSuper
   public void onEditorViewCreated(@NonNull IEditorDelegate var1) {
      var1.setCodeFormatProvider(this.getCodeFormatProvider());
   }

   public void onEditorViewDestroyed(@NonNull IEditorDelegate var1) {
   }

   protected void onHideKeyboard() {
      if (this.mTabLayout != null) {
         this.mTabLayout.setVisibility(0);
      }

      if (this.mTxtDocumentInfo != null) {
         this.mTxtDocumentInfo.setVisibility(8);
      }

   }

   public boolean onMenuItemClick(MenuItem var1) {
      this.closeDrawers();
      int var2 = var1.getItemId();
      if (var2 == id.action_new_file) {
         this.createNewFile();
      } else if (var2 == id.action_open) {
         this.openFileExplorer();
      } else if (var2 == id.action_goto_line) {
         EditorDelegate var4 = this.getCurrentEditorDelegate();
         if (var4 != null) {
            (new GotoLineDialog(this, var4)).show();
         }
      } else if (var2 == id.action_file_history) {
         RecentFilesManager var5 = new RecentFilesManager(this);
         var5.setOnFileItemClickListener(new OnFileItemClickListener() {
            public void onClick(String var1, String var2) {
               IdeActivity.this.openFile(var1, var2, 0);
            }
         });
         var5.show(this);
      } else if (var2 == id.action_highlight) {
         (new LangListDialog(this)).show();
      } else if (var2 == id.m_menu) {
         this.hideSoftInput();
         this.mDrawerLayout.postDelayed(new Runnable() {
            public void run() {
               IdeActivity.this.mDrawerLayout.openDrawer(8388613);
            }
         }, 200L);
      } else if (var2 == id.action_save_all) {
         UIUtils.toast(this, string.save_all);
         this.saveAll(0);
      } else {
         boolean var3;
         if (var2 == id.m_fullscreen) {
            var3 = this.mPreferences.isFullScreenMode();
            this.mPreferences.setFullScreenMode(var3 ^ true);
            if (var3) {
               var2 = string.disabled_fullscreen_mode_message;
            } else {
               var2 = string.enable_fullscreen_mode_message;
            }

            UIUtils.toast(this, var2);
         } else if (var2 == id.m_readonly) {
            var3 = this.mPreferences.isReadOnly();
            this.mPreferences.setReadOnly(var3 ^ true);
            this.doCommandForAllEditor(new Command(CommandEnum.READONLY_MODE));
         } else if (var2 == id.action_encoding) {
            (new CharsetsDialog(this)).show();
         } else if (var2 == id.action_editor_setting) {
            EditorSettingsActivity.open(this, 5);
         } else if (var2 == id.action_share) {
            StoreUtil.shareThisApp(this);
         } else if (var2 == id.action_rate) {
            StoreUtil.gotoPlayStore(this, this.getPackageName());
         } else {
            CommandEnum var6 = MenuFactory.getInstance(this).idToCommandEnum(var2);
            if (var6 != CommandEnum.NONE) {
               this.doCommand(new Command(var6));
            }
         }
      }

      return true;
   }

   protected void onNewIntent(Intent var1) {
      super.onNewIntent(var1);
      this.setIntent(var1);
      this.processIntent();
   }

   public boolean onOptionsItemSelected(MenuItem var1) {
      boolean var2;
      if (!this.onMenuItemClick(var1) && !super.onOptionsItemSelected(var1)) {
         var2 = false;
      } else {
         var2 = true;
      }

      return var2;
   }

   public void onRequestPermissionsResult(int var1, @NonNull String[] var2, @NonNull int[] var3) {
      super.onRequestPermissionsResult(var1, var2, var3);
      if (var1 == 5001 && var3.length > 0 && var3[0] == 0) {
         this.createNewFile();
      }

   }

   protected void onSaveComplete(int var1) {
   }

   public void onSharedPreferenceChanged(SharedPreferences var1, String var2) {
      byte var4;
      byte var6;
      label40: {
         super.onSharedPreferenceChanged(var1, var2);
         int var3 = var2.hashCode();
         var4 = 0;
         if (var3 != -330305548) {
            if (var3 != 675138944) {
               if (var3 == 1979352601 && var2.equals("pref_screen_orientation")) {
                  var6 = 1;
                  break label40;
               }
            } else if (var2.equals("readonly_mode")) {
               var6 = 2;
               break label40;
            }
         } else if (var2.equals("pref_keep_screen_on")) {
            var6 = 0;
            break label40;
         }

         var6 = -1;
      }

      switch(var6) {
      case 0:
         if (this.mToolbar != null) {
            this.mToolbar.setKeepScreenOn(var1.getBoolean(var2, false));
         }
         break;
      case 1:
         this.setScreenOrientation();
         break;
      case 2:
         if (this.mSymbolBarLayout != null) {
            SymbolBarLayout var5 = this.mSymbolBarLayout;
            byte var7 = var4;
            if (this.mPreferences.isReadOnly()) {
               var7 = 8;
            }

            var5.setVisibility(var7);
         }
      }

   }

   protected void onShowKeyboard() {
      if (this.mTabLayout != null) {
         this.mTabLayout.setVisibility(8);
      }

      if (this.mTxtDocumentInfo != null) {
         this.mTxtDocumentInfo.setVisibility(0);
      }

   }

   protected void openFile(String var1) {
      this.openFile(var1, (String)null, 0);
   }

   protected void openFile(String var1, final String var2, final int var3) {
      if (!TextUtils.isEmpty(var1)) {
         final File var6 = new File(var1);
         if (!var6.isFile()) {
            UIUtils.toast(this, string.file_not_exists);
         } else if (!var6.canRead()) {
            UIUtils.alert(this, this.getString(string.cannt_read_file, new Object[]{var6.getPath()}));
         } else {
            Iterator var4 = Catalog.modes.entrySet().iterator();

            boolean var5;
            while(true) {
               if (var4.hasNext()) {
                  if (!((Mode)((Entry)var4.next()).getValue()).accept(var6.getPath(), var6.getName(), "")) {
                     continue;
                  }

                  var5 = true;
                  break;
               }

               var5 = false;
               break;
            }

            if (!var5) {
               UIUtils.showConfirmDialog(this, this.getString(string.not_a_text_file, new Object[]{var6.getName()}), new OnClickCallback() {
                  public void onOkClick() {
                     IdeActivity.this.createNewEditor(var6, var3, var2);
                  }
               });
            } else {
               this.createNewEditor(var6, var3, var2);
            }

         }
      }
   }

   public void openFileExplorer() {
      EditorDelegate var1 = this.getCurrentEditorDelegate();
      String var2 = Environment.getExternalStorageDirectory().getAbsolutePath();
      String var3;
      if (var1 != null) {
         var3 = (new File(var1.getPath())).getParent();
      } else {
         var3 = var2;
      }

      FileExplorerActivity.startPickFileActivity(this, var3, var2, 1);
   }

   protected abstract void populateDiagnostic(@NonNull Presenter var1);

   public void saveAll(final int var1) {
      final ProgressDialog var2 = new ProgressDialog(this);
      var2.setTitle(string.saving);
      var2.setCanceledOnTouchOutside(false);
      var2.setCancelable(false);
      var2.show();
      (new SaveAllTask(this, new SaveListener() {
         public void onSaveFailed(Exception var1x) {
            var2.dismiss();
            UIUtils.alert(IdeActivity.this, var1x.getMessage());
         }

         public void onSavedSuccess() {
            var2.dismiss();
            IdeActivity.this.onSaveComplete(var1);
         }
      })).execute(new Void[0]);
   }

   public void setMenuStatus(@IdRes int var1, int var2) {
      if (this.mToolbar != null) {
         MenuItem var3 = this.mToolbar.getMenu().findItem(var1);
         if (var3 != null) {
            Drawable var4 = var3.getIcon();
            if (var2 == 2) {
               var3.setEnabled(false);
               var3.setIcon(MenuManager.makeToolbarDisabledIcon(var4));
            } else {
               var3.setEnabled(true);
               if (var3.getGroupId() == 1) {
                  var3.setIcon(MenuManager.makeToolbarNormalIcon(var4));
               } else {
                  var3.setIcon(MenuManager.makeMenuNormalIcon(var4));
               }
            }

         }
      }
   }

   public void startPickPathActivity(String var1, String var2) {
      FileExplorerActivity.startPickPathActivity(this, var1, var2, 3);
   }

   private class KeyBoardEventListener implements OnGlobalLayoutListener {
      IdeActivity activity;

      KeyBoardEventListener(IdeActivity var2) {
         this.activity = var2;
      }

      public void onGlobalLayout() {
         int var1 = this.activity.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
         int var2 = 0;
         if (var1 > 0) {
            var1 = this.activity.getResources().getDimensionPixelSize(var1);
         } else {
            var1 = 0;
         }

         int var3 = this.activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
         if (var3 > 0) {
            var2 = this.activity.getResources().getDimensionPixelSize(var3);
         }

         Rect var4 = new Rect();
         this.activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(var4);
         if (this.activity.mDrawerLayout.getRootView().getHeight() - (var1 + var2 + var4.height()) <= 0) {
            this.activity.onHideKeyboard();
         } else {
            this.activity.onShowKeyboard();
         }

      }
   }
}