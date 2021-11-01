package com.duy.ide.code.model;

import android.support.annotation.Nullable;
import com.duy.ide.code.api.CodeFormatter;
import org.json.JSONObject;

public class JsonFormatter implements CodeFormatter {
   @Nullable
   public CharSequence format(CharSequence sequence) {
      try {
         JSONObject object = new JSONObject(sequence.toString());
         String result = object.toString(1);
         return result;
      } catch (Exception ignored) {
         return null;
      }
   }
}