package com.duy.common;

import android.support.annotation.NonNull;
import android.util.Log;
import java.io.PrintStream;

public class DLog {
    public static boolean ANDROID = true;
    public static final boolean DEBUG = false;
    private static final String TAG = "DLog";

    /* renamed from: d */
    public static void m0d(Object obj) {
        if (!DEBUG) {
            return;
        }
        if (ANDROID) {
            Log.d(TAG, obj.toString());
            return;
        }
        PrintStream printStream = System.out;
        printStream.println("DLog: " + obj.toString());
    }

    /* renamed from: d */
    public static void m1d(String str, Object obj) {
        if (!DEBUG) {
            return;
        }
        if (ANDROID) {
            Log.d(str, obj.toString());
            return;
        }
        PrintStream printStream = System.out;
        printStream.println(str + ": " + obj.toString());
    }

    /* renamed from: d */
    public static void m2d(String str, String str2, Throwable th) {
        if (!DEBUG) {
            return;
        }
        if (ANDROID) {
            Log.d(str, str2, th);
            return;
        }
        PrintStream printStream = System.out;
        printStream.println("DLog: " + str2);
    }

    /* renamed from: e */
    public static void m3e(@NonNull String str, @NonNull String str2) {
        if (!DEBUG) {
            return;
        }
        if (ANDROID) {
            Log.e(str, str2);
            return;
        }
        PrintStream printStream = System.err;
        printStream.println(str + ": " + str2);
    }

    /* renamed from: e */
    public static void m4e(@NonNull String str, @NonNull String str2, @NonNull Throwable th) {
        if (!DEBUG) {
            return;
        }
        if (ANDROID) {
            Log.e(str, str2, th);
            return;
        }
        PrintStream printStream = System.err;
        printStream.println(str + ": " + str2);
        th.printStackTrace();
    }

    /* renamed from: e */
    public static void m5e(String str, Throwable th) {
        if (!DEBUG) {
            return;
        }
        if (ANDROID) {
            Log.e(str, "Error ", th);
            return;
        }
        PrintStream printStream = System.err;
        printStream.println(str + ": " + th.getMessage());
        th.printStackTrace();
    }

    /* renamed from: e */
    public static void m6e(Throwable th) {
        if (!DEBUG) {
            return;
        }
        if (ANDROID) {
            Log.e(TAG, "Error ", th);
            return;
        }
        PrintStream printStream = System.err;
        printStream.println("DLog: " + th.getMessage());
        th.printStackTrace();
    }

    /* renamed from: i */
    public static void m7i(@NonNull Object obj) {
        if (!DEBUG) {
            return;
        }
        if (ANDROID) {
            Log.i(TAG, obj.toString());
            return;
        }
        PrintStream printStream = System.out;
        printStream.println("DLog: " + obj.toString());
    }

    /* renamed from: i */
    public static void m8i(String str, @NonNull Object obj) {
        if (!DEBUG) {
            return;
        }
        if (ANDROID) {
            Log.i(str, obj.toString());
            return;
        }
        PrintStream printStream = System.err;
        printStream.println(str + ": " + obj);
    }

    public static void reportException(@NonNull Throwable th) {
        boolean z = DEBUG;
    }

    @Deprecated
    public static void reportServer(Throwable th) {
        if (!ANDROID) {
            System.err.println("Fatal exception : ");
            th.printStackTrace();
        }
    }

    /* renamed from: w */
    public static void m9w(Object obj) {
        if (!DEBUG) {
            return;
        }
        if (ANDROID) {
            Log.w(TAG, obj.toString());
            return;
        }
        PrintStream printStream = System.out;
        printStream.println("DLog: " + obj.toString());
    }

    /* renamed from: w */
    public static void m10w(String str, Object obj) {
        if (!DEBUG) {
            return;
        }
        if (ANDROID) {
            Log.w(str, obj.toString());
            return;
        }
        PrintStream printStream = System.out;
        printStream.println(str + ": " + obj.toString());
    }

    /* renamed from: w */
    public static void m11w(String str, Object obj, Throwable th) {
        if (!DEBUG) {
            return;
        }
        if (ANDROID) {
            Log.w(str, obj.toString(), th);
            return;
        }
        PrintStream printStream = System.out;
        printStream.println(str + ": " + obj.toString());
        th.printStackTrace();
    }
}
