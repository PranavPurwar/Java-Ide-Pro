package com.duy.android.compiler.utils;

import android.util.Log;


public class DLog {
    public static String TAG = DLog.class.getSimpleName();
    public static boolean DEBUG = true;
    public static boolean ANDROID = true;

/*
 * Log some debug information
 * @param msg the message as an object to log, it can be a string, charsequence, exception, error...
 */
    public static void d(Object msg) {
        if (DEBUG) {
            if (ANDROID) {
                Log.d(TAG, msg.toString());
            } else {
                System.out.println(TAG + ": " + msg.toString());
            }
        }
    }

/*
 * Same as previous but has support with tag
 * @param TAG teh tag to use for debugging
 * @param msg the message as an object
 */
    public static void d(String TAG, Object msg) {
        if (DEBUG) {
            if (ANDROID) {
                Log.d(TAG, msg.toString());
            } else {
                System.out.println(TAG + ": " + msg.toString());
            }
        }
    }
/*
 * Log some warning
 * @param msg the message as an object to log, it can be a string, charsequence, exception, error, etc.
 */
    public static void w(Object msg) {
        if (DEBUG) {
            if (ANDROID) {
                Log.w(TAG, msg.toString());
            } else {
                System.out.println(TAG + ": " + msg.toString());
            }
        }
    }

/*
 * Log some warning
 * @param msg the message as an object to log, it can be a string, charsequence, exception, error, etc.
 * @param TAG the tag to use for debugging
 */
    public static void w(String TAG, Object msg) {
        if (DEBUG) {
            if (ANDROID) {
                Log.w(TAG, msg.toString());
            } else {
                System.out.println(TAG + ": " + msg.toString());
            }
        }
    }

/*
 * Log some exception that occured at runtime
 * @param exception the exception caused
 */
    public static void e(Exception exception) {
        if (DEBUG) {
            if (ANDROID) {
                Log.e(TAG, "Error ", exception);
            } else {
                System.err.println(TAG + ": " + exception.toString());
            }
        }
    }

/*
 * Log some error with tag and information about the exception
 * @param TAG the tag to use for debugging
 * @param exception the exception caused
 */
    public static void e(String TAG, Exception exception) {
        if (DEBUG) {
            if (ANDROID) {
                Log.e(TAG, "Error ", exception);
            } else {
                System.err.println(TAG + ": " + exception.toString());
            }
        }
    }

/*
 * Log some error with tag and information about the exception
 * @param TAG the tag to use for debugging
 * @param exception the exception as a string object
 */
    public static void e(String TAG, String exception) {
        if (DEBUG) {
            if (ANDROID) {
                Log.e(TAG, exception);
            } else {
                System.err.println(TAG + ": " + exception);
            }
        }
    }

/*
 * Log some error with tag, message and information about the exception
 * @param TAG the tag to use for debugging
 * @param msg the message
 * @param e the exception
 */
    public static void e(String TAG, String msg, Exception e) {
        if (DEBUG) {
            if (ANDROID) {
                Log.e(TAG, msg, e);
            } else {
                System.err.println(TAG + ": " + msg);
                e.printStackTrace();
            }
        }
    }

/*
 * Log some information about running processes
 * @param msg the message
 */
    public static void i(Object msg) {
        if (DEBUG) {
            if (ANDROID) {
                Log.i(TAG, msg.toString());
            } else {
                System.out.println(TAG + ": " + msg.toString());
            }
        }
    }


}
