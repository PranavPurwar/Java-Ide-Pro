package com.duy.ide.diagnostic.model;

import android.support.annotation.NonNull;
import com.google.common.base.Objects;
import java.io.File;
import java.io.Serializable;

public final class SourceFilePosition implements Serializable {
   public static final SourceFilePosition UNKNOWN;
   @NonNull
   private final SourceFile mSourceFile;
   @NonNull
   private final SourcePosition mSourcePosition;

   static {
      UNKNOWN = new SourceFilePosition(SourceFile.UNKNOWN, SourcePosition.UNKNOWN);
   }

   public SourceFilePosition(@NonNull SourceFile var1, @NonNull SourcePosition var2) {
      this.mSourceFile = var1;
      this.mSourcePosition = var2;
   }

   public SourceFilePosition(@NonNull File var1, @NonNull SourcePosition var2) {
      this(new SourceFile(var1), var2);
   }

   public boolean equals(Object var1) {
      boolean var2 = true;
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof SourceFilePosition)) {
         return false;
      } else {
         SourceFilePosition var3 = (SourceFilePosition)var1;
         if (!Objects.equal(this.mSourceFile, var3.mSourceFile) || !Objects.equal(this.mSourcePosition, var3.mSourcePosition)) {
            var2 = false;
         }

         return var2;
      }
   }

   @NonNull
   public SourceFile getFile() {
      return this.mSourceFile;
   }

   @NonNull
   public SourcePosition getPosition() {
      return this.mSourcePosition;
   }

   public int hashCode() {
      return Objects.hashCode(new Object[]{this.mSourceFile, this.mSourcePosition});
   }

   public String print(boolean var1) {
      if (this.mSourcePosition.equals(SourcePosition.UNKNOWN)) {
         return this.mSourceFile.print(var1);
      } else {
         StringBuilder var2 = new StringBuilder();
         var2.append(this.mSourceFile.print(var1));
         var2.append(':');
         var2.append(this.mSourcePosition.toString());
         return var2.toString();
      }
   }

   public String toString() {
      return this.print(false);
   }
}