package com.duy.ide.javaide.editor.autocomplete.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import com.duy.ide.javaide.editor.autocomplete.model.ClassDescription;


public class SpanUtil {


    public static Spannable formatClass(Context context, ClassDescription item) {
        SpannableString simpleName = new SpannableString(item.getSimpleName());
        SpannableString packageName = new SpannableString("(" + item.getPackageName() + ")");
        simpleName.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context,
                android.R.color.primary_text_dark)), 0, simpleName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        packageName.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context,
                android.R.color.secondary_text_dark)), 0, packageName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return new SpannableStringBuilder().append(simpleName).append(packageName);
    }
}
