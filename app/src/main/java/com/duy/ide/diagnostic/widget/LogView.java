package com.duy.ide.diagnostic.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class LogView extends AppCompatTextView {
   public LogView(Context context) {
      super(context);
   }

   public LogView(Context context, AttributeSet set) {
      super(context, set);
   }

   public LogView(Context context, AttributeSet set, int pos) {
      super(context, set, pos);
   }
}