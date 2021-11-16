package com.duy.common;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import java.io.File;

public class ShareUtil {
    public static void shareApp(Activity activity, String str) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.putExtra("android.intent.extra.TEXT", String.format("http://play.google.com/store/apps/details?id=%s", new Object[]{str}));
        intent.setType("text/plain");
        try {
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void shareImage(Context context, File file) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        Intent intent = new Intent("android.intent.action.SEND");
        intent.putExtra("android.intent.extra.STREAM", uri);
        intent.addFlags(1);
        intent.setType("image/*");
        context.startActivity(Intent.createChooser(intent, "Share image via"));
    }

    public static void shareText(Context context, CharSequence charSequence) {
        try {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.SEND");
            intent.putExtra("android.intent.extra.TEXT", charSequence);
            intent.setType("text/plain");
            context.startActivity(intent);
        } catch (ActivityNotFoundException unused) {
        }
    }

    @Deprecated
    public static void shareText(String str, Context context) {
        shareText(context, (CharSequence) str);
    }

    public static void shareThisApp(Activity activity) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.putExtra("android.intent.extra.TEXT", "http://play.google.com/store/apps/details?id=" + activity.getPackageName());
        intent.setType("text/plain");
        try {
            activity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
