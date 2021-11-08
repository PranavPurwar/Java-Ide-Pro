package com.duy.file.explorer;

public class ExplorerException extends RuntimeException {
   public ExplorerException() {
   }

   public ExplorerException(String message) {
      super(message);
   }

   public ExplorerException(String message, Throwable throwable) {
      super(message, throwable);
   }

   public ExplorerException(Throwable th) {
      super(th);
   }
}