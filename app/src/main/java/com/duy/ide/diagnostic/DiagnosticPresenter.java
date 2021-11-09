package com.duy.ide.diagnostic;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.support.v4.util.Pair;
import com.duy.common.DLog;
import com.duy.ide.core.api.IdeActivity;
import com.duy.ide.diagnostic.DiagnosticContract.MessageFilter;
import com.duy.ide.diagnostic.DiagnosticContract.Presenter;
import com.duy.ide.diagnostic.DiagnosticContract.View;
import com.duy.ide.diagnostic.model.Message;
import com.duy.ide.diagnostic.model.Message.Kind;
import com.duy.ide.diagnostic.parser.PatternAwareOutputParser;
import com.duy.ide.diagnostic.parser.ToolOutputParser;
import com.duy.ide.editor.IEditorDelegate;
import com.duy.ide.editor.editor.R.string;
import com.jecelyin.common.utils.UIUtils;
import com.jecelyin.editor.v2.common.Command;
import com.jecelyin.editor.v2.common.Command.CommandEnum;
import com.jecelyin.editor.v2.manager.TabManager;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DiagnosticPresenter implements Presenter {
   private static final String TAG = "DiagnosticPresenter";
   private final IdeActivity mActivity;
   private final Handler mHandler;
   private MessageFilter mMessageFilter = new MessageFilter() {
      public boolean accept(Message message) {
         return true;
      }
   };
   private OutputStream mStdErr;
   private OutputStream mStdOut;
   private final TabManager mTabManager;
   @Nullable
   private ToolOutputParser mToolOutputParser = null;
   private View mView;

   public DiagnosticPresenter(@Nullable View view, @NonNull IdeActivity activity, @NonNull TabManager manager, @NonNull Handler handler) {
      this.mActivity = activity;
      this.mTabManager = manager;
      this.mView = view;
      if (this.mView != null) {
         this.mView.setPresenter(this);
      }

      this.mHandler = handler;
      this.createIOStream();
   }

   private void createIOStream() {
      this.mStdOut = new OutputStream() {
         public void write(int buffer) throws IOException {
            this.write(new byte[]{(byte)buffer}, 0, 1);
         }

         public void write(@NonNull byte[] byteBuff, int var2, int var3) throws IOException {
            DiagnosticPresenter.this.onNewMessage(new String(byteBuff, var2, var3));
         }
      };
      this.mStdErr = new OutputStream() {
         public void write(int buff) throws IOException {
            this.write(new byte[]{(byte)buff}, 0, 1);
         }

         public void write(@NonNull byte[] byteArr, int var2, int var3) throws IOException {
            DiagnosticPresenter.this.onNewMessage(new String(byteArr, var2, var3));
         }
      };
   }

   private List<Message> filter(List<Message> messages) {
      ArrayList<Message> filteredList = new ArrayList<Message>();
      Iterator it = messages.iterator();

      while(it.hasNext()) {
         Message message = (Message)it.next();
         if (this.mMessageFilter.accept(message)) {
            filteredList.add(message);
         }
      }

      return filteredList;
   }

   @WorkerThread
   private void highlightError(final List<Message> messages) {
      this.mHandler.post(new Runnable() {
         public void run() {
            Iterator it = messages.iterator();

            while(it.hasNext()) {
               Message message = (Message)it.next();
               if (message.getKind() == Kind.ERROR && message.getSourcePath() != null) {
                  File path = new File(message.getSourcePath());
                  Pair pair = DiagnosticPresenter.this.mTabManager.getEditorDelegate(path);
                  if (pair != null) {
                     IEditorDelegate delegate = (IEditorDelegate)pair.second;
                     Command command = new Command(CommandEnum.HIGHLIGHT_ERROR);
                     int line = message.getLineNumber();
                     int column = message.getColumn();
                     command.args.putInt("line", line);
                     command.args.putInt("col", column);
                     delegate.doCommand(command);
                  }
               }
            }

         }
      });
   }

   private boolean isSystemFile(File file) {
      try {
         File filesDir = this.mActivity.getFilesDir().getParentFile();
         return file.getCanonicalPath().startsWith(filesDir.getCanonicalPath());
      } catch (IOException e) {
         e.printStackTrace();
         return false;
      }
   }

   @MainThread
   @Nullable
   private IEditorDelegate moveToEditor(File file) {
      Pair pair = this.mTabManager.getEditorDelegate(file);
      if (pair != null) {
         int pos = (Integer)pair.first;
         IEditorDelegate delegate = (IEditorDelegate)pair.second;
         this.mTabManager.setCurrentTab(pos);
         delegate.doCommand(new Command(CommandEnum.REQUEST_FOCUS));
         return delegate;
      } else {
         return this.mTabManager.newTab(file) ? this.moveToEditor(file) : null;
      }
   }

   private void show(List<Message> messages) {
      if (this.mView != null) {
         this.mView.showDiagnostic(messages);
      }

   }

   @SuppressLint({"WrongThread"})
   public void clear() {
      this.mView.clearAll();
   }

   public void error(@Nullable Throwable th, @Nullable String tag, Object... message) {
   }

   @NonNull
   public OutputStream getErrorOutput() {
      return this.mStdErr;
   }

   @NonNull
   public OutputStream getStandardOutput() {
      return this.mStdOut;
   }

   public void hidePanel() {
      this.mActivity.mSlidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
   }

   public void info(@NonNull String tag, Object... exception) {
   }

   @MainThread
   public void onDiagnosticClick(android.view.View view, Message message) {
      if (DLog.DEBUG) {
         DLog.d(TAG, "onDiagnosticClick() called diagnostic = [" + message + "]");
      }

      if (message.getSourcePath() != null) {
         File file = new File(message.getSourcePath());
         if (this.isSystemFile(file)) {
            UIUtils.alert(this.mActivity, this.mActivity.getString(string.title_non_project_file), this.mActivity.getString(string.message_non_project_file, new Object[]{file.getPath()}));
            return;
         }

         IEditorDelegate delegate = this.moveToEditor(file);
         if (delegate != null) {
            Command command = new Command(CommandEnum.GOTO_INDEX);
            command.args.putInt("line", message.getLineNumber());
            command.args.putInt("col", message.getColumn());
            delegate.doCommand(command);
         }

         this.hidePanel();
      }

   }

   public void onNewMessage(String message) {
      if (this.mView != null) {
         this.mView.printMessage(message);
      }

      if (this.mToolOutputParser != null) {
         List list = this.filter(this.mToolOutputParser.parseToolOutput(message));
         this.mView.addMessage(list);
         this.highlightError(list);
      }
   }

   public void setCurrentItem(int pos) {
      if (this.mView != null) {
         this.mView.setCurrentItem(pos);
      }

   }

   public void setFilter(@NonNull MessageFilter filter) {
      this.mMessageFilter = filter;
   }

   @SuppressLint({"WrongThread"})
   public void setMessages(ArrayList<Message> messages) {
      this.show(this.filter(messages));
      this.highlightError(messages);
   }

   public void setOutputParser(@NonNull PatternAwareOutputParser... parser) {
      this.mToolOutputParser = new ToolOutputParser(parser, this);
   }

   public void showPanel() {
      this.mActivity.mSlidingUpPanelLayout.setPanelState(PanelState.EXPANDED);
   }

   public void verbose(@NonNull String tag, Object... obj) {
   }

   public void warning(@NonNull String tag, Object... obj) {
   }
}