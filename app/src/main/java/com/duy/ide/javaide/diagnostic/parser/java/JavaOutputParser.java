package com.duy.ide.javaide.diagnostic.parser.java;

import android.support.annotation.NonNull;
import com.duy.ide.diagnostic.model.Message;
import com.duy.ide.diagnostic.parser.ParsingFailedException;
import com.duy.ide.diagnostic.parser.PatternAwareOutputParser;
import com.duy.ide.diagnostic.util.OutputLineReader;
import com.duy.ide.logging.ILogger;
import java.util.List;

public class JavaOutputParser implements PatternAwareOutputParser {
   private static JavaWarningParser javaWarn = new JavaWarningParser();
   private static JavaErrorParser javaError = new JavaErrorParser();
   private static final PatternAwareOutputParser[] PARSERS = new PatternAwareOutputParser[]{javaWarn, javaError};

   public boolean parse(@NonNull String var1, @NonNull OutputLineReader var2, @NonNull List<Message> var3, @NonNull ILogger var4) {
      PatternAwareOutputParser[] var5 = PARSERS;
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         PatternAwareOutputParser var8 = var5[var7];

         boolean var9;
         try {
            var9 = var8.parse(var1, var2, var3, var4);
         } catch (ParsingFailedException var10) {
            continue;
         }

         if (var9) {
            return true;
         }
      }

      return false;
   }
}