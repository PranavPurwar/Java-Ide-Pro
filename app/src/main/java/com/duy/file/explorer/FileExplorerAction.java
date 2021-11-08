/* Decompiler 54ms, total 1965ms, lines 272 */
package com.duy.file.explorer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.view.ActionMode;
import android.support.v7.view.ActionMode.Callback;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.ShareActionProvider.OnShareTargetSelectedListener;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import com.duy.file.explorer.io.JecFile;
import com.duy.file.explorer.io.LocalFile;
import com.duy.file.explorer.listener.BoolResultListener;
import com.duy.file.explorer.listener.OnClipboardPasteFinishListener;
import com.duy.file.explorer.util.MimeTypes;
import com.duy.file.explorer.util.OnCheckedChangeListener;
import com.duy.ide.editor.editor.R.id;
import com.duy.ide.editor.editor.R.string;
import com.jecelyin.common.utils.UIUtils;
import com.jecelyin.common.utils.UIUtils.OnShowInputCallback;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FileExplorerAction implements OnCheckedChangeListener, Callback, OnShareTargetSelectedListener {
   private ActionMode actionMode;
   private List<JecFile> checkedList = new ArrayList();
   private final Context context;
   private final ExplorerContext explorerContext;
   private final FileClipboard fileClipboard;
   private MenuItem renameMenu;
   private ShareActionProvider shareActionProvider;
   private MenuItem shareMenu;
   private final FileExplorerView view;

   public FileExplorerAction(Context var1, FileExplorerView var2, FileClipboard var3, ExplorerContext var4) {
      this.view = var2;
      this.context = var1;
      this.fileClipboard = var3;
      this.explorerContext = var4;
   }

   private boolean canShare() {
      Iterator var1 = this.checkedList.iterator();

      JecFile var2;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         var2 = (JecFile)var1.next();
      } while(var2 instanceof LocalFile && var2.isFile());

      return false;
   }

   private void destroyActionMode() {
      if (this.actionMode != null) {
         this.actionMode.finish();
         this.actionMode = null;
      }

   }

   private void doDeleteAction() {
      Iterator var1 = this.checkedList.iterator();

      while(var1.hasNext()) {
         ((JecFile)var1.next()).delete((BoolResultListener)null);
      }

      this.view.refresh();
      this.destroyActionMode();
   }

   private void doRenameAction() {
      if (this.checkedList.size() == 1) {
         final JecFile var1 = (JecFile)this.checkedList.get(0);
         UIUtils.showInputDialog(this.context, string.rename, 0, var1.getName(), 0, new OnShowInputCallback() {
            public void onConfirm(CharSequence var1x) {
               if (!TextUtils.isEmpty(var1x)) {
                  if (var1.getName().equals(var1x)) {
                     FileExplorerAction.this.destroyActionMode();
                  } else {
                     var1.renameTo(var1.getParentFile().newFile(var1x.toString()), new BoolResultListener() {
                        public void onResult(boolean var1x) {
                           if (!var1x) {
                              UIUtils.toast(FileExplorerAction.this.context, string.rename_fail);
                           } else {
                              FileExplorerAction.this.view.refresh();
                              FileExplorerAction.this.destroyActionMode();
                           }
                        }
                     });
                  }
               }
            }
         });
      }
   }

   private void shareFile() {
      if (!this.checkedList.isEmpty() && this.shareActionProvider != null) {
         Intent var1 = new Intent();
         if (this.checkedList.size() == 1) {
            File var2 = new File(((JecFile)this.checkedList.get(0)).getPath());
            var1.setAction("android.intent.action.SEND");
            var1.setType(MimeTypes.getInstance().getMimeType(var2.getPath()));
            var1.putExtra("android.intent.extra.STREAM", Uri.fromFile(var2));
         } else {
            var1.setAction("android.intent.action.SEND_MULTIPLE");
            ArrayList var3 = new ArrayList();
            Iterator var4 = this.checkedList.iterator();

            while(var4.hasNext()) {
               JecFile var7 = (JecFile)var4.next();
               if (!(var7 instanceof LocalFile)) {
                  Context var6 = this.context;
                  int var5 = string.can_not_share_x;
                  StringBuilder var8 = new StringBuilder();
                  var8.append(var7);
                  var8.append(" isn't LocalFile");
                  throw new ExplorerException(var6.getString(var5, new Object[]{var8.toString()}));
               }

               var3.add(Uri.fromFile(new File(var7.getPath())));
            }

            var1.putParcelableArrayListExtra("android.intent.extra.STREAM", var3);
         }

         this.shareActionProvider.setShareIntent(var1);
      }
   }

   public void destroy() {
      this.destroyActionMode();
   }

   public void doCreateFolder() {
      UIUtils.showInputDialog(this.context, string.create_folder, 0, (CharSequence)null, 0, new OnShowInputCallback() {
         public void onConfirm(CharSequence var1) {
            if (!TextUtils.isEmpty(var1)) {
               FileExplorerAction.this.explorerContext.getCurrentDirectory().newFile(var1.toString()).mkdirs(new BoolResultListener() {
                  public void onResult(boolean var1) {
                     if (!var1) {
                        UIUtils.toast(FileExplorerAction.this.context, string.can_not_create_folder);
                     } else {
                        FileExplorerAction.this.view.refresh();
                        FileExplorerAction.this.destroyActionMode();
                     }
                  }
               });
            }
         }
      });
   }

   public boolean onActionItemClicked(ActionMode var1, MenuItem var2) {
      int var3 = var2.getItemId();
      if (var3 == id.select_all) {
         if (!var2.isChecked()) {
            this.view.setSelectAll(true);
            var2.setChecked(true);
            var2.setTitle(string.cancel_select_all);
         } else {
            this.view.setSelectAll(false);
         }
      } else if (var3 == id.copy && !this.checkedList.isEmpty()) {
         this.fileClipboard.setData(true, this.checkedList);
         this.destroyActionMode();
      } else if (var3 == id.cut && !this.checkedList.isEmpty()) {
         this.fileClipboard.setData(false, this.checkedList);
         this.destroyActionMode();
      } else if (var3 == id.paste) {
         this.destroyActionMode();
         this.fileClipboard.paste(this.context, this.explorerContext.getCurrentDirectory(), new OnClipboardPasteFinishListener() {
            public void onFinish(int var1, String var2) {
               FileExplorerAction.this.fileClipboard.showPasteResult(FileExplorerAction.this.context, var1, var2);
            }
         });
      } else if (var3 == id.rename) {
         this.doRenameAction();
      } else if (var3 == id.share) {
         this.shareFile();
      } else {
         if (var3 != id.delete) {
            return false;
         }

         this.doDeleteAction();
      }

      return true;
   }

   public void onCheckedChanged(int var1) {
      if (var1 > 0) {
         if (this.actionMode == null) {
            this.actionMode = this.view.startActionMode(this);
         }

         this.actionMode.setTitle(this.context.getString(string.selected_x_items, new Object[]{var1}));
      } else if (this.actionMode != null) {
         this.actionMode.finish();
         this.actionMode = null;
      }

   }

   public void onCheckedChanged(JecFile var1, int var2, boolean var3) {
      if (var3) {
         this.checkedList.add(var1);
      } else {
         this.checkedList.remove(var1);
      }

   }

   public boolean onCreateActionMode(ActionMode var1, Menu var2) {
      var2.add(0, id.select_all, 0, string.select_all).setShowAsAction(2);
      var2.add(0, id.cut, 0, string.cut).setShowAsAction(0);
      var2.add(0, id.copy, 0, string.copy).setShowAsAction(0);
      MenuItem var3 = var2.add(0, id.paste, 0, string.paste);
      var3.setShowAsAction(0);
      var3.setEnabled(this.fileClipboard.canPaste());
      this.renameMenu = var2.add(0, id.rename, 0, string.rename);
      this.renameMenu.setShowAsAction(0);
      this.shareMenu = var2.add(0, id.share, 0, string.share);
      this.shareMenu.setShowAsAction(0);
      this.shareActionProvider = new ShareActionProvider(this.context);
      this.shareActionProvider.setOnShareTargetSelectedListener(this);
      MenuItemCompat.setActionProvider(this.shareMenu, this.shareActionProvider);
      var2.add(0, id.delete, 0, string.delete).setShowAsAction(0);
      return true;
   }

   public void onDestroyActionMode(ActionMode var1) {
      this.shareActionProvider.setOnShareTargetSelectedListener((OnShareTargetSelectedListener)null);
      this.shareActionProvider = null;
      this.checkedList.clear();
      this.view.setSelectAll(false);
      this.renameMenu = null;
      this.shareMenu = null;
      this.actionMode = null;
   }

   public boolean onPrepareActionMode(ActionMode var1, Menu var2) {
      this.shareMenu.setEnabled(this.canShare());
      MenuItem var4 = this.renameMenu;
      boolean var3;
      if (this.checkedList.size() == 1) {
         var3 = true;
      } else {
         var3 = false;
      }

      var4.setEnabled(var3);
      return true;
   }

   public boolean onShareTargetSelected(ShareActionProvider var1, Intent var2) {
      this.destroyActionMode();
      return false;
   }
}