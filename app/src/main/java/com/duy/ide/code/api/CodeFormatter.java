package com.duy.ide.code.api;

import android.support.annotation.Nullable;

public interface CodeFormatter {
   @Nullable
   CharSequence format(CharSequence charSeq);
}