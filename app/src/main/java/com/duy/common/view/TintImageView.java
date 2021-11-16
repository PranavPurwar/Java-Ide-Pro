package com.duy.common.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class TintImageView extends AppCompatImageView {
   private ColorStateList colorList;

   public TintImageView(Context context) {
      super(context);
      this.init(context, null);
   }

   public TintImageView(Context context, AttributeSet set) {
      super(context, set);
      this.init(context, set);
   }

   public TintImageView(Context context, AttributeSet set, int n) {
      super(context, set, n);
      this.init(context, set);
   }

   private void init(Context context, AttributeSet set) {
      if (set != null) {
         TypedArray typedArr = context.obtainStyledAttributes(set, new int[]{16842904});
         this.colorList = typedArr.getColorStateList(0);
         if (this.colorList != null) {
            this.setColorFilter(this.colorList.getDefaultColor());
         }

         typedArr.recycle();
      }

   }

   public void dispatchDrawableHotspotChanged(float var1, float var2) {
      super.dispatchDrawableHotspotChanged(var1, var2);
      this.clearColorFilter();
      this.setColorFilter(this.colorList.getColorForState(this.getDrawableState(), this.colorList.getDefaultColor()));
   }

   public void refreshDrawableState() {
      super.refreshDrawableState();
   }
}