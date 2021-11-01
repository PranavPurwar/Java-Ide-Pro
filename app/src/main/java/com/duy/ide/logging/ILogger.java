/* Decompiler 0ms, total 1657ms, lines 20 */
package com.duy.ide.logging;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

public interface ILogger {
   @WorkerThread
   void error(@Nullable Throwable var1, @Nullable String var2, Object... var3);

   @WorkerThread
   void info(@NonNull String var1, Object... var2);

   @WorkerThread
   void verbose(@NonNull String var1, Object... var2);

   @WorkerThread
   void warning(@NonNull String var1, Object... var2);
}