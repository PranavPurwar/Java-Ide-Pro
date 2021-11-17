package com.duy.common;

import android.support.annotation.NonNull;
import android.util.Log;
import java.io.PrintStream;

public class DLog {
    public static boolean ANDROID = true;
    public static final boolean DEBUG = false;
    private static final String TAG = "DLog";

    public static void d(Object obj) {
        if (!DEBUG) {
            return;
        }
        if (ANDROID) {
            Log.d(TAG, obj.toString());
            return;
        }
        PrintStream printStream = System.out;
        printStream.println(TAG + ": " + obj.toString());
    }

    public static void d(String str, Object obj) {
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

    public static void d(String str, String str2, Throwable th) {
        if (!DEBUG) {
            return;
        }
        if (ANDROID) {
            Log.d(str, str2, th);
            return;
        }
        PrintStream printStream = System.out;
        printStream.println(TAG + ": " + str2);
    }

    public static void e(@NonNull String str, @NonNull String str2) {
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

    public static void e(@NonNull String str, @NonNull String str2, @NonNull Throwable th) {
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

    public static void e(String str, Throwable th) {
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

    public static void e(Throwable th) {
        if (!DEBUG) {
            return;
        }
        if (ANDROID) {
            Log.e(TAG, "Error ", th);
            return;
        }
        PrintStream printStream = System.err;
        printStream.println(TAG + ": " + th.getMessage());
        th.printStackTrace();
    }

    public static void i(@NonNull Object obj) {
        if (!DEBUG) {
            return;
        }
        if (ANDROID) {
            Log.i(TAG, obj.toString());
            return;
        }
        PrintStream printStream = System.out;
        printStream.println(TAG + ": " + obj.toString());
    }

    public static void i(String str, @NonNull Object obj) {
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
        // currently empty
    }

    @Deprecated
    public static void reportServer(Throwable th) {
        if (!ANDROID) {
            System.err.println("Fatal exception : ");
            th.printStackTrace();
        }
    }

    public static void w(Object obj) {
        if (!DEBUG) {
            return;
        }
        if (ANDROID) {
            Log.w(TAG, obj.toString());
            return;
        }
        PrintStream printStream = System.out;
        printStream.println(TAG + ": " + obj.toString());
    }

    public static void w(String str, Object obj) {
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

    public static void w(String str, Object obj, Throwable th) {
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
