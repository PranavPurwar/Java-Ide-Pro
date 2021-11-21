package com.duy.ide.diagnostic;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import com.duy.ide.diagnostic.model.Message;
import com.duy.ide.diagnostic.parser.PatternAwareOutputParser;
import com.duy.ide.logging.ILogger;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DiagnosticContract {
   public static final int COMPILER_LOG = 0;
   public static final int DIAGNOSTIC = 1;

   public interface MessageFilter {
      boolean accept(Message message);
   }

   public interface Presenter extends ILogger {
      @MainThread
      void clear();

      @NonNull
      OutputStream getErrorOutput();

      @NonNull
      OutputStream getStandardOutput();

      @MainThread
      void hidePanel();

      @MainThread
      void onDiagnosticClick(android.view.View view, Message message);

      @WorkerThread
      void onNewMessage(String message);

      @MainThread
      void setCurrentItem(int pos);

      void setFilter(@NonNull DiagnosticContract.MessageFilter filter);

      @MainThread
      void setMessages(ArrayList<Message> messages);

      void setOutputParser(@NonNull PatternAwareOutputParser... outputParser);

      @MainThread
      void showPanel();
   }

   public interface View {
      @WorkerThread
      void addMessage(Message message);

      @WorkerThread
      void addMessage(List<Message> messages);

      @WorkerThread
      void clearAll();

      @WorkerThread
      void printError(String err);

      @WorkerThread
      void printMessage(String message);

      @WorkerThread
      void removeMessage(Message message);

      @MainThread
      void setCurrentItem(int pos);

      void setPresenter(DiagnosticContract.Presenter presenter);

      @WorkerThread
      void showDiagnostic(List<Message> messages);
   }
}