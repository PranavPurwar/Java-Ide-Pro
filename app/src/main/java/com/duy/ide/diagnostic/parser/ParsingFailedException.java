package com.duy.ide.diagnostic.parser;

import android.support.annotation.NonNull;

public class ParsingFailedException extends Exception {

   public ParsingFailedException() {
       
   }

   public ParsingFailedException(@NonNull Throwable throwable) {
      super(throwable);
   }
}