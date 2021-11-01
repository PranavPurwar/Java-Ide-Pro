/* Decompiler 5ms, total 1688ms, lines 35 */
package com.duy.ide.javaide.diagnostic.parser.aapt;

import com.duy.ide.diagnostic.model.Message;
import com.duy.ide.diagnostic.parser.ParsingFailedException;
import com.duy.ide.diagnostic.parser.PatternAwareOutputParser;
import com.duy.ide.diagnostic.util.OutputLineReader;
import com.duy.ide.logging.ILogger;
import java.util.List;

public class AaptOutputParser implements PatternAwareOutputParser {
   private static final AbstractAaptOutputParser[] PARSERS = new AbstractAaptOutputParser[]{new SkippingHiddenFileParser(), new Error1Parser(), new Error6Parser(), new Error2Parser(), new Error3Parser(), new Error4Parser(), new Warning1Parser(), new Error5Parser(), new Error7Parser(), new Error8Parser(), new SkippingWarning2Parser(), new SkippingWarning1Parser(), new BadXmlBlockParser()};

   public boolean parse(String var1, OutputLineReader var2, List<Message> var3, ILogger var4) {
      AbstractAaptOutputParser[] var5 = PARSERS;
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         AbstractAaptOutputParser var8 = var5[var7];

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