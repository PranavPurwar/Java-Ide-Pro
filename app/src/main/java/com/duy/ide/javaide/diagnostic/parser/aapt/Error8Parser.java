package com.duy.ide.javaide.diagnostic.parser.aapt;

import com.duy.ide.diagnostic.model.Message;
import com.duy.ide.diagnostic.model.Message.Kind;
import com.duy.ide.diagnostic.parser.ParsingFailedException;
import com.duy.ide.diagnostic.util.OutputLineReader;
import com.duy.ide.logging.ILogger;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Error8Parser extends AbstractAaptOutputParser {
   private static final Pattern MSG_PATTERN = Pattern.compile("^Invalid configuration: (.+)$");

   public boolean parse(String var1, OutputLineReader var2, List<Message> var3, ILogger var4) throws ParsingFailedException {
      Matcher var5 = MSG_PATTERN.matcher(var1);
      if (!var5.matches()) {
         return false;
      } else {
         var1 = String.format("APK Configuration filter '%1$s' is invalid", var5.group(1));
         var2.skipNextLine();
         var3.add(this.createMessage(Kind.ERROR, var1, (String)null, (String)null, "", var4));
         return true;
      }
   }
}