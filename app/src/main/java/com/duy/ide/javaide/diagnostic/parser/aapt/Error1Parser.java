/* Decompiler 4ms, total 628ms, lines 34 */
package com.duy.ide.javaide.diagnostic.parser.aapt;

import com.duy.ide.diagnostic.model.Message;
import com.duy.ide.diagnostic.model.Message.Kind;
import com.duy.ide.diagnostic.parser.ParsingFailedException;
import com.duy.ide.diagnostic.util.OutputLineReader;
import com.duy.ide.logging.ILogger;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Error1Parser extends AbstractAaptOutputParser {
   private static final List<Pattern> MSG_PATTERNS = ImmutableList.of(Pattern.compile("^ERROR\\s+at\\s+line\\s+(\\d+):\\s+(.*)$"), Pattern.compile("^\\s+\\(Occurred while parsing\\s+(.*)\\)$"));

   public boolean parse(String var1, OutputLineReader var2, List<Message> var3, ILogger var4) throws ParsingFailedException {
      Matcher var5 = ((Pattern)MSG_PATTERNS.get(0)).matcher(var1);
      if (!var5.matches()) {
         return false;
      } else {
         var1 = var5.group(1);
         String var8 = var5.group(2);
         Matcher var6 = this.getNextLineMatcher(var2, (Pattern)MSG_PATTERNS.get(1));
         if (var6 == null) {
            throw new ParsingFailedException();
         } else {
            String var7 = var6.group(1);
            var3.add(this.createMessage(Kind.ERROR, var8, var7, var1, "", var4));
            return true;
         }
      }
   }
}