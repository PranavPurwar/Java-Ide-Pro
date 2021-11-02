package com.duy.android.compiler.builder;

import android.content.Context;
import android.os.AsyncTask;
import com.duy.android.compiler.builder.model.BuildType;
import com.duy.android.compiler.project.JavaProject;
import java.io.File;
import java.util.List;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;

public class BuildJar extends AsyncTask<JavaProject, Object, File> {
   private Context context;
   private Exception error;
   private BuildJar.CompileListener listener;
   private DiagnosticCollector mDiagnosticCollector;

   public BuildJar(Context var1, BuildJar.CompileListener var2) {
      this.context = var1;
      this.listener = var2;
      this.mDiagnosticCollector = new DiagnosticCollector();
   }

   protected File doInBackground(JavaProject... var1) {
      JavaProject var2 = var1[0];
      if (var1[0] == null) {
         return null;
      } else {
         try {
            JavaBuilder var4 = new JavaBuilder(context, var2);
            if (var4.build(BuildType.DEBUG)) {
               File var5 = var2.getOutJarArchive();
               return var5;
            }
         } catch (Exception var3) {
            error = var3;
         }

         return null;
      }
   }

   protected void onPostExecute(File var1) {
      super.onPostExecute(var1);
      if (var1 == null) {
         listener.onError(error, mDiagnosticCollector.getDiagnostics());
      } else {
         listener.onComplete(var1, mDiagnosticCollector.getDiagnostics());
      }

   }

   protected void onPreExecute() {
      super.onPreExecute();
      listener.onStart();
   }

   public interface CompileListener {
      void onComplete(File jarFile, List<Diagnostic> var2);

      void onError(Exception var1, List<Diagnostic> var2);

      void onStart();
   }
}