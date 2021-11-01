/* Decompiler 3ms, total 459ms, lines 34 */
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

class Error2Parser extends AbstractAaptOutputParser {
   private static final List<Pattern> MSG_PATTERNS = ImmutableList.of(Pattern.compile("^ERROR:\\s+(.+)$"), Pattern.compile("Defined\\s+at\\s+file\\s+(.+)\\s+line\\s+(\\d+)"));

   public boolean parse(String var1, OutputLineReader var2, List<Message> var3, ILogger var4) throws ParsingFailedException {
      Matcher var6 = ((Pattern)MSG_PATTERNS.get(0)).matcher(var1);
      if (!var6.matches()) {
         return false;
      } else {
         var1 = var6.group(1);
         Matcher var5 = this.getNextLineMatcher(var2, (Pattern)MSG_PATTERNS.get(1));
         if (var5 == null) {
            throw new ParsingFailedException();
         } else {
            String var7 = var5.group(1);
            String var8 = var5.group(2);
            var3.add(this.createMessage(Kind.ERROR, var1, var7, var8, "", var4));
            return true;
         }
      }
   }
}