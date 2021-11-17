package com.duy.ide.editor.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.ListPopupWindow;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.duy.common.DLog;
import com.duy.ide.code.api.SuggestItem;
import com.duy.ide.editor.editor.R.string;
import com.duy.ide.editor.internal.suggestion.OnSuggestItemClickListener;
import com.duy.ide.editor.internal.suggestion.SuggestionAdapter;
import com.duy.ide.editor.theme.model.EditorTheme;
import com.jecelyin.common.utils.SysUtils;
import java.util.ArrayList;
import java.util.List;

public class SuggestionEditor extends EditActionSupportEditor implements OnItemClickListener {
   private static final String TAG = "SuggestionEditor";
   private SuggestionAdapter mAdapter;
   private ListPopupWindow mPopup;
   private final Runnable mPostChangePopupPosition = new Runnable() {
      public void run() {
         SuggestionEditor.this.onPopupChangePosition();
      }
   };
   private boolean mSuggestionEnable = false;
   private final Rect mTmpRect = new Rect();

   public SuggestionEditor(Context var1) {
      super(var1);
      this.init(var1, null, 0);
   }

   public SuggestionEditor(Context var1, AttributeSet var2) {
      super(var1, var2);
      this.init(var1, var2, 0);
   }

   public SuggestionEditor(Context var1, AttributeSet var2, int var3) {
      super(var1, var2, var3);
      this.init(var1, var2, var3);
   }

   private int getSelectedItemPosition() {
      return this.mPopup.getListView().getSelectedItemPosition();
   }

   private void init(Context var1, AttributeSet var2, int var3) {
      this.mAdapter = new SuggestionAdapter(this.getContext(), new ArrayList());
      this.mPopup = new ListPopupWindow(var1, var2, var3, 0);
      this.mPopup.setSoftInputMode(16);
      this.mPopup.setAdapter(this.mAdapter);
      this.mAdapter.setListener(this);
      if (this.getEditorTheme() != null) {
         this.setThemeForPopup(this.getEditorTheme());
      }

      this.setSuggestEnable(this.mPreferences.isUseAutoComplete());
   }

   @SuppressLint({"RestrictedApi"})
   private void performCompletion() {
      this.performCompletion(-1);
   }

   @SuppressLint({"RestrictedApi"})
   private void performCompletion(int var1) {
      if (this.isPopupShowing() && this.mAdapter != null) {
         int var2 = var1;
         if (var1 < 0) {
            var2 = this.mPopup.getSelectedItemPosition();
         }

         if (var2 < 0) {
            return;
         }

         SuggestItem var3 = this.mAdapter.getItem(var2);
         if (var3 != null) {
            var3.onSelectThis(this);
         }
      }

      if (!this.mPopup.isDropDownAlwaysVisible()) {
         this.dismissDropDown();
         this.requestFocus();
      }

   }

   private void postUpdatePosition() {
      if (this.mSuggestionEnable && this.mHandler != null) {
         this.mHandler.removeCallbacks(this.mPostChangePopupPosition);
         this.mHandler.postDelayed(this.mPostChangePopupPosition, 50L);
      }
   }

   private void setThemeForPopup(EditorTheme var1) {
      GradientDrawable var2 = new GradientDrawable();
      var2.setColor(var1.getDropdownBgColor());
      var2.setShape(0);
      var2.setCornerRadius((float)SysUtils.dpToPixels(this.getContext(), 4));
      var2.setStroke(SysUtils.dpToPixels(this.getContext(), 1), var1.getDropdownBorderColor());
      this.mPopup.setBackgroundDrawable(var2);
      this.mAdapter.setTextColor(var1.getDropdownFgColor());
   }

   private void showDropDownImpl() {
      if (this.mPopup.getAnchorView() == null) {
         this.mPopup.setAnchorView(this);
      }

      if (!this.isPopupShowing()) {
         this.mPopup.setInputMethodMode(1);
      }

      this.mPopup.show();
      this.mPopup.getListView().setOverScrollMode(0);
   }

   public void clearListSelection() {
      this.mPopup.clearListSelection();
   }

   public void dismissDropDown() {
      try {
         if (this.mPopup.isShowing()) {
            this.mPopup.dismiss();
         }
      } catch (Exception var2) {
      }

   }

   public int getDropDownHeight() {
      return this.mPopup.getHeight();
   }

   public int getDropDownHorizontalOffset() {
      return this.mPopup.getHorizontalOffset();
   }

   public int getDropDownVerticalOffset() {
      return this.mPopup.getVerticalOffset();
   }

   public boolean isPopupShowing() {
      return this.mPopup.isShowing();
   }

   protected void onDetachedFromWindow() {
      this.dismissDropDown();
      this.mHandler.removeCallbacks(this.mPostChangePopupPosition);
      super.onDetachedFromWindow();
   }

   @SuppressLint({"RestrictedApi"})
   protected void onDisplayHint(int var1) {
      super.onDisplayHint(var1);
      if (var1 == 4 && !this.mPopup.isDropDownAlwaysVisible()) {
         this.dismissDropDown();
      }

   }

   protected void onDropdownChangeSize() {
      Rect var1 = new Rect();
      this.getGlobalVisibleRect(var1);
      if (DLog.DEBUG) {
         StringBuilder var2 = new StringBuilder();
         var2.append("rect = ");
         var2.append(var1);
         DLog.d(TAG, var2.toString());
      }

      this.setDropDownWidth((int)((float)var1.width() * 0.65F));
      this.setDropDownHeight((int)((float)var1.height() * 0.5F));
      this.onPopupChangePosition();
   }

   @SuppressLint({"RestrictedApi"})
   protected void onFocusChanged(boolean var1, int var2, Rect var3) {
      super.onFocusChanged(var1, var2, var3);
      if ((VERSION.SDK_INT < 24 || !this.isTemporarilyDetached()) && !var1 && !this.mPopup.isDropDownAlwaysVisible()) {
         this.dismissDropDown();

      }
   }

   public void onItemClick(AdapterView<?> var1, View var2, int var3, long var4) {
      this.performCompletion(var3);
   }

   public boolean onKeyDown(int var1, KeyEvent var2) {
      if (!this.isPopupShowing()) {
         switch(var1) {
         default:
            switch(var1) {
            case 268:
            case 269:
            case 270:
            case 271:
               break;
            default:
               return super.onKeyDown(var1, var2);
            }
         case 19:
         case 20:
            super.onKeyDown(var1, var2);
            return true;
         }
      } else if ((var1 == 66 || var1 == 23) && this.getSelectedItemPosition() >= 0) {
         return true;
      } else if (var1 != 61 && this.mPopup.onKeyDown(var1, var2)) {
         return true;
      } else {
         boolean var3 = super.onKeyDown(var1, var2);
         if (var3) {
            this.clearListSelection();
         }

         return var3;
      }
   }

   public boolean onKeyUp(int var1, KeyEvent var2) {
      if (this.isPopupShowing() && var1 != 61) {
         this.mPopup.onKeyUp(var1, var2);
         if (var1 == 23 || var1 == 66) {
            if (var2.hasNoModifiers()) {
               this.performCompletion();
            }

            return true;
         }
      }

      return super.onKeyUp(var1, var2);
   }

   protected void onPopupChangePosition() {
      if (this.mSuggestionEnable) {
         Exception var10000;
         label56: {
            Layout var1;
            try {
               var1 = this.getLayout();
            } catch (Exception var7) {
               var10000 = var7;
               break label56;
            }

            if (var1 == null) {
               return;
            }

            int var2;
            try {
               var2 = this.getSelectionStart();
            } catch (Exception var6) {
               var10000 = var6;
               break label56;
            }

            if (var2 < 0) {
               return;
            }

            int var3;
            try {
               var3 = var1.getLineForOffset(var2);
               int var4 = var1.getLineBottom(var3);
               var3 = var1.getLineTop(var3);
               this.setDropDownHorizontalOffset((int) (var1.getPrimaryHorizontal(var2)) + this.getPaddingLeft() - this.getScrollX());
               this.getGlobalVisibleRect(this.mTmpRect);
               var2 = this.mTmpRect.height();
               if (var4 - this.getScrollY() + this.getDropDownHeight() < var2) {
                  this.setDropDownVerticalOffset(var4 - this.getScrollY() + this.getDropDownHeight());
                  return;
               }
            } catch (Exception var8) {
               var10000 = var8;
               break label56;
            }

            try {
               this.setDropDownVerticalOffset(var3 - this.getScrollY() - this.getDropDownHeight() + this.getDropDownHeight());
               return;
            } catch (Exception var5) {
               var10000 = var5;
            }
         }

         Exception var9 = var10000;
         if (DLog.DEBUG) {
            DLog.e(TAG, "onPopupChangePosition: ", var9);
         }

      }
   }

   public void onSharedPreferenceChanged(SharedPreferences var1, String var2) {
      super.onSharedPreferenceChanged(var1, var2);
      if (var2.equals(this.getContext().getString(string.pref_auto_complete))) {
         this.mSuggestionEnable = this.mPreferences.isUseAutoComplete();
         this.setSuggestEnable(this.mSuggestionEnable);
      }

   }

   protected void onSizeChanged(int var1, int var2, int var3, int var4) {
      super.onSizeChanged(var1, var2, var3, var4);
      this.onDropdownChangeSize();
   }

   public void onTextChanged(CharSequence var1, int var2, int var3, int var4) {
      super.onTextChanged(var1, var2, var3, var4);
      this.postUpdatePosition();
   }

   @SuppressLint({"RestrictedApi"})
   public void onWindowFocusChanged(boolean var1) {
      super.onWindowFocusChanged(var1);
      if (!var1 && !this.mPopup.isDropDownAlwaysVisible()) {
         this.dismissDropDown();
      }

   }

   public void setAdapter(SuggestionAdapter var1) {
      this.mAdapter = var1;
      this.mPopup.setAdapter(this.mAdapter);
   }

   public void setDropDownHeight(int var1) {
      this.mPopup.setHeight(var1);
   }

   public void setDropDownHorizontalOffset(int var1) {
      if (var1 != this.mPopup.getHorizontalOffset()) {
         this.mPopup.setHorizontalOffset(var1);
      }

   }

   public void setDropDownVerticalOffset(int var1) {
      if (var1 != this.mPopup.getVerticalOffset()) {
         this.mPopup.setVerticalOffset(var1);
      }

   }

   public void setDropDownWidth(int var1) {
      this.mPopup.setWidth(var1);
   }

   protected boolean setFrame(int var1, int var2, int var3, int var4) {
      boolean var5 = super.setFrame(var1, var2, var3, var4);
      if (this.isPopupShowing()) {
         this.showDropDown();
      }

      return var5;
   }

   public void setOnSuggestItemClickListener(@Nullable OnSuggestItemClickListener var1) {
       // not implemented here
   }

   public void setSuggestData(@NonNull List<SuggestItem> var1) {
      if (this.mSuggestionEnable) {
         this.mAdapter.setData(var1);
         this.mAdapter.notifyDataSetChanged();
         if (var1.size() > 0) {
            this.showDropDown();
         } else if (this.isPopupShowing()) {
            this.dismissDropDown();
         }

      }
   }

   public void setSuggestEnable(boolean var1) {
      this.mSuggestionEnable = var1;
      if (this.mSuggestionEnable) {
         this.onDropdownChangeSize();
      } else {
         this.dismissDropDown();
      }

   }

   public void setTheme(@NonNull EditorTheme var1) {
      super.setTheme(var1);
      if (this.mPopup != null) {
         this.setThemeForPopup(var1);
      }

   }

   public void showDropDown() {
      if (this.mSuggestionEnable && !this.isPopupShowing() && this.hasFocus()) {
         try {
            this.showDropDownImpl();
         } catch (Exception var2) {
            if (DLog.DEBUG) {
               DLog.e(TAG, "showDropDown: ", var2);
            }
         }
      }
   }
}