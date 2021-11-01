/* Decompiler 4ms, total 607ms, lines 17 */
package com.duy.ide.javaide.diagnostic.parser.aapt;

import com.duy.ide.diagnostic.model.Message;
import com.duy.ide.diagnostic.parser.ParsingFailedException;
import com.duy.ide.diagnostic.util.OutputLineReader;
import com.duy.ide.logging.ILogger;
import java.util.List;
import java.util.regex.Pattern;

class SkippingHiddenFileParser extends AbstractAaptOutputParser {
   private static final Pattern MSG_PATTERN = Pattern.compile("^\\s+\\(skipping hidden file\\s'(.*)'\\)$");

   public boolean parse(String var1, OutputLineReader var2, List<Message> var3, ILogger var4) throws ParsingFailedException {
      return MSG_PATTERN.matcher(var1).matches();
   }
}