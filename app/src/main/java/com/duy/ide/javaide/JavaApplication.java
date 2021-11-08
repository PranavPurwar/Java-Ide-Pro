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

   public void addStdErr(PrintStream stdErr) {
      this.systemErr.add(stdErr);
   }

   public void addStdOut(PrintStream stdOut) {
      this.systemOut.add(stdOut);
   }

   public void onCreate() {
      super.onCreate();
      this.systemOut = new JavaApplication.InterceptorOutputStream(System.out, this.out);
      this.systemErr = new JavaApplication.InterceptorOutputStream(System.err, this.err);
      System.setOut(this.systemOut);
      System.setErr(this.systemErr);
      AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
      IdePreferenceManager.setDefaultValues(this);
   }

   public void removeErrStream(PrintStream stdErr) {
      this.systemErr.remove(stdErr);
   }

   public void removeOutStream(PrintStream stdOut) {
      this.systemOut.remove(stdOut);
   }

   private static class InterceptorOutputStream extends PrintStream {
      private static final String TAG = "InterceptorOutputStream";
      private ArrayList<PrintStream> streams;

      public InterceptorOutputStream(@NonNull OutputStream file, ArrayList<PrintStream> streams) {
         super(file, true);
         this.streams = streams;
      }

      public void add(PrintStream out) {
         Log.d("InterceptorOutputStream", "add() called with: out = [" + out + "]");
         this.streams.add(out);
      }

      public ArrayList<PrintStream> getStreams() {
         return this.streams;
      }

      public void remove(PrintStream out) {
         Log.d("InterceptorOutputStream", "remove() called with: out = [" + out + "]");
         this.streams.remove(out);
      }

      public void setStreams(ArrayList<PrintStream> streams) {
         this.streams = streams;
      }

      public void write(@NonNull byte[] buffer, int off, int len) {
         super.write(buffer, off, len);
         if (this.streams != null) {
            Iterator it = this.streams.iterator();

            while(it.hasNext()) {
               ((PrintStream)it.next()).write(buffer, off, len);
            }
         }

      }
   }
}