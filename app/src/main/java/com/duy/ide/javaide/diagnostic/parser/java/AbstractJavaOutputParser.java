package com.duy.ide.javaide.diagnostic.parser.java;

import com.duy.ide.diagnostic.model.SourcePosition;
import com.duy.ide.diagnostic.parser.ParsingFailedException;
import com.duy.ide.diagnostic.parser.PatternAwareOutputParser;

abstract class AbstractJavaOutputParser implements PatternAwareOutputParser {
   SourcePosition parseLineNumber(String var1) throws ParsingFailedException {
      int var2;
      if (var1 != null) {
         try {
            var2 = Integer.parseInt(var1);
         } catch (NumberFormatException var3) {
            throw new ParsingFailedException();
         }
      } else {
         var2 = -1;
      }

      return new SourcePosition(var2 - 1, -1, -1);
   }
}