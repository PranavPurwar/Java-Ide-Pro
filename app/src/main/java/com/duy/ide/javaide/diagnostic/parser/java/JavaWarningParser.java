package com.duy.ide.javaide.diagnostic.parser.java;

import android.support.annotation.NonNull;
import com.duy.ide.diagnostic.model.Message;
import com.duy.ide.diagnostic.model.SourceFile;
import com.duy.ide.diagnostic.model.SourceFilePosition;
import com.duy.ide.diagnostic.model.Message.Kind;
import com.duy.ide.diagnostic.parser.ParsingFailedException;
import com.duy.ide.diagnostic.util.OutputLineReader;
import com.duy.ide.logging.ILogger;
import java.io.File;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class JavaWarningParser extends AbstractJavaOutputParser {
   private static final Pattern PATTERN = Pattern.compile("(\\S+):([0-9]+): (warning:)(.*)");

   public boolean parse(@NonNull String var1, @NonNull OutputLineReader var2, @NonNull List<Message> var3, @NonNull ILogger var4) throws ParsingFailedException {
      Matcher var12 = PATTERN.matcher(var1);
      if (!var12.find()) {
         return false;
      } else {
         try {
            String var11 = var12.group(1);
            var1 = var12.group(4);
            String var5 = var12.group(2);
            Kind var7 = Kind.WARNING;
            File var13 = new File(var11);
            SourceFile var9 = new SourceFile(var13);
            SourceFilePosition var8 = new SourceFilePosition(var9, this.parseLineNumber(var5));
            Message var6 = new Message(var7, var1, var8, new SourceFilePosition[0]);
            var3.add(var6);
            return true;
         } catch (Exception var10) {
            return false;
         }
      }
   }
}