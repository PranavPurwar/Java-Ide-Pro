/* Decompiler 6ms, total 4769ms, lines 45 */
package com.duy.ide.javaide.diagnostic.parser.aapt;

import com.duy.ide.diagnostic.model.Message;
import com.duy.ide.diagnostic.model.Message.Kind;
import com.duy.ide.diagnostic.parser.ParsingFailedException;
import com.duy.ide.diagnostic.util.OutputLineReader;
import com.duy.ide.logging.ILogger;
import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Error5Parser extends AbstractAaptOutputParser {
   private static final List<Pattern> MSG_PATTERNS = ImmutableList.of(Pattern.compile("^(.+?):(\\d+): error: Error:\\s+(.+)$"), Pattern.compile("^(.+?):(\\d+): error:\\s+(.+)$"), Pattern.compile("^(.+?):(\\d+):\\s+(.+)$"));

   public boolean parse(String var1, OutputLineReader var2, List<Message> var3, ILogger var4) throws ParsingFailedException {
      Iterator var8 = MSG_PATTERNS.iterator();

      Matcher var5;
      do {
         if (!var8.hasNext()) {
            return false;
         }

         var5 = ((Pattern)var8.next()).matcher(var1);
      } while(!var5.matches());

      String var9 = var5.group(1);
      String var6 = var5.group(2);
      String var10 = var5.group(3);
      Kind var7 = Kind.ERROR;
      if (var10.startsWith("warning: ")) {
         var7 = Kind.WARNING;
      }

      if (var9.endsWith(".java")) {
         return false;
      } else {
         var3.add(this.createMessage(var7, var10, var9, var6, "", var4));
         return true;
      }
   }
}