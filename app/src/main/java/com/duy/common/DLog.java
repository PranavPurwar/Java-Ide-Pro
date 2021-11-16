/* Decompiler 41ms, total 408ms, lines 216 */
package com.duy.common;

import android.support.annotation.NonNull;
import android.util.Log;
import java.io.PrintStream;

public class DLog {
   public static boolean ANDROID;
   public static final boolean DEBUG = false;
   private static final String TAG = "DLog";

   public static void d(Object var0) {
      if (DEBUG) {
         if (ANDROID) {
            Log.d("DLog", var0.toString());
         } else {
            PrintStream var1 = System.out;
            StringBuilder var2 = new StringBuilder();
            var2.append("DLog: ");
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

   public static void d(String var0, String var1, Throwable var2) {
      if (DEBUG) {
         if (ANDROID) {
            Log.d(var0, var1, var2);
         } else {
            PrintStream var4 = System.out;
            StringBuilder var3 = new StringBuilder();
            var3.append("DLog: ");
            var3.append(var1);
            var4.println(var3.toString());
         }
      }

   }

   public static void e(@NonNull String var0, @NonNull String var1) {
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

   public static void e(@NonNull String var0, @NonNull String var1, @NonNull Throwable var2) {
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

   public static void e(String var0, Throwable var1) {
      if (DEBUG) {
         if (ANDROID) {
            Log.e(var0, "Error ", var1);
         } else {
            PrintStream var2 = System.err;
            StringBuilder var3 = new StringBuilder();
            var3.append(var0);
            var3.append(": ");
            var3.append(var1.getMessage());
            var2.println(var3.toString());
            var1.printStackTrace();
         }
      }

   }

   public static void e(Throwable var0) {
      if (DEBUG) {
         if (ANDROID) {
            Log.e("DLog", "Error ", var0);
         } else {
            PrintStream var1 = System.err;
            StringBuilder var2 = new StringBuilder();
            var2.append("DLog: ");
            var2.append(var0.getMessage());
            var1.println(var2.toString());
            var0.printStackTrace();
         }
      }

   }

   public static void i(@NonNull Object var0) {
      if (DEBUG) {
         if (ANDROID) {
            Log.i("DLog", var0.toString());
         } else {
            PrintStream var1 = System.out;
            StringBuilder var2 = new StringBuilder();
            var2.append("DLog: ");
            var2.append(var0.toString());
            var1.println(var2.toString());
         }
      }

   }

   public static void i(String var0, @NonNull Object var1) {
      if (DEBUG) {
         if (ANDROID) {
            Log.i(var0, var1.toString());
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

   public static void reportException(@NonNull Throwable var0) {
      boolean var1 = DEBUG;
   }

   @Deprecated
   public static void reportServer(Throwable var0) {
      if (!ANDROID) {
         System.err.println("Fatal exception : ");
         var0.printStackTrace();
      }

   }

   public static void w(Object var0) {
      if (DEBUG) {
         if (ANDROID) {
            Log.w("DLog", var0.toString());
         } else {
            PrintStream var1 = System.out;
            StringBuilder var2 = new StringBuilder();
            var2.append("DLog: ");
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

   public static void w(String var0, Object var1, Throwable var2) {
      if (DEBUG) {
         if (ANDROID) {
            Log.w(var0, var1.toString(), var2);
         } else {
            PrintStream var3 = System.out;
            StringBuilder var4 = new StringBuilder();
            var4.append(var0);
            var4.append(": ");
            var4.append(var1.toString());
            var3.println(var4.toString());
            var2.printStackTrace();
         }
      }

   }
}