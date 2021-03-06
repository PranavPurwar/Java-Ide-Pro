package com.duy.android.compiler.builder.task.java;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.duy.android.compiler.builder.IBuilder;
import com.duy.android.compiler.builder.internal.CompileOptions;
import com.duy.android.compiler.builder.internal.JavaVersion;
import com.duy.android.compiler.builder.task.Task;
import com.duy.android.compiler.java.Javac;
import com.duy.android.compiler.builder.util.Argument;
import com.duy.android.compiler.project.JavaProject;
import com.duy.javacompiler.R;

import org.eclipse.jdt.internal.compiler.batch.Main;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Locale;

import javax.tools.*;

public class JavacTask extends Task<JavaProject> {

    private static final String TAG = "JavacTask";
    private CompileOptions mCompileOptions;

    public JavacTask(IBuilder<? extends JavaProject> builder) {
        super(builder);
    }

    @Override
    public String getTaskName() {
        return "Compile java source with javac";
    }

    public boolean doFullTaskAction() {
        loadCompilerOptions();
        return runJavac();
    }

    private void loadCompilerOptions() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        mCompileOptions = new CompileOptions();

        String sourceCompatibility = pref.getString(context.getString(R.string.key_pref_source_compatibility), null);
        if (sourceCompatibility == null || sourceCompatibility.isEmpty()) {
            sourceCompatibility = JavaVersion.VERSION_1_7.toString();
        }
        mCompileOptions.setSourceCompatibility(sourceCompatibility);


        String targetCompatibility = pref.getString(context.getString(R.string.key_pref_target_compatibility), null);
        if (targetCompatibility == null || targetCompatibility.isEmpty()) {
            targetCompatibility = JavaVersion.VERSION_1_7.toString();
        }
        if (targetCompatibility == JavaVersion.VERSION_1_8.toString()) {
            targetCompatibility = JavaVersion.VERSION_1_7.toString();
            new PrintWriter(mBuilder.getStdout()).write("Java 8 is not compatible with this version of javac, you can choose java 7 or switch the compiler to ecj");
        }
            
        mCompileOptions.setTargetCompatibility(targetCompatibility);

        String encoding = pref.getString(context.getString(R.string.key_pref_source_encoding), null);
        if (encoding == null || encoding.isEmpty()) {
            encoding = Charset.forName("UTF-8").toString();
        } else {
            try {
                Charset charset = Charset.forName(encoding);
                encoding = charset.toString();
            } catch (Exception e) {
                encoding = Charset.forName("UTF-8").toString();
            }
        }
        mCompileOptions.setEncoding(encoding);
    }


    private boolean runJavac() {
        mBuilder.stdout(TAG + ": Compile java with javac");
        PrintWriter outWriter = new PrintWriter(mBuilder.getStdout());
        PrintWriter errWriter = new PrintWriter(mBuilder.getStderr());
        Argument argument = new Argument();
        argument.add(mBuilder.isVerbose() ? "-verbose" : "-warn:");
        argument.add("-classpath", mBuilder.getBootClassPath() + ":" + mProject.getClasspath());
        argument.add("-source", mCompileOptions.getSourceCompatibility().toString()); //host
        argument.add("-target", mCompileOptions.getTargetCompatibility().toString()); //target
        argument.add("-proc:none"); // Disable annotation processors...
        argument.add("-d", mProject.getDirBuildClasses().getAbsolutePath()); // The location of the output folder

        String[] sourceFiles = getAllSourceFiles(mProject);
        argument.add(sourceFiles);

        System.out.println(TAG + ": Compiler arguments " + argument);
        int n = Javac.compile(argument.toArray(), errWriter);
        return n == 0;
    }

    private String[] getAllSourceFiles(JavaProject project) {
        ArrayList<String> javaFiles = new ArrayList<>();
        String[] sourcePaths = project.getSourcePath().split(File.pathSeparator);
        for (String sourcePath : sourcePaths) {
            getAllSourceFiles(javaFiles, new File(sourcePath));
        }

        System.out.println("source size: " + javaFiles.size());
        String[] sources = new String[javaFiles.size()];
        return javaFiles.toArray(sources);
    }

    private void getAllSourceFiles(ArrayList<String> toAdd, File parent) {
        if (!parent.exists()) {
            return;
        }
        for (File child : parent.listFiles()) {
            if (child.isDirectory()) {
                getAllSourceFiles(toAdd, child);
            } else if (child.exists() && child.isFile()) {
                if (child.getName().endsWith(".java")) {
                    toAdd.add(child.getAbsolutePath());
                }
            }
        }
    }

}
