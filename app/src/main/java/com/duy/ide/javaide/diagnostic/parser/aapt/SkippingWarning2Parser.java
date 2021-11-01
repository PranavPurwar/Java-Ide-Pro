/* Decompiler 3ms, total 470ms, lines 30 */
package com.duy.ide.javaide.diagnostic.parser.aapt;

import com.duy.ide.diagnostic.model.Message;
import com.duy.ide.diagnostic.model.Message.Kind;
import com.duy.ide.diagnostic.parser.ParsingFailedException;
import com.duy.ide.diagnostic.util.OutputLineReader;
import com.duy.ide.logging.ILogger;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class SkippingWarning2Parser extends AbstractAaptOutputParser {
   private static final Pattern MSG_PATTERN = Pattern.compile("    \\(skipping .+ '(.+)' due to ANDROID_AAPT_IGNORE pattern '.+'\\)");

   public boolean parse(String var1, OutputLineReader var2, List<Message> var3, ILogger var4) throws ParsingFailedException {
      Matcher var5 = MSG_PATTERN.matcher(var1);
      if (!var5.matches()) {
         return false;
      } else {
         String var6 = var5.group(1);
         if (var6 == null || !var6.startsWith(".") && !var6.endsWith("~")) {
            var3.add(this.createMessage(Kind.WARNING, var1, var6, (String)null, "", var4));
            return true;
         } else {
            return true;
         }
      }
   }
}