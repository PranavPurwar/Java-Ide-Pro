/* Decompiler 96ms, total 1266ms, lines 323 */
package com.duy.file.explorer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.duy.file.explorer.adapter.FileListItemAdapter;
import com.duy.file.explorer.adapter.PathButtonAdapter;
import com.duy.file.explorer.io.JecFile;
import com.duy.file.explorer.listener.FileListResultListener;
import com.duy.file.explorer.listener.OnClipboardPasteFinishListener;
import com.duy.file.explorer.util.FileListSorter;
import com.duy.ide.editor.editor.R.dimen;
import com.duy.ide.editor.editor.R.id;
import com.duy.ide.editor.editor.R.layout;
import com.duy.ide.editor.editor.R.string;
import com.duy.ide.editor.editor.databinding.FileExplorerFragmentBinding;
import com.jecelyin.common.listeners.OnItemClickListener;
import com.jecelyin.common.task.JecAsyncTask;
import com.jecelyin.common.task.TaskListener;
import com.jecelyin.common.task.TaskResult;
import com.jecelyin.common.utils.UIUtils;
import com.jecelyin.editor.v2.Preferences;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration.Builder;
import java.util.ArrayList;
import java.util.Arrays;

public class FileListPagerFragment extends Fragment implements OnRefreshListener, OnItemClickListener, FileExplorerView, ExplorerContext, OnSharedPreferenceChangeListener {
   private static final String EXTRA_PATH = "path";
   private FileExplorerAction action;
   private FileListItemAdapter adapter;
   private FileExplorerFragmentBinding binding;
   private JecFile path;
   private PathButtonAdapter pathAdapter;
   private FileListPagerFragment.ScanFilesTask task;

   public static FileListPagerFragment newFragment(JecFile var0) {
      FileListPagerFragment var1 = new FileListPagerFragment();
      Bundle var2 = new Bundle();
      var2.putParcelable("path", var0);
      var1.setArguments(var2);
      return var1;
   }

   public void filter(String var1) {
      if (this.adapter != null) {
         this.adapter.filter(var1);
      }

   }

   public void finish() {
      this.getActivity().finish();
   }

   public JecFile getCurrentDirectory() {
      return this.path;
   }

   public boolean onBackPressed() {
      JecFile var1 = this.path.getParentFile();
      if (var1 != null && !var1.getPath().startsWith(this.path.getPath())) {
         return false;
      } else {
         this.switchToPath(var1);
         return true;
      }
   }

   public void onCreate(@Nullable Bundle var1) {
      super.onCreate(var1);
      this.setHasOptionsMenu(true);
   }

   @Nullable
   public View onCreateView(@NonNull LayoutInflater var1, ViewGroup var2, Bundle var3) {
      this.path = (JecFile)this.getArguments().getParcelable("path");
      this.binding = (FileExplorerFragmentBinding)DataBindingUtil.inflate(var1, layout.file_explorer_fragment, var2, false);
      return this.binding.getRoot();
   }

   public void onDestroyView() {
      super.onDestroyView();
      Preferences.getInstance(this.getContext()).unregisterOnSharedPreferenceChangeListener(this);
      if (this.action != null) {
         this.action.destroy();
      }

   }

   public void onItemClick(int var1, View var2) {
      JecFile var3 = this.adapter.getItem(var1);
      if (!((FileExplorerActivity)this.getActivity()).onSelectFile(var3) && var3.isDirectory()) {
         this.switchToPath(var3);
      }

   }

   public boolean onOptionsItemSelected(MenuItem var1) {
      if (var1.getItemId() == id.paste_menu) {
         final FileClipboard var2 = ((FileExplorerActivity)this.getActivity()).getFileClipboard();
         var2.paste(this.getContext(), this.getCurrentDirectory(), new OnClipboardPasteFinishListener() {
            public void onFinish(int var1, String var2x) {
               FileListPagerFragment.this.onRefresh();
               var2.showPasteResult(FileListPagerFragment.this.getContext(), var1, var2x);
            }
         });
         var1.setVisible(false);
      } else if (var1.getItemId() == id.add_folder_menu) {
         this.action.doCreateFolder();
      }

      return super.onOptionsItemSelected(var1);
   }

   public void onRefresh() {
      FileListPagerFragment.UpdateRootInfo var1 = new FileListPagerFragment.UpdateRootInfo() {
         public void onUpdate(JecFile var1) {
            FileListPagerFragment.this.path = var1;
         }
      };
      this.task = new FileListPagerFragment.ScanFilesTask(this.getActivity(), this.path, var1);
      this.task.setTaskListener(new TaskListener<JecFile[]>() {
         public void onCompleted() {
            if (FileListPagerFragment.this.binding.explorerSwipeRefreshLayout != null) {
               FileListPagerFragment.this.binding.explorerSwipeRefreshLayout.setRefreshing(false);
            }

         }

         public void onError(Exception var1) {
            if (FileListPagerFragment.this.binding.explorerSwipeRefreshLayout != null) {
               FileListPagerFragment.this.binding.explorerSwipeRefreshLayout.setRefreshing(false);
               UIUtils.toast(FileListPagerFragment.this.getContext(), var1);
            }
         }

         public void onSuccess(JecFile[] var1) {
            if (FileListPagerFragment.this.adapter != null) {
               FileListPagerFragment.this.adapter.setData(var1);
            }

         }
      });
      this.task.execute(new Void[0]);
   }

   public void onSharedPreferenceChanged(SharedPreferences var1, String var2) {
      this.onRefresh();
   }

   public void onStop() {
      super.onStop();
      if (this.task != null) {
         this.task.cancel(true);
         this.task = null;
      }

   }

   public void onViewCreated(@NonNull View var1, @Nullable Bundle var2) {
      this.action = new FileExplorerAction(this.getContext(), this, ((FileExplorerActivity)this.getActivity()).getFileClipboard(), this);
      this.adapter = new FileListItemAdapter(this.getContext());
      this.adapter.setOnCheckedChangeListener(this.action);
      this.adapter.setOnItemClickListener(this);
      this.adapter.registerAdapterDataObserver(new AdapterDataObserver() {
         public void onChanged() {
            FileListPagerFragment.this.binding.emptyLayout.post(new Runnable() {
               public void run() {
                  AppCompatTextView var1 = FileListPagerFragment.this.binding.emptyLayout;
                  byte var2;
                  if (FileListPagerFragment.this.adapter.getItemCount() > 0) {
                     var2 = 8;
                  } else {
                     var2 = 0;
                  }

                  var1.setVisibility(var2);
               }
            });
         }
      });
      this.binding.pathScrollView.setLayoutManager(new LinearLayoutManager(this.getContext(), 0, false));
      this.pathAdapter = new PathButtonAdapter();
      this.pathAdapter.registerAdapterDataObserver(new AdapterDataObserver() {
         public void onChanged() {
            FileListPagerFragment.this.binding.pathScrollView.scrollToPosition(FileListPagerFragment.this.pathAdapter.getItemCount() - 1);
         }
      });
      this.pathAdapter.setPath(this.path);
      this.pathAdapter.setOnItemClickListener(new OnItemClickListener() {
         public void onItemClick(int var1, View var2) {
            JecFile var3 = FileListPagerFragment.this.pathAdapter.getItem(var1);
            FileListPagerFragment.this.switchToPath(var3);
         }
      });
      this.binding.pathScrollView.setAdapter(this.pathAdapter);
      this.binding.explorerSwipeRefreshLayout.setOnRefreshListener(this);
      this.binding.recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
      this.binding.recyclerView.setAdapter(this.adapter);
      this.binding.recyclerView.addItemDecoration((new Builder(this.getContext())).margin(this.getResources().getDimensionPixelSize(dimen.file_list_item_divider_left_margin), 0).build());
      this.binding.explorerSwipeRefreshLayout.post(new Runnable() {
         public void run() {
            FileListPagerFragment.this.binding.explorerSwipeRefreshLayout.setRefreshing(true);
         }
      });
      Preferences.getInstance(this.getContext()).registerOnSharedPreferenceChangeListener(this);
      var1.post(new Runnable() {
         public void run() {
            FileListPagerFragment.this.onRefresh();
         }
      });
   }

   public void refresh() {
      this.onRefresh();
   }

   public void setSelectAll(boolean var1) {
      this.adapter.checkAll(var1);
   }

   public ActionMode startActionMode(Callback var1) {
      return ((AppCompatActivity)this.getActivity()).startSupportActionMode(var1);
   }

   public void switchToPath(JecFile var1) {
      if (var1.canRead()) {
         this.path = var1;
         this.pathAdapter.setPath(var1);
         Preferences.getInstance(this.getContext()).setLastOpenPath(var1.getPath());
         this.onRefresh();
      } else {
         Toast.makeText(this.getContext(), string.cannot_read_folder, 0).show();
      }

   }

   private static class ScanFilesTask extends JecAsyncTask<Void, Void, JecFile[]> {
      private final Context context;
      private final boolean isRoot;
      private JecFile path;
      private final FileListPagerFragment.UpdateRootInfo updateRootInfo;

      private ScanFilesTask(Context var1, JecFile var2, FileListPagerFragment.UpdateRootInfo var3) {
         this.isRoot = false;
         this.context = var1.getApplicationContext();
         this.path = var2;
         this.updateRootInfo = var3;
      }

      // $FF: synthetic method
      ScanFilesTask(Context var1, JecFile var2, FileListPagerFragment.UpdateRootInfo var3, Object var4) {
         this(var1, var2, var3);
      }

      // $FF: synthetic method
      // $FF: bridge method
      protected void onRun(TaskResult var1, Object[] var2) throws Exception {
         this.onRun(var1, (Void[])var2);
      }

      protected void onRun(final TaskResult<JecFile[]> var1, Void... var2) throws Exception {
         Preferences var5 = Preferences.getInstance(this.context);
         final boolean var3 = var5.isShowHiddenFiles();
         final int var4 = var5.getFileSortType();
         this.updateRootInfo.onUpdate(this.path);
         this.path.listFiles(new FileListResultListener() {
            public void onResult(JecFile[] var1x) {
               if (var1x.length == 0) {
                  var1.setResult(var1x);
               } else {
                  JecFile[] var2 = var1x;
                  if (!var3) {
                     ArrayList var3x = new ArrayList(var1x.length);
                     int var4x = var1x.length;

                     for(int var5 = 0; var5 < var4x; ++var5) {
                        JecFile var6 = var1x[var5];
                        if (var6.getName().charAt(0) != '.') {
                           var3x.add(var6);
                        }
                     }

                     var2 = new JecFile[var3x.size()];
                     var3x.toArray(var2);
                  }

                  Arrays.sort(var2, new FileListSorter(true, var4, true));
                  var1.setResult(var2);
               }
            }
         });
      }
   }

   private interface UpdateRootInfo {
      void onUpdate(JecFile var1);
   }
}