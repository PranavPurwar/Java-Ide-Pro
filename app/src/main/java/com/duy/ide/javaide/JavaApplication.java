/* Decompiler 11ms, total 1465ms, lines 93 */
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

   public void addStdErr(PrintStream var1) {
      this.systemErr.add(var1);
   }

   public void addStdOut(PrintStream var1) {
      this.systemOut.add(var1);
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

   public void removeErrStream(PrintStream var1) {
      this.systemErr.remove(var1);
   }

   public void removeOutStream(PrintStream var1) {
      this.systemOut.remove(var1);
   }

   private static class InterceptorOutputStream extends PrintStream {
      private static final String TAG = "InterceptorOutputStream";
      private ArrayList<PrintStream> streams;

      public InterceptorOutputStream(@NonNull OutputStream var1, ArrayList<PrintStream> var2) {
         super(var1, true);
         this.streams = var2;
      }

      public void add(PrintStream var1) {
         StringBuilder var2 = new StringBuilder();
         var2.append("add() called with: out = [");
         var2.append(var1);
         var2.append("]");
         Log.d("InterceptorOutputStream", var2.toString());
         this.streams.add(var1);
      }

      public ArrayList<PrintStream> getStreams() {
         return this.streams;
      }

      public void remove(PrintStream var1) {
         StringBuilder var2 = new StringBuilder();
         var2.append("remove() called with: out = [");
         var2.append(var1);
         var2.append("]");
         Log.d("InterceptorOutputStream", var2.toString());
         this.streams.remove(var1);
      }

      public void setStreams(ArrayList<PrintStream> var1) {
         this.streams = var1;
      }

      public void write(@NonNull byte[] var1, int var2, int var3) {
         super.write(var1, var2, var3);
         if (this.streams != null) {
            Iterator var4 = this.streams.iterator();

            while(var4.hasNext()) {
               ((PrintStream)var4.next()).write(var1, var2, var3);
            }
         }

      }
   }
}