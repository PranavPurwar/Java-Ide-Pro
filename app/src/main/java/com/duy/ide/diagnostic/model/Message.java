package com.duy.ide.diagnostic.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Message implements Serializable {
   @NonNull
   private final Message.Kind mKind;
   @NonNull
   private final String mRawMessage;
   @NonNull
   private final ArrayList<SourceFilePosition> mSourceFilePositions = new ArrayList();
   @NonNull
   private final String mText;

   public Message(@NonNull Message.Kind var1, @NonNull String var2, @NonNull SourceFilePosition var3, @NonNull SourceFilePosition... var4) {
      this.mKind = var1;
      this.mText = var2;
      this.mRawMessage = var2;
      this.mSourceFilePositions.add(var3);
      this.mSourceFilePositions.addAll(Arrays.asList(var4));
   }

   public Message(@NonNull Message.Kind var1, @NonNull String var2, @NonNull String var3, @NonNull SourceFilePosition var4, @NonNull SourceFilePosition... var5) {
      this.mKind = var1;
      this.mText = var2;
      this.mRawMessage = var3;
      this.mSourceFilePositions.add(var4);
      this.mSourceFilePositions.addAll(Arrays.asList(var5));
   }

   public Message(@NonNull Message.Kind var1, @NonNull String var2, @NonNull String var3, @NonNull ImmutableList<SourceFilePosition> var4) {
      this.mKind = var1;
      this.mText = var2;
      this.mRawMessage = var3;
      if (var4.isEmpty()) {
         this.mSourceFilePositions.add(SourceFilePosition.UNKNOWN);
      } else {
         this.mSourceFilePositions.addAll(var4);
      }

   }

   public boolean equals(Object var1) {
      boolean var2 = true;
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Message)) {
         return false;
      } else {
         Message var3 = (Message)var1;
         if (!Objects.equal(this.mKind, var3.mKind) || !Objects.equal(this.mText, var3.mText) || !Objects.equal(this.mSourceFilePositions, var3.mSourceFilePositions)) {
            var2 = false;
         }

         return var2;
      }
   }

   @Deprecated
   public int getColumn() {
      return ((SourceFilePosition)this.mSourceFilePositions.get(0)).getPosition().getStartColumn() + 1;
   }

   @NonNull
   public Message.Kind getKind() {
      return this.mKind;
   }

   @Deprecated
   public int getLineNumber() {
      return ((SourceFilePosition)this.mSourceFilePositions.get(0)).getPosition().getStartLine() + 1;
   }

   @NonNull
   public String getRawMessage() {
      return this.mRawMessage;
   }

   @NonNull
   public List<SourceFilePosition> getSourceFilePositions() {
      return this.mSourceFilePositions;
   }

   @Nullable
   public String getSourcePath() {
      File var1 = ((SourceFilePosition)this.mSourceFilePositions.get(0)).getFile().getSourceFile();
      return var1 == null ? null : var1.getAbsolutePath();
   }

   @NonNull
   public String getText() {
      return this.mText;
   }

   public int hashCode() {
      return Objects.hashCode(new Object[]{this.mKind, this.mText, this.mSourceFilePositions});
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("kind", this.mKind).add("text", this.mText).add("sources", this.mSourceFilePositions).toString();
   }

   public static enum Kind {
      ERROR,
      INFO,
      SIMPLE,
      STATISTICS,
      UNKNOWN,
      WARNING;

      @Nullable
      public static Message.Kind findIgnoringCase(String var0) {
         return findIgnoringCase(var0, (Message.Kind)null);
      }

      public static Message.Kind findIgnoringCase(String var0, Message.Kind var1) {
         Message.Kind[] var2 = values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Message.Kind var5 = var2[var4];
            if (var5.toString().equalsIgnoreCase(var0)) {
               return var5;
            }
         }

         return var1;
      }
   }
}