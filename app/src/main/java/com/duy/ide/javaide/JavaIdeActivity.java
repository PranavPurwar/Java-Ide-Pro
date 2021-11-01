/* Decompiler 37ms, total 695ms, lines 388 */
package com.duy.ide.javaide;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog.Builder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.android.utils.StdLogger;
import com.android.utils.StdLogger.Level;
import com.duy.android.compiler.builder.AndroidAppBuilder;
import com.duy.android.compiler.builder.BuildTask;
import com.duy.android.compiler.builder.JavaBuilder;
import com.duy.android.compiler.builder.BuildTask.CompileListener;
import com.duy.android.compiler.project.AndroidAppProject;
import com.duy.android.compiler.project.FileCollection;
import com.duy.android.compiler.project.JavaProject;
import com.duy.android.compiler.project.JavaProjectManager;
import com.duy.android.compiler.utils.ProjectUtils;
import com.duy.common.purchase.InAppPurchaseHelper;
import com.duy.ide.code.api.CodeFormatProvider;
import com.duy.ide.code.api.SuggestionProvider;
import com.duy.ide.diagnostic.DiagnosticContract.MessageFilter;
import com.duy.ide.diagnostic.DiagnosticContract.Presenter;
import com.duy.ide.diagnostic.model.Message;
import com.duy.ide.diagnostic.model.Message.Kind;
import com.duy.ide.diagnostic.parser.PatternAwareOutputParser;
import com.duy.ide.editor.IEditorDelegate;
import com.duy.ide.javaide.diagnostic.parser.aapt.AaptOutputParser;
import com.duy.ide.javaide.diagnostic.parser.java.JavaOutputParser;
import com.duy.ide.javaide.editor.autocomplete.JavaAutoCompleteProvider;
import com.duy.ide.javaide.editor.format.JavaIdeCodeFormatProvider;
import com.duy.ide.javaide.menu.JavaMenuManager;
import com.duy.ide.javaide.run.activities.ExecuteActivity;
import com.duy.ide.javaide.run.dialog.DialogRunConfig.OnConfigChangeListener;
import com.duy.ide.javaide.sample.activities.JavaSampleActivity;
import com.duy.ide.javaide.setting.CompilerSettingActivity;
import com.duy.ide.javaide.theme.ThemeActivity;
import com.duy.ide.javaide.uidesigner.inflate.DialogLayoutPreview;
import com.duy.ide.javaide.utils.RootUtils;
import com.duy.ide.javaide.utils.StoreUtil;
import com.duy.ide.R;
import com.jecelyin.editor.v2.common.Command;
import com.jecelyin.editor.v2.common.Command.CommandEnum;
import com.jecelyin.editor.v2.manager.MenuManager;
import com.pluscubed.logcat.ui.LogcatActivity;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class JavaIdeActivity extends ProjectManagerActivity implements OnConfigChangeListener {
	private static final int RC_BUILD_PROJECT = 131;
	private static final int RC_CHANGE_THEME = 350;
	private static final int RC_OPEN_SAMPLE = 1015;
	private static final int RC_REVIEW_LAYOUT = 741;
	private static final String TAG = "MainActivity";
	private SuggestionProvider mAutoCompleteProvider;
	private ProgressBar mCompileProgress;
	private InAppPurchaseHelper mInAppPurchaseHelper;
	private AaptOutputParser aaptParser = new AaptOutputParser();
	private JavaOutputParser javaParser = new JavaOutputParser();
	
	private void compileAndroidProject() {
		if (this.mProject instanceof AndroidAppProject) {
			if (!((AndroidAppProject)this.mProject).getManifestFile().exists()) {
				Toast.makeText(this, "Can not find AndroidManifest.xml", 0).show();
				return;
			}
			
			if (((AndroidAppProject)this.mProject).getLauncherActivity() == null) {
				Toast.makeText(this, this.getString(2131689530), 0).show();
				return;
			}
			
			AndroidAppBuilder var1 = new AndroidAppBuilder(this, (AndroidAppProject)this.mProject);
			var1.setStdOut(new PrintStream(this.mDiagnosticPresenter.getStandardOutput()));
			var1.setStdErr(new PrintStream(this.mDiagnosticPresenter.getErrorOutput()));
			var1.setLogger(new StdLogger(Level.VERBOSE));
			(new BuildTask(var1, new CompileListener<AndroidAppProject>() {
				public void onComplete() {
					JavaIdeActivity.this.updateUIFinish();
					Toast.makeText(JavaIdeActivity.this, 2131689525, 0).show();
					JavaIdeActivity.this.mFilePresenter.refresh(JavaIdeActivity.this.mProject);
					RootUtils.installApk(JavaIdeActivity.this, ((AndroidAppProject)JavaIdeActivity.this.mProject).getApkSigned());
				}
				
				public void onError(Exception var1) {
					JavaIdeActivity.this.updateUIFinish();
					Toast.makeText(JavaIdeActivity.this, 2131689658, 0).show();
				}
				
				public void onStart() {
					JavaIdeActivity.this.updateUiStartCompile();
				}
			})).execute(new AndroidAppProject[0]);
		} else if (this.mProject != null) {
			this.toast("This is Java project, please create new Android project");
		} else {
			this.toast("You need create Android project");
		}
		
	}
	
	private void compileJavaProject() {
		JavaBuilder var1 = new JavaBuilder(this, this.mProject);
		var1.setStdOut(new PrintStream(this.mDiagnosticPresenter.getStandardOutput()));
		var1.setStdErr(new PrintStream(this.mDiagnosticPresenter.getErrorOutput()));
		(new BuildTask(var1, new CompileListener<JavaProject>() {
			public void onComplete() {
				JavaIdeActivity.this.updateUIFinish();
				Toast.makeText(JavaIdeActivity.this, 2131689577, 0).show();
				JavaIdeActivity.this.runJava(JavaIdeActivity.this.mProject);
			}
			
			public void onError(Exception var1) {
				Toast.makeText(JavaIdeActivity.this, 2131689658, 0).show();
				JavaIdeActivity.this.mDiagnosticPresenter.showPanel();
				JavaIdeActivity.this.updateUIFinish();
			}
			
			public void onStart() {
				JavaIdeActivity.this.updateUiStartCompile();
			}
		})).execute(new AndroidAppProject[0]);
	}
	
	private void populateAutoCompleteService(@NonNull SuggestionProvider var1) {
		Iterator var2 = this.getTabManager().getEditorPagerAdapter().getAllEditor().iterator();
		
		while(var2.hasNext()) {
			IEditorDelegate var3 = (IEditorDelegate)var2.next();
			if (var3 != null) {
				var3.setSuggestionProvider(var1);
			}
		}
		
	}
	
	private void runJava(final JavaProject var1) {
		File var2 = this.getCurrentFile();
		if (var2 != null && ProjectUtils.isFileBelongProject(var1, var2)) {
			Intent var7 = new Intent(this, ExecuteActivity.class);
			var7.putExtra("DEX_FILE", var1.getDexFile());
			var7.putExtra("MAIN_CLASS_FILE", var2);
			this.startActivity(var7);
		} else {
			ArrayList var3 = new ArrayList();
			var3.add(var1.getJavaSrcDir());
			final ArrayList<File> javaSources = (new FileCollection(var3)).filter(new FileFilter() {
				public boolean accept(File var1) {
					boolean var2;
					if (var1.isFile() && var1.getName().endsWith(".java")) {
						var2 = true;
					} else {
						var2 = false;
					}
					
					return var2;
				}
			});
			Builder var4 = new Builder(this);
			String[] var6 = new String[javaSources.size()];
			
			for(int var5 = 0; var5 < javaSources.size(); ++var5) {
				var6[var5] = ((File)javaSources.get(var5)).getName();
			}
			
			var4.setTitle(R.string.select_class_to_run);
			var4.setItems(var6, new OnClickListener() {
				public void onClick(DialogInterface var1x, int var2) {
					Intent var3x = new Intent(JavaIdeActivity.this, ExecuteActivity.class);
					var3x.putExtra("DEX_FILE", var1.getDexFile());
					var3x.putExtra("MAIN_CLASS_FILE", (Serializable)javaSources.get(var2));
					JavaIdeActivity.this.startActivity(var3x);
				}
			});
			var4.create().show();
		}
		
	}
	
	private void updateUIFinish() {
		this.setMenuStatus(2131296314, 0);
		if (this.mCompileProgress != null) {
			this.mCompileProgress.setVisibility(8);
		}
		
	}
	
	private void updateUiStartCompile() {
		this.setMenuStatus(2131296314, 2);
		if (this.mCompileProgress != null) {
			this.mCompileProgress.setVisibility(0);
		}
		
		this.mDiagnosticPresenter.setCurrentItem(1);
		this.mDiagnosticPresenter.showPanel();
		this.mDiagnosticPresenter.clear();
	}
	
	public void createNewFile(View var1) {
	}
	
	protected CodeFormatProvider getCodeFormatProvider() {
		return new JavaIdeCodeFormatProvider(this);
	}
	
	protected void onActivityResult(final int var1, final int var2, final Intent var3) {
		System.gc();
		final Context context = this;
		new Thread(new Runnable() {
			public void run() {
				if (var1 != 350) {
					if (var1 != 1015) {
						return;
					} else if (var2 == -1) {
						String var7 = var3.getStringExtra("project_file");
						JavaProjectManager var4 = new JavaProjectManager(context);
						
						JavaProject var8;
						try {
							File var5 = new File(var7);
							var8 = var4.loadProject(var5, true);
						} catch (IOException var6) {
							var6.printStackTrace();
							var8 = null;
						}
						
						if (var8 != null) {
							JavaIdeActivity.this.onProjectCreated(var8);
						}
					}
				} else {
					JavaIdeActivity.this.doCommandForAllEditor(new Command(CommandEnum.REFRESH_THEME));
				}
			}
		}).start();
		
	}
	
	public void onConfigChange(JavaProject var1) {
		this.mProject = var1;
		if (var1 != null) {
			JavaProjectManager.saveProject(this, var1);
		}
		
	}
	
	protected void onCreate(Bundle var1) {
		super.onCreate(var1);
		this.mCompileProgress = (ProgressBar)this.findViewById(R.id.compile_progress);
		this.startAutoCompleteService();
		this.mInAppPurchaseHelper = new InAppPurchaseHelper(this);
	}
	
	protected void onCreateNavigationMenu(Menu var1) {
		this.getMenuInflater().inflate(R.menu.menu_nav_javaide, var1);
		var1.findItem(R.id.action_premium).setVisible(false);
		super.onCreateNavigationMenu(var1);
	}
	
	public boolean onCreateOptionsMenu(Menu var1) {
		var1.add(0, R.id.action_run, 0, R.string.run).setIcon(MenuManager.makeToolbarNormalIcon(this, R.drawable.ic_play_arrow_white_24dp)).setShowAsAction(2);
		super.onCreateOptionsMenu(var1);
		MenuItem var2 = var1.findItem(R.id.menu_file);
		(new JavaMenuManager(this)).createFileMenu(var2.getSubMenu());
		return true;
	}
	
	public void onEditorViewCreated(@NonNull IEditorDelegate var1) {
		super.onEditorViewCreated(var1);
		var1.setSuggestionProvider(this.mAutoCompleteProvider);
	}
	
	public void onEditorViewDestroyed(@NonNull IEditorDelegate var1) {
		super.onEditorViewDestroyed(var1);
	}
	
	public boolean onOptionsItemSelected(MenuItem var1) {
		switch(var1.getItemId()) {
			case R.id.action_compiler_setting:
			this.startActivity(new Intent(this, CompilerSettingActivity.class));
			return true;
			case R.id.action_editor_color_scheme:
			this.startActivityForResult(new Intent(this, ThemeActivity.class), 350);
			break;
			case R.id.action_install_cpp_nide:
			StoreUtil.gotoPlayStore(this, "com.duy.c.cpp.compiler");
			break;
			case R.id.action_new_android_project:
			this.createAndroidProject();
			break;
			case R.id.action_new_class:
			this.createNewClass((File)null);
			break;
			case R.id.action_new_file:
			this.createNewFile((View)null);
			break;
			case R.id.action_new_java_project:
			this.createJavaProject();
			break;
			case R.id.action_open_android_project:
			this.openAndroidProject();
			break;
			case R.id.action_open_java_project:
			this.openJavaProject();
			break;
			case R.id.action_report_bug:
			this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://github.com/tranleduy2000/javaide/issues")));
			break;
			case R.id.action_run:
			this.saveAll(131);
			break;
			case R.id.action_sample:
			this.startActivityForResult(new Intent(this, JavaSampleActivity.class), 1015);
			break;
			case R.id.action_see_logcat:
			this.startActivity(new Intent(this, LogcatActivity.class));
		}
		
		return super.onOptionsItemSelected(var1);
	}
	
	protected void onSaveComplete(int var1) {
		super.onSaveComplete(var1);
		if (var1 != 131) {
			if (var1 == 741) {
				File var2 = this.getCurrentFile();
				if (var2 != null) {
					DialogLayoutPreview.newInstance(var2).show(this.getSupportFragmentManager(), "DialogLayoutPreview");
				} else {
					Toast.makeText(this, "Can not find file", 0).show();
				}
			}
		} else if (this.mProject != null) {
			if (this.mProject instanceof AndroidAppProject) {
				this.compileAndroidProject();
			} else {
				this.compileJavaProject();
			}
		} else {
			Toast.makeText(this, "You need create project", 0).show();
		}
		
	}
	
	protected void populateDiagnostic(@NonNull Presenter var1) {
		var1.setOutputParser(new PatternAwareOutputParser[]{this.aaptParser, this.javaParser});
		var1.setFilter(new MessageFilter() {
			public boolean accept(Message var1) {
				boolean var2;
				if (var1.getKind() != Kind.ERROR && var1.getKind() != Kind.WARNING) {
					var2 = false;
				} else {
					var2 = true;
				}
				
				return var2;
			}
		});
	}
	
	public void previewLayout(String var1) {
		this.saveAll(741);
	}
	
	protected void startAutoCompleteService() {
		Log.d("MainActivity", "startAutoCompleteService() called");
		if (this.mAutoCompleteProvider == null) {
			if (this.mProject != null) {
				(new Thread(new Runnable() {
					public void run() {
						JavaAutoCompleteProvider var1 = new JavaAutoCompleteProvider(JavaIdeActivity.this);
						var1.load(JavaIdeActivity.this.mProject);
						JavaIdeActivity.this.mAutoCompleteProvider = var1;
						JavaIdeActivity.this.populateAutoCompleteService(JavaIdeActivity.this.mAutoCompleteProvider);
					}
				})).start();
			}
		} else {
			this.populateAutoCompleteService(this.mAutoCompleteProvider);
		}
		
	}
}
