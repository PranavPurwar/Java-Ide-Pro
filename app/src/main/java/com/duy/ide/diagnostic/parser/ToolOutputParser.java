package com.duy.ide.diagnostic.parser;

import android.support.annotation.NonNull;
import com.duy.ide.diagnostic.model.Message;
import com.duy.ide.diagnostic.model.SourceFilePosition;
import com.duy.ide.diagnostic.model.Message.Kind;
import com.duy.ide.diagnostic.util.OutputLineReader;
import com.duy.ide.logging.ILogger;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ToolOutputParser {
   @NonNull
   private final ILogger mLogger;
   @NonNull
   private final List<PatternAwareOutputParser> mParsers;

   public ToolOutputParser(@NonNull PatternAwareOutputParser parser, @NonNull ILogger logger) {
      this.mParsers = ImmutableList.of(parser);
      this.mLogger = logger;
   }

   public ToolOutputParser(@NonNull Iterable<PatternAwareOutputParser> list, @NonNull ILogger logger) {
      this.mParsers = ImmutableList.copyOf(list);
      this.mLogger = logger;
   }

   public ToolOutputParser(@NonNull PatternAwareOutputParser[] parser, @NonNull ILogger logger) {
      this.mParsers = ImmutableList.copyOf(parser);
      this.mLogger = logger;
   }

   public List<Message> parseToolOutput(@NonNull String string) {
      OutputLineReader reader = new OutputLineReader(string);
      if (reader.getLineCount() == 0) {
         return Collections.emptyList();
      } else {
         ArrayList list = Lists.newArrayList();

         while(true) {
            String var4 = reader.readLine();
            if (var4 == null) {
               break;
            }

            if (!var4.isEmpty()) {
               Iterator var5 = this.mParsers.iterator();

               boolean var7;
               while(true) {
                  if (var5.hasNext()) {
                     PatternAwareOutputParser var9 = (PatternAwareOutputParser)var5.next();

                     boolean var6;
                     try {
                        var6 = var9.parse(var4, reader, list, this.mLogger);
                     } catch (ParsingFailedException ignored) {
                        return Collections.emptyList();
                     }

                     if (!var6) {
                        continue;
                     }

                     var7 = true;
                     break;
                  }

                  var7 = false;
                  break;
               }

               if (var7) {
                  int var10 = list.size();
                  if (var10 > 0 && ((Message)list.get(var10 - 1)).getText().contains("Build cancelled")) {
                     break;
                  }
               } else {
                  list.add(new Message(Kind.SIMPLE, var4, SourceFilePosition.UNKNOWN, new SourceFilePosition[0]));
               }
            }
         }

         return list;
      }
   }
}