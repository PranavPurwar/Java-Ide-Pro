/* Decompiler 11ms, total 353ms, lines 133 */
package com.duy.file.explorer;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import com.duy.file.explorer.io.JecFile;
import com.duy.file.explorer.listener.OnClipboardDataChangedListener;
import com.duy.file.explorer.listener.OnClipboardPasteFinishListener;
import com.duy.file.explorer.util.FileUtils;
import com.duy.ide.editor.editor.R.string;
import com.jecelyin.common.task.JecAsyncTask;
import com.jecelyin.common.task.TaskResult;
import com.jecelyin.common.utils.UIUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FileClipboard {
   private List<JecFile> clipList = new ArrayList();
   private boolean isCopy;
   private OnClipboardDataChangedListener onClipboardDataChangedListener;

   public boolean canPaste() {
      return this.clipList.isEmpty() ^ true;
   }

   public void paste(Context var1, JecFile var2, OnClipboardPasteFinishListener var3) {
      if (this.canPaste()) {
         ProgressDialog var4 = new ProgressDialog(var1);
         FileClipboard.PasteTask var5 = new FileClipboard.PasteTask(var3);
         var5.setProgress(var4);
         var5.execute(new JecFile[]{var2});
      }
   }

   public void setData(boolean var1, List<JecFile> var2) {
      this.isCopy = var1;
      this.clipList.clear();
      this.clipList.addAll(var2);
      if (this.onClipboardDataChangedListener != null) {
         this.onClipboardDataChangedListener.onClipboardDataChanged();
      }

   }

   public void setOnClipboardDataChangedListener(OnClipboardDataChangedListener var1) {
      this.onClipboardDataChangedListener = var1;
   }

   public void showPasteResult(Context var1, int var2, String var3) {
      if (TextUtils.isEmpty(var3)) {
         UIUtils.toast(var1, string.x_items_completed, new Object[]{var2});
      } else {
         UIUtils.toast(var1, string.x_items_completed_and_error_x, new Object[]{var2, var3});
      }

   }

   private class PasteTask extends JecAsyncTask<JecFile, JecFile, Integer> {
      private StringBuilder errorMsg = new StringBuilder();
      private final OnClipboardPasteFinishListener listener;

      public PasteTask(OnClipboardPasteFinishListener var2) {
         this.listener = var2;
      }

      protected void onError(Exception var1) {
         if (this.listener != null) {
            this.listener.onFinish(0, var1.getMessage());
         }

      }

      protected void onProgressUpdate(JecFile... var1) {
         this.getProgress().setMessage(var1[0].getPath());
      }

      protected void onRun(TaskResult<Integer> var1, JecFile... var2) throws Exception {
         JecFile var3 = var2[0];
         Iterator var8 = FileClipboard.this.clipList.iterator();
         int var4 = 0;

         while(var8.hasNext()) {
            JecFile var5 = (JecFile)var8.next();
            this.publishProgress(new JecFile[]{var5});

            try {
               if (var5.isDirectory()) {
                  FileUtils.copyDirectory(var5, var3, true ^ FileClipboard.this.isCopy);
               } else {
                  FileUtils.copyFile(var5, var3.newFile(var5.getName()), true ^ FileClipboard.this.isCopy);
               }
            } catch (Exception var7) {
               StringBuilder var9 = this.errorMsg;
               var9.append(var7.getMessage());
               var9.append("\n");
               continue;
            }

            ++var4;
         }

         FileClipboard.this.clipList.clear();
         var1.setResult(var4);
      }

      protected void onSuccess(Integer var1) {
         if (this.listener != null) {
            this.listener.onFinish(var1, this.errorMsg.toString());
         }

      }
   }
}