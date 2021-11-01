/* Decompiler 11ms, total 425ms, lines 161 */
package com.duy.ide.javaide.utils;

import android.util.Log;
import com.crashlytics.android.Crashlytics;
import java.io.PrintStream;

public class DLog {
   public static boolean ANDROID;
   public static boolean DEBUG;
   public static String TAG;

   public static void d(Object var0) {
      if (DEBUG) {
         if (ANDROID) {
            Log.d(TAG, var0.toString());
         } else {
            PrintStream var1 = System.out;
            StringBuilder var2 = new StringBuilder();
            var2.append(TAG);
            var2.append(": ");
            var2.append(var0.toString());
            var1.println(var2.toString());
         }
      }

   }

   public static void d(String var0, Object var1) {
      if (DEBUG) {
         if (ANDROID) {
            Log.d(var0, var1.toString());
         } else {
            PrintStream var2 = System.out;
            StringBuilder var3 = new StringBuilder();
            var3.append(var0);
            var3.append(": ");
            var3.append(var1.toString());
            var2.println(var3.toString());
         }
      }

   }

   public static void e(Exception var0) {
      if (DEBUG) {
         if (ANDROID) {
            Log.e(TAG, "Error ", var0);
         } else {
            PrintStream var1 = System.err;
            StringBuilder var2 = new StringBuilder();
            var2.append(TAG);
            var2.append(": ");
            var2.append(var0.toString());
            var1.println(var2.toString());
         }
      }

   }

   public static void e(String var0, Exception var1) {
      if (DEBUG) {
         if (ANDROID) {
            Log.e(var0, "Error ", var1);
         } else {
            PrintStream var2 = System.err;
            StringBuilder var3 = new StringBuilder();
            var3.append(var0);
            var3.append(": ");
            var3.append(var1.toString());
            var2.println(var3.toString());
         }
      }

   }

   public static void e(String var0, String var1) {
      if (DEBUG) {
         if (ANDROID) {
            Log.e(var0, var1);
         } else {
            PrintStream var2 = System.err;
            StringBuilder var3 = new StringBuilder();
            var3.append(var0);
            var3.append(": ");
            var3.append(var1);
            var2.println(var3.toString());
         }
      }

   }

   public static void e(String var0, String var1, Exception var2) {
      if (DEBUG) {
         if (ANDROID) {
            Log.e(var0, var1, var2);
         } else {
            PrintStream var3 = System.err;
            StringBuilder var4 = new StringBuilder();
            var4.append(var0);
            var4.append(": ");
            var4.append(var1);
            var3.println(var4.toString());
            var2.printStackTrace();
         }
      }

   }

   public static void i(Object var0) {
      if (DEBUG) {
         if (ANDROID) {
            Log.i(TAG, var0.toString());
         } else {
            PrintStream var1 = System.out;
            StringBuilder var2 = new StringBuilder();
            var2.append(TAG);
            var2.append(": ");
            var2.append(var0.toString());
            var1.println(var2.toString());
         }
      }

   }

   public static void reportException(Throwable var0) {
      Crashlytics.logException(var0);
   }

   public static void w(Object var0) {
      if (DEBUG) {
         if (ANDROID) {
            Log.w(TAG, var0.toString());
         } else {
            PrintStream var1 = System.out;
            StringBuilder var2 = new StringBuilder();
            var2.append(TAG);
            var2.append(": ");
            var2.append(var0.toString());
            var1.println(var2.toString());
         }
      }

   }

   public static void w(String var0, Object var1) {
      if (DEBUG) {
         if (ANDROID) {
            Log.w(var0, var1.toString());
         } else {
            PrintStream var2 = System.out;
            StringBuilder var3 = new StringBuilder();
            var3.append(var0);
            var3.append(": ");
            var3.append(var1.toString());
            var2.println(var3.toString());
         }
      }

   }
}