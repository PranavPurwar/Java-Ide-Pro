package com.duy.common;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;
import com.jecelyin.common.utils.DLog;

public class StoreUtil {
    private static final String TAG = "StoreUtil";

    public static void gotoPlayStore(Activity activity, String str) {
        if (DLog.DEBUG) {
            DLog.d(TAG, "gotoPlayStore() called with: context = [" + activity + "], appId = [" + str + "]");
        }
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(String.format("market://details?id=%s", new Object[]{str})));
        intent.addFlags(1207959552);
        try {
            activity.startActivity(intent);
        } catch (Exception e) {
            if (DLog.DEBUG) {
                DLog.e(e);
            }
            try {
                activity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://play.google.com/store/apps/details?id=" + str)));
            } catch (Exception e2) {
                if (DLog.DEBUG) {
                    DLog.e(e2);
                }
            }
        }
    }

    public static void gotoPlayStore(Activity activity, String str, int i) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + str));
        intent.addFlags(1207959552);
        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException unused) {
            gotoToLink(activity, "http://play.google.com/store/apps/details?id=" + str, i);
        }
    }

    @Deprecated
    public static void gotoToLink(Activity activity, String str, int i) {
        openBrowser(activity, str, i);
    }

    public static boolean isAppInstalled(Context context, String str) {
        try {
            context.getPackageManager().getApplicationInfo(str, 0);
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public static void moreApp(Activity activity) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/dev?id=7055567654109499514"));
        intent.addFlags(1207959552);
        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException unused) {
            activity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/dev?id=7055567654109499514")));
        }
    }

    public static void openBrowser(Activity activity, String str, int i) {
        try {
            activity.startActivityForResult(new Intent("android.intent.action.VIEW", Uri.parse(str)), i);
        } catch (Exception e) {
            Toast.makeText(activity, e.getMessage(), 0).show();
        }
    }

    @Deprecated
    public static void shareApp(Activity activity, String str) {
        ShareUtil.shareApp(activity, str);
    }

    public static void shareThisApp(Activity activity) {
        ShareUtil.shareThisApp(activity);
    }
}
