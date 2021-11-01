package com.duy.ide.javaide;

import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import com.duy.ide.javaide.setting.IdePreferenceManager;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;

public class JavaApplication extends MultiDexApplication {
   private ArrayList<PrintStream> err = new ArrayList();
   private ArrayList<PrintStream> out = new ArrayList();
   private JavaApplication.InterceptorOutputStream systemErr;
   private JavaApplication.InterceptorOutputStream systemOut;

   public void addStdErr(PrintStream err) {
      this.systemErr.add(err);
   }

   public void addStdOut(PrintStream out) {
      this.systemOut.add(out);
   }

   @Override
   public void onCreate() {
      super.onCreate();
      this.systemOut = new JavaApplication.InterceptorOutputStream(System.out, this.out);
      this.systemErr = new JavaApplication.InterceptorOutputStream(System.err, this.err);
      System.setOut(this.systemOut);
      System.setErr(this.systemErr);
      AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
      IdePreferenceManager.setDefaultValues(this);
   }

   public void removeErrStream(PrintStream err) {
      this.systemErr.remove(err);
   }

   public void removeOutStream(PrintStream out) {
      this.systemOut.remove(out);
   }

   private static class InterceptorOutputStream extends PrintStream {
      private static final String TAG = "InterceptorOutputStream";
      private ArrayList<PrintStream> streams;

      public InterceptorOutputStream(@NonNull OutputStream file, ArrayList<PrintStream> streams) {
         super(file, true);
         this.streams = streams;
      }

      public void add(PrintStream out) {
         Log.d(TAG, "add() called with: out = [" + out + "]");
         this.streams.add(out);
      }

      public ArrayList<PrintStream> getStreams() {
         return this.streams;
      }

      public void remove(PrintStream out) {
         Log.d(TAG, "remove() called with: out = [" + out + "]");
         this.streams.remove(out);
      }

      public void setStreams(ArrayList<PrintStream> streams) {
         this.streams = streams;
      }

      @Override
        public void write(@NonNull byte[] buf, int off, int len) {
            super.write(buf, off, len);
            if (streams != null) {
                for (PrintStream printStream : streams) {
                    printStream.write(buf, off, len);
                }
            }
        }
   }
}