package com.duy.ide.diagnostic.suggestion;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import java.io.File;

public interface ISuggestion extends Parcelable {
   int getColEnd();

   int getColStart();

   int getLineEnd();

   int getLineStart();

   @NonNull
   String getMessage();

   File getSourceFile();
}