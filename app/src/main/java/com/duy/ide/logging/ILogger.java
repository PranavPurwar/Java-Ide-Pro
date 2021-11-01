package com.duy.ide.logging;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

public interface ILogger {
   @WorkerThread
   void error(@Nullable Throwable th, @Nullable String tag, Object... content);

   @WorkerThread
   void info(@NonNull String tag, Object... content);

   @WorkerThread
   void verbose(@NonNull String tag, Object... content);

   @WorkerThread
   void warning(@NonNull String tag, Object... content);
}