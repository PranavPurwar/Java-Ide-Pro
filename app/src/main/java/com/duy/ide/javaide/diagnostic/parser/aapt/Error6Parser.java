package com.duy.ide.javaide.diagnostic.parser.aapt;

import com.duy.ide.diagnostic.model.Message;
import com.duy.ide.diagnostic.model.Message.Kind;
import com.duy.ide.diagnostic.parser.ParsingFailedException;
import com.duy.ide.diagnostic.util.OutputLineReader;
import com.duy.ide.logging.ILogger;
import com.google.common.base.Strings;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Error6Parser extends AbstractAaptOutputParser {
   private static final Pattern MSG_PATTERN = Pattern.compile("^ERROR:\\s+9-patch\\s+image\\s+(.+)\\s+malformed\\.$");

   public boolean parse(String var1, OutputLineReader var2, List<Message> var3, ILogger var4) throws ParsingFailedException {
      Matcher var5 = MSG_PATTERN.matcher(var1);
      if (!var5.matches()) {
         return false;
      } else {
         String var6 = var5.group(1);
         if (var2.hasNextLine()) {
            var1 = Strings.nullToEmpty(var2.readLine()).trim();
            if (var2.hasNextLine()) {
               var1 = var1 + " - " + Strings.nullToEmpty(var2.readLine()).trim();
               var2.skipNextLine();
            }
         }

         var3.add(this.createMessage(Kind.ERROR, var1, var6, (String)null, "", var4));
         return true;
      }
   }
}