package com.duy.ide.diagnostic.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.common.base.Objects;
import java.io.File;
import java.io.Serializable;

public final class SourceFile implements Serializable {
   public static final SourceFile UNKNOWN = new SourceFile();
   @Nullable
   private final String mDescription;
   @Nullable
   private final File mSourceFile;

   private SourceFile() {
      this.mSourceFile = null;
      this.mDescription = null;
   }

   public SourceFile(@NonNull File var1) {
      this.mSourceFile = var1;
      this.mDescription = null;
   }

   public SourceFile(@NonNull File var1, @NonNull String var2) {
      this.mSourceFile = var1;
      this.mDescription = var2;
   }

   public SourceFile(@NonNull String var1) {
      this.mSourceFile = null;
      this.mDescription = var1;
   }

   public boolean equals(Object var1) {
      boolean var2 = true;
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof SourceFile)) {
         return false;
      } else {
         SourceFile var3 = (SourceFile)var1;
         if (!Objects.equal(this.mDescription, var3.mDescription) || !Objects.equal(this.mSourceFile, var3.mSourceFile)) {
            var2 = false;
         }

         return var2;
      }
   }

   @Nullable
   public String getDescription() {
      return this.mDescription;
   }

   @Nullable
   public File getSourceFile() {
      return this.mSourceFile;
   }

   public int hashCode() {
      return Objects.hashCode(new Object[]{this.mSourceFile, this.mDescription});
   }

   public String print(boolean var1) {
      if (this.mSourceFile == null) {
         return this.mDescription == null ? "Unknown source file" : this.mDescription;
      } else {
         String var2 = this.mSourceFile.getName();
         String var3;
         if (var1) {
            var3 = var2;
         } else {
            var3 = this.mSourceFile.getAbsolutePath();
         }

         return this.mDescription != null && !this.mDescription.equals(var2) ? String.format("[%1$s] %2$s", this.mDescription, var3) : var3;
      }
   }

   public String toString() {
      return this.print(false);
   }
}