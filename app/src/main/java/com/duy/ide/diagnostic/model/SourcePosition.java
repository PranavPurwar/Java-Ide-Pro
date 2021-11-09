package com.duy.ide.diagnostic.model;

import com.google.common.base.Objects;
import java.io.Serializable;

public final class SourcePosition implements Serializable {
   public static final SourcePosition UNKNOWN = new SourcePosition();
   private final int mEndColumn;
   private final int mEndLine;
   private final int mEndOffset;
   private final int mStartColumn;
   private final int mStartLine;
   private final int mStartOffset;

   private SourcePosition() {
      this.mEndOffset = -1;
      this.mEndColumn = -1;
      this.mEndLine = -1;
      this.mStartOffset = -1;
      this.mStartColumn = -1;
      this.mStartLine = -1;
   }

   public SourcePosition(int var1, int var2, int var3) {
      this.mEndLine = var1;
      this.mStartLine = var1;
      this.mEndColumn = var2;
      this.mStartColumn = var2;
      this.mEndOffset = var3;
      this.mStartOffset = var3;
   }

   public SourcePosition(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.mStartLine = var1;
      this.mStartColumn = var2;
      this.mStartOffset = var3;
      this.mEndLine = var4;
      this.mEndColumn = var5;
      this.mEndOffset = var6;
   }

   protected SourcePosition(SourcePosition var1) {
      this.mStartLine = var1.getStartLine();
      this.mStartColumn = var1.getStartColumn();
      this.mStartOffset = var1.getStartOffset();
      this.mEndLine = var1.getEndLine();
      this.mEndColumn = var1.getEndColumn();
      this.mEndOffset = var1.getEndOffset();
   }

   public boolean equals(Object var1) {
      boolean var2 = true;
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof SourcePosition)) {
         return false;
      } else {
         SourcePosition var3 = (SourcePosition)var1;
         if (var3.mStartLine != this.mStartLine || var3.mStartColumn != this.mStartColumn || var3.mStartOffset != this.mStartOffset || var3.mEndLine != this.mEndLine || var3.mEndColumn != this.mEndColumn || var3.mEndOffset != this.mEndOffset) {
            var2 = false;
         }

         return var2;
      }
   }

   public int getEndColumn() {
      return this.mEndColumn;
   }

   public int getEndLine() {
      return this.mEndLine;
   }

   public int getEndOffset() {
      return this.mEndOffset;
   }

   public int getStartColumn() {
      return this.mStartColumn;
   }

   public int getStartLine() {
      return this.mStartLine;
   }

   public int getStartOffset() {
      return this.mStartOffset;
   }

   public int hashCode() {
      return Objects.hashCode(new Object[]{this.mStartLine, this.mStartColumn, this.mStartOffset, this.mEndLine, this.mEndColumn, this.mEndOffset});
   }

   public String toString() {
      if (this.mStartLine == -1) {
         return "?";
      } else {
         StringBuilder var1 = new StringBuilder(15);
         var1.append(this.mStartLine + 1);
         if (this.mStartColumn != -1) {
            var1.append(':');
            var1.append(this.mStartColumn + 1);
         }

         if (this.mEndLine != -1) {
            if (this.mEndLine == this.mStartLine) {
               if (this.mEndColumn != -1 && this.mEndColumn != this.mStartColumn) {
                  var1.append('-');
                  var1.append(this.mEndColumn + 1);
               }
            } else {
               var1.append('-');
               var1.append(this.mEndLine + 1);
               if (this.mEndColumn != -1) {
                  var1.append(':');
                  var1.append(this.mEndColumn + 1);
               }
            }
         }

         return var1.toString();
      }
   }
}