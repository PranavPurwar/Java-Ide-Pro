package com.duy.android.compiler.d8;

import android.os.Build.VERSION;

public class StringHelper {
    public static String join(CharSequence charSequence, Iterable<? extends CharSequence> iterable) {
        if (VERSION.SDK_INT >= 26) {
            return String.join(charSequence, iterable);
        }
        requireNonNull(charSequence);
        requireNonNull(iterable);
        StringBuilder stringBuilder = new StringBuilder();
        for (CharSequence append : iterable) {
            stringBuilder.append(append);
            stringBuilder.append(charSequence);
        }
        stringBuilder.setLength(stringBuilder.length() - charSequence.length());
        return stringBuilder.toString();
    }

    public static String join(CharSequence charSequence, CharSequence[] charSequenceArr) {
        if (VERSION.SDK_INT >= 26) {
            return String.join(charSequence, charSequenceArr);
        }
        requireNonNull(charSequence);
        requireNonNull(charSequenceArr);
        StringBuilder stringBuilder = new StringBuilder();
        for (CharSequence append : charSequenceArr) {
            stringBuilder.append(append);
            stringBuilder.append(charSequence);
        }
        stringBuilder.setLength(stringBuilder.length() - charSequence.length());
        return stringBuilder.toString();
    }

    private static void requireNonNull(Object obj) {
        if (obj == null) {
            throw new NullPointerException();
        }
    }
}