package com.duy.ide.diagnostic.view;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import com.duy.ide.editor.editor.R.id;
import com.duy.ide.editor.editor.R.string;

class DiagnosticPagerAdapter extends PagerAdapter {
   private Fragment fragment;

   public DiagnosticPagerAdapter(Fragment fragment) {
      this.fragment = fragment;
   }

   public int getCount() {
      return DiagnosticPagerAdapter.Page.values().length;
   }

   @Nullable
   public CharSequence getPageTitle(int pos) {
      return this.fragment.getString(DiagnosticPagerAdapter.Page.values()[pos].titleId);
   }

   @NonNull
   public View instantiateItem(@NonNull ViewGroup group, int pos) {
      return this.fragment.getView().findViewById(DiagnosticPagerAdapter.Page.values()[pos].layoutId);
   }

   public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
      if (view == obj) {
         return true;
      } else {
         return false;
      }
   }

   static enum Page {
      COMPILER_LOG(id.compiler_output_container, string.compiler_output),
      DIAGNOSTIC(id.diagnostic_list_view, string.diagnostic);

      private final int layoutId;
      private final int titleId;

      private Page(@IdRes int layoutId, @StringRes int titleId) {
         this.layoutId = layoutId;
         this.titleId = titleId;
      }
   }
}