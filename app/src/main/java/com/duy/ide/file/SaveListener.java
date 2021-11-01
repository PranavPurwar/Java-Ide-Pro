package com.duy.ide.file;

import android.support.annotation.UiThread;

public interface SaveListener {
   @UiThread
   void onSaveFailed(Exception e);

   @UiThread
   void onSavedSuccess();
}