/* Decompiler 4ms, total 2311ms, lines 19 */
package com.duy.ide.code.model;

import android.support.annotation.Nullable;
import com.duy.ide.code.api.CodeFormatter;
import org.json.JSONObject;

public class JsonFormatter implements CodeFormatter {
   @Nullable
   public CharSequence format(CharSequence var1) {
      try {
         JSONObject var2 = new JSONObject(var1.toString());
         String var4 = var2.toString(1);
         return var4;
      } catch (Exception var3) {
         return null;
      }
   }
}