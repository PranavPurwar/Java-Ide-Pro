/* Decompiler 17ms, total 416ms, lines 50 */
package com.duy.common.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class TintImageView extends AppCompatImageView {
   private ColorStateList colorList;

   public TintImageView(Context var1) {
      super(var1);
      this.init(var1, (AttributeSet)null);
   }

   public TintImageView(Context var1, AttributeSet var2) {
      super(var1, var2);
      this.init(var1, var2);
   }

   public TintImageView(Context var1, AttributeSet var2, int var3) {
      super(var1, var2, var3);
      this.init(var1, var2);
   }

   private void init(Context var1, AttributeSet var2) {
      if (var2 != null) {
         TypedArray var3 = var1.obtainStyledAttributes(var2, new int[]{16842904});
         this.colorList = var3.getColorStateList(0);
         if (this.colorList != null) {
            this.setColorFilter(this.colorList.getDefaultColor());
         }

         var3.recycle();
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