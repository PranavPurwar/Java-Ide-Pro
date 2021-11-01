/* Decompiler 10ms, total 551ms, lines 22 */
package com.duy.android.compiler.builder.task.java;

import com.duy.android.compiler.builder.IBuilder;
import com.duy.android.compiler.builder.task.Task;
import com.duy.android.compiler.java.JarArchive;
import com.duy.android.compiler.project.JavaProject;

public class JarTask extends Task<JavaProject> {
   public JarTask(IBuilder<JavaProject> var1) {
      super(var1);
   }

   public boolean doFullTaskAction() throws Exception {
      (new JarArchive(this.mBuilder.isVerbose())).createJarArchive(this.mProject);
      return true;
   }

   public String getTaskName() {
      return "Create jar archive";
   }
}