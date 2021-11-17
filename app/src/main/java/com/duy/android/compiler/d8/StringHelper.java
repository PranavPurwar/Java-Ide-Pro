package com.duy.android.compiler.d8;

import android.os.Build.VERSION;
import java.util.Objects;

public class StringHelper {
    public static String join(CharSequence charSequence, Iterable<? extends CharSequence> iterable) {
        if (VERSION.SDK_INT >= 26) {
            return String.join(charSequence, iterable);
        }
        Objects.requireNonNull(charSequence);
        Objects.requireNonNull(iterable);
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
        Objects.requireNonNull(charSequence);
        Objects.requireNonNull(charSequenceArr);
        StringBuilder stringBuilder = new StringBuilder();
        for (CharSequence append : charSequenceArr) {
            stringBuilder.append(append);
            stringBuilder.append(charSequence);
        }
        stringBuilder.setLength(stringBuilder.length() - charSequence.length());
        return stringBuilder.toString();
    }
}