package com.duy.ide.javaide;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.duy.common.purchase.Premium;
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
import com.duy.ide.javaide.theme.PremiumDialog;
import com.duy.ide.javaide.theme.ThemeActivity;
import com.duy.ide.javaide.uidesigner.inflate.DialogLayoutPreview;
import com.duy.ide.javaide.utils.RootUtils;
import com.duy.ide.javaide.utils.StoreUtil;
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

import com.duy.ide.R;

public class JavaIdeActivity extends ProjectManagerActivity implements OnConfigChangeListener {
	private static final int RC_BUILD_PROJECT = 131;
	private static final int RC_CHANGE_THEME = 350;
	private static final int RC_OPEN_SAMPLE = 1015;
	private static final int RC_REVIEW_LAYOUT = 741;
	private static final String TAG = "MainActivity";
	private SuggestionProvider mAutoCompleteProvider;
	private ProgressBar mCompileProgress;
	private InAppPurchaseHelper mInAppPurchaseHelper;
	
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
			
			AndroidAppBuilder builder = new AndroidAppBuilder(this, (AndroidAppProject)this.mProject);
			builder.setStdOut(new PrintStream(this.mDiagnosticPresenter.getStandardOutput()));
			builder.setStdErr(new PrintStream(this.mDiagnosticPresenter.getErrorOutput()));
			builder.setLogger(new StdLogger(Level.VERBOSE));
			new BuildTask(builder, new CompileListener<AndroidAppProject>() {
				public void onComplete() {
					JavaIdeActivity.this.updateUIFinish();
					Toast.makeText(JavaIdeActivity.this, 2131689525, 0).show();
					JavaIdeActivity.this.mFilePresenter.refresh(JavaIdeActivity.this.mProject);
				}
				
				public void onError(Exception e) {
					JavaIdeActivity.this.updateUIFinish();
					Toast.makeText(JavaIdeActivity.this, 2131689658, 0).show();
				}
				
				public void onStart() {
					JavaIdeActivity.this.updateUiStartCompile();
				}
			}).execute(new AndroidAppProject[0]);
		} else if (this.mProject != null) {
			this.toast("This is a Java project, please create a new Android project");
		} else {
			this.toast("You need to create an Android project");
		}
		
	}
	
	private void compileJavaProject() {
		JavaBuilder builder = new JavaBuilder(this, this.mProject);
		builder.setStdOut(new PrintStream(this.mDiagnosticPresenter.getStandardOutput()));
		builder.setStdErr(new PrintStream(this.mDiagnosticPresenter.getErrorOutput()));
		new BuildTask(builder, new CompileListener<JavaProject>() {
			public void onComplete() {
				JavaIdeActivity.this.updateUIFinish();
				Toast.makeText(JavaIdeActivity.this, 2131689577, 0).show();
				JavaIdeActivity.this.runJava(JavaIdeActivity.this.mProject);
			}
			
			public void onError(Exception e) {
				Toast.makeText(JavaIdeActivity.this, 2131689658, 0).show();
				JavaIdeActivity.this.mDiagnosticPresenter.showPanel();
				JavaIdeActivity.this.updateUIFinish();
			}
			
			public void onStart() {
				JavaIdeActivity.this.updateUiStartCompile();
			}
		}).execute(new AndroidAppProject[0]);
	}
	
	private void populateAutoCompleteService(@NonNull SuggestionProvider provider) {
		Iterator it = this.getTabManager().getEditorPagerAdapter().getAllEditor().iterator();
		
		while(it.hasNext()) {
			IEditorDelegate delegate = (IEditorDelegate)it.next();
			if (delegate != null) {
				delegate.setSuggestionProvider(provider);
			}
		}
		
	}
	
	private void runJava(final JavaProject project) {
		File current = this.getCurrentFile();
		if (current != null && ProjectUtils.isFileBelongProject(project, current)) {
			Intent intent = new Intent(this, ExecuteActivity.class);
			intent.putExtra("DEX_FILE", project.getDexFile());
			intent.putExtra("MAIN_CLASS_FILE", current);
			this.startActivity(intent);
		} else {
			ArrayList list = new ArrayList();
			list.add(project.getJavaSrcDir());
			final ArrayList<File> javaSources = (new FileCollection(list)).filter(new FileFilter() {
				public boolean accept(File file) {
					if (file.isFile() && file.getName().endsWith(".java")) {
						return true;
					} else {
						return false;
					}
				}
			});
			Builder dialog = new Builder(this);
			String[] items = new String[javaSources.size()];
			
			for(int pos = 0; pos < javaSources.size(); ++pos) {
				items[pos] = ((File)javaSources.get(pos)).getName();
			}
			
			dialog.setTitle(R.string.select_class_to_run);
			dialog.setItems(items, new OnClickListener() {
				public void onClick(DialogInterface unused, int index) {
					Intent intent = new Intent(JavaIdeActivity.this, ExecuteActivity.class);
					intent.putExtra("DEX_FILE", project.getDexFile());
					intent.putExtra("MAIN_CLASS_FILE", (Serializable)javaSources.get(index));
					JavaIdeActivity.this.startActivity(intent);
				}
			});
			dialog.create().show();
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
	
	public void createNewFile(View view) {
	}
	
	protected CodeFormatProvider getCodeFormatProvider() {
		return new JavaIdeCodeFormatProvider(this);
	}
	
	protected void onActivityResult(int var1, int var2, Intent intent) {
		if (var1 != 350) {
			if (var1 != 1015) {
				super.onActivityResult(var1, var2, intent);
			} else if (var2 == -1) {
				String file = intent.getStringExtra("project_file");
				JavaProjectManager manager = new JavaProjectManager(this);
				
				JavaProject project;
				try {
					File path = new File(file);
					project = manager.loadProject(path, true);
				} catch (IOException e) {
					e.printStackTrace();
					project = null;
				}
				
				if (project != null) {
					this.onProjectCreated(project);
				}
			}
		} else {
			this.doCommandForAllEditor(new Command(CommandEnum.REFRESH_THEME));
		}
		
	}
	
	public void onConfigChange(JavaProject project) {
		this.mProject = project;
		if (project != null) {
			JavaProjectManager.saveProject(this, project);
		}
		
	}
	
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		this.mCompileProgress = (ProgressBar)this.findViewById(2131296382);
		this.startAutoCompleteService();
		this.mInAppPurchaseHelper = new InAppPurchaseHelper(this);
	}
	
	protected void onCreateNavigationMenu(Menu menu) {
		this.getMenuInflater().inflate(2131492866, menu);
		if (Premium.isPremiumUser(this)) {
			menu.findItem(2131296310).setVisible(false);
		}
		
		super.onCreateNavigationMenu(menu);
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 2131296314, 0, 2131689951).setIcon(MenuManager.makeToolbarNormalIcon(this, 2131231026)).setShowAsAction(2);
		super.onCreateOptionsMenu(menu);
		MenuItem item = menu.findItem(2131296553);
		new JavaMenuManager(this).createFileMenu(item.getSubMenu());
		return true;
	}
	
	public void onEditorViewCreated(@NonNull IEditorDelegate delegate) {
		super.onEditorViewCreated(delegate);
		delegate.setSuggestionProvider(this.mAutoCompleteProvider);
	}
	
	public void onEditorViewDestroyed(@NonNull IEditorDelegate delegate) {
		super.onEditorViewDestroyed(delegate);
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case 2131296272:
			this.startActivity(new Intent(this, CompilerSettingActivity.class));
			return true;
			case 2131296283:
			this.startActivityForResult(new Intent(this, ThemeActivity.class), 350);
			break;
			case 2131296296:
			StoreUtil.gotoPlayStore(this, "com.duy.c.cpp.compiler");
			break;
			case 2131296302:
			this.createAndroidProject();
			break;
			case 2131296303:
			this.createNewClass((File)null);
			break;
			case 2131296304:
			this.createNewFile((View)null);
			break;
			case 2131296305:
			this.createJavaProject();
			break;
			case 2131296307:
			this.openAndroidProject();
			break;
			case 2131296308:
			this.openJavaProject();
			break;
			case 2131296310:
			new PremiumDialog(this, this.mInAppPurchaseHelper).show();
			break;
			case 2131296313:
			this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://github.com/tranleduy2000/javaide/issues")));
			break;
			case 2131296314:
			this.saveAll(131);
			break;
			case 2131296315:
			this.startActivityForResult(new Intent(this, JavaSampleActivity.class), 1015);
			break;
			case 2131296320:
			this.startActivity(new Intent(this, LogcatActivity.class));
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	protected void onSaveComplete(int code) {
		super.onSaveComplete(code);
		if (code != 131) {
			if (code == 741) {
				File current = this.getCurrentFile();
				if (current != null) {
					DialogLayoutPreview.newInstance(current).show(this.getSupportFragmentManager(), "DialogLayoutPreview");
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
			Toast.makeText(this, "You need to create a project", 0).show();
		}
		
	}
	
	protected void populateDiagnostic(@NonNull Presenter presenter) {
		presenter.setOutputParser(new PatternAwareOutputParser[]{new AaptOutputParser(), new JavaOutputParser()});
		presenter.setFilter(new MessageFilter() {
			public boolean accept(Message message) {
				if (message.getKind() != Kind.ERROR && message.getKind() != Kind.WARNING) {
					return false;
				} else {
					return true;
				}
			}
		});
	}
	
	public void previewLayout(String unused) {
		this.saveAll(741);
	}
	
	protected void startAutoCompleteService() {
		Log.d(TAG, "startAutoCompleteService() called");
		if (this.mAutoCompleteProvider == null) {
			if (this.mProject != null) {
				new Thread(new Runnable() {
					public void run() {
						JavaAutoCompleteProvider provider = new JavaAutoCompleteProvider(JavaIdeActivity.this);
						provider.load(JavaIdeActivity.this.mProject);
						JavaIdeActivity.this.mAutoCompleteProvider = provider;
						JavaIdeActivity.this.populateAutoCompleteService(JavaIdeActivity.this.mAutoCompleteProvider);
					}
				}).start();
			}
		} else {
			this.populateAutoCompleteService(this.mAutoCompleteProvider);
		}
		
	}
}
