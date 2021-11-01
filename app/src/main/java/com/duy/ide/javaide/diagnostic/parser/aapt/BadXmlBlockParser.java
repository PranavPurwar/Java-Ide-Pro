/* Decompiler 9ms, total 646ms, lines 23 */
package com.duy.ide.javaide.diagnostic.parser.aapt;

import com.duy.ide.diagnostic.model.Message;
import com.duy.ide.diagnostic.parser.ParsingFailedException;
import com.duy.ide.diagnostic.util.OutputLineReader;
import com.duy.ide.logging.ILogger;
import java.util.List;
import java.util.regex.Pattern;

class BadXmlBlockParser extends AbstractAaptOutputParser {
   private static final Pattern MSG_PATTERN = Pattern.compile("W/ResourceType\\(.*\\): Bad XML block: no root element node found");

   public boolean parse(String var1, OutputLineReader var2, List<Message> var3, ILogger var4) throws ParsingFailedException {
      if (!MSG_PATTERN.matcher(var1).matches()) {
         return false;
      } else if (var2.getLineCount() == 1) {
         throw new ParsingFailedException();
      } else {
         return true;
      }
   }
}