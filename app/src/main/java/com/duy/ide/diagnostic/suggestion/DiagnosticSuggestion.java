package com.duy.ide.diagnostic.suggestion;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import java.io.File;

public class DiagnosticSuggestion implements Parcelable, ISuggestion {
   public static final Creator<DiagnosticSuggestion> CREATOR = new Creator<DiagnosticSuggestion>() {
      public DiagnosticSuggestion createFromParcel(Parcel var1) {
         return new DiagnosticSuggestion(var1);
      }

      public DiagnosticSuggestion[] newArray(int var1) {
         return new DiagnosticSuggestion[var1];
      }
   };
   public final int colEnd;
   public final int colStart;
   public final String filePath;
   public final int lineEnd;
   public final int lineStart;
   public final String suggestion;

   protected DiagnosticSuggestion(Parcel parcel) {
      this.filePath = parcel.readString();
      this.lineStart = parcel.readInt();
      this.colStart = parcel.readInt();
      this.lineEnd = parcel.readInt();
      this.colEnd = parcel.readInt();
      this.suggestion = parcel.readString();
   }

   public DiagnosticSuggestion(String path, int start, int colStart, int end, int colEnd, String suggestion) {
      this.filePath = path;
      this.lineStart = start;
      this.colStart = colStart;
      this.lineEnd = end;
      this.colEnd = colEnd;
      this.suggestion = suggestion;
   }

   public int describeContents() {
      return 0;
   }

   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof DiagnosticSuggestion)) {
         return false;
      } else {
         DiagnosticSuggestion suggest = (DiagnosticSuggestion)obj;
         if (this.getLineStart() != suggest.getLineStart() ||
                  this.getColStart() != suggest.getColStart() ||
                  this.getLineEnd() != suggest.getLineEnd() ||
                  this.getColEnd() != suggest.getColEnd()) 
         {
            return false;
         } else {
            if (this.getSourceFile() != null) {
               if (!this.getSourceFile().equals(suggest.getSourceFile())) {
                  return false;
               }
            } else if (suggest.getSourceFile() != null) {
               return false;
            }

            if (this.getMessage() != null) {
               return this.getMessage().equals(suggest.getMessage());
            } else if (suggest.getMessage() != null) {
               return false;
            }
         }
      }
      return false;
   }

   public int getColEnd() {
      return this.colEnd;
   }

   public int getColStart() {
      return this.colStart;
   }

   public int getLineEnd() {
      return this.lineEnd;
   }

   public int getLineStart() {
      return this.lineStart;
   }

   @NonNull
   public String getMessage() {
      return this.suggestion;
   }

   public File getSourceFile() {
      return new File(this.filePath);
   }

   public int hashCode() {
      File source = this.getSourceFile();
      int messageHash = 0;
      int sourceHash;
      if (source != null) {
         sourceHash = source.hashCode();
      } else {
         sourceHash = 0;
      }

      int start = this.getLineStart();
      int colStart = this.getColStart();
      int end = this.getLineEnd();
      int colEnd = this.getColEnd();
      if (this.getMessage() != null) {
         messageHash = this.getMessage().hashCode();
      }

      return ((((sourceHash * 31 + start) * 31 + colStart) * 31 + end) * 31 + colEnd) * 31 + messageHash;
   }

   public String toString() {
      return (
              "DiagnosticSuggestion{filePath='" +
                     this.filePath +
                     '\'' +
                     ", lineStart=" +
                     this.lineStart +
                     ", colStart=" +
                     this.colStart +
                     ", lineEnd=" +
                     this.lineEnd +
                     ", colEnd=" +
                     this.colEnd +
                     ", suggestion='" +
                     this.suggestion +
                     '\'' +
                     '}'
             );
   }

   public void writeToParcel(Parcel parcel, int unused) {
      parcel.writeString(this.filePath);
      parcel.writeInt(this.lineStart);
      parcel.writeInt(this.colStart);
      parcel.writeInt(this.lineEnd);
      parcel.writeInt(this.colEnd);
      parcel.writeString(this.suggestion);
   }
}