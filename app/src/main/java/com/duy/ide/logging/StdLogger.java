package com.duy.ide.logging;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.io.PrintStream;

public class StdLogger implements ILogger {
   private final StdLogger.Level mLevel;

   public StdLogger(@NonNull StdLogger.Level level) {
      if (level == null) {
         throw new IllegalArgumentException("level cannot be null");
      } else {
         this.mLevel = level;
      }
   }

   private void printMessage(String message, PrintStream stream) {
      stream.print(message);
      if (!message.endsWith("\n")) {
         stream.println();
      }

   }

   public void error(@Nullable Throwable throwable, @Nullable String var2, Object... var3) {
      if (var2 != null) {
         StringBuilder var4 = new StringBuilder();
         var4.append("Error: ");
         var4.append(var2);
         this.printMessage(String.format(var4.toString(), var3), System.err);
      }

      if (throwable != null) {
         throwable.printStackTrace();
      }

   }

   public StdLogger.Level getLevel() {
      return this.mLevel;
   }

   public void info(@NonNull String var1, Object... var2) {
      if (this.mLevel.mLevel <= StdLogger.Level.INFO.mLevel) {
         this.printMessage(String.format(var1, var2), System.out);
      }
   }

   public void verbose(@NonNull String var1, Object... var2) {
      if (this.mLevel.mLevel <= StdLogger.Level.VERBOSE.mLevel) {
         this.printMessage(String.format(var1, var2), System.out);
      }
   }

   public void warning(@NonNull String var1, Object... var2) {
      if (this.mLevel.mLevel <= StdLogger.Level.WARNING.mLevel) {
         StringBuilder var3 = new StringBuilder();
         var3.append("Warning: ");
         var3.append(var1);
         this.printMessage(String.format(var3.toString(), var2), System.out);
      }
   }

   public static enum Level {
      ERROR(3),
      INFO(1),
      VERBOSE(0),
      WARNING(2);

      private final int mLevel;

      private Level(int level) {
         this.mLevel = level;
      }
   }
}