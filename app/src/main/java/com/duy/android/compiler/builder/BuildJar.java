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

   public BuildJar(Context context, BuildJar.CompileListener listener) {
      this.context = context;
      this.listener = listener;
      this.mDiagnosticCollector = new DiagnosticCollector();
   }

   protected File doInBackground(JavaProject... project) {
      JavaProject mProject = project[0];
      if (project[0] == null) {
         return null;
      } else {
         try {
            JavaBuilder builder = new JavaBuilder(context, mProject);
            if (builder.build(BuildType.DEBUG)) {
               File out = mProject.getOutJarArchive();
               return out;
            }
         } catch (Exception e) {
            error = e;
         }

         return null;
      }
   }

   protected void onPostExecute(File file) {
      super.onPostExecute(file);
      if (file == null) {
         listener.onError(error, mDiagnosticCollector.getDiagnostics());
      } else {
         listener.onComplete(file, mDiagnosticCollector.getDiagnostics());
      }

   }

   protected void onPreExecute() {
      super.onPreExecute();
      listener.onStart();
   }

   public interface CompileListener {
      void onComplete(File jarFile, List<Diagnostic> diagnostic);

      void onError(Exception e, List<Diagnostic> diagnostic);

      void onStart();
   }
}