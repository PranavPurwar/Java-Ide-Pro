/* Decompiler 8ms, total 439ms, lines 28 */
package com.duy.ide.javaide.diagnostic.parser.aapt;

import com.duy.ide.diagnostic.model.Message;
import com.duy.ide.diagnostic.model.Message.Kind;
import com.duy.ide.diagnostic.parser.ParsingFailedException;
import com.duy.ide.diagnostic.util.OutputLineReader;
import com.duy.ide.logging.ILogger;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Warning1Parser extends AbstractAaptOutputParser {
   private static final Pattern MSG_PATTERN = Pattern.compile("^(.+?):(\\d+):\\s+WARNING:(.+)$");

   public boolean parse(String var1, OutputLineReader var2, List<Message> var3, ILogger var4) throws ParsingFailedException {
      Matcher var5 = MSG_PATTERN.matcher(var1);
      if (!var5.matches()) {
         return false;
      } else {
         var1 = var5.group(1);
         String var6 = var5.group(2);
         String var7 = var5.group(3);
         var3.add(this.createMessage(Kind.WARNING, var7, var1, var6, "", var4));
         return true;
      }
   }
}