package com.duy.android.compiler.builder.task.java;

import android.support.annotation.NonNull;
import android.util.Log;

import com.duy.android.compiler.builder.IBuilder;
import com.duy.android.compiler.builder.task.Task;
import com.duy.android.compiler.builder.util.MD5Hash;
import com.duy.android.compiler.env.Environment;
import com.duy.android.compiler.project.JavaProject;
import com.android.tools.r8.D8;

import java.io.File;
import java.util.List;
import java.util.Arrays;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;

public class D8Task extends Task<JavaProject> {
    private static final String TAG = "Dexer";

    public D8Task(IBuilder<? extends JavaProject> builder) {
        super(builder);
    }

    @Override
    public String getTaskName() {
        return "D8";
    }

    @Override
    public boolean doFullTaskAction() throws Exception {
        Log.d(TAG, "convertToDexFormat() called with: projectFile = [" + mProject + "]");

        mBuilder.stdout("Android D8");

        if (!dexLibs(mProject)) {
            return false;
        }
        if (!dexBuildClasses(mProject)) {
            return false;
        }
        return dexMerge(mProject);
    }

    private boolean dexLibs(@NonNull JavaProject project) throws Exception {
        mBuilder.stdout("Dexing libraries");
        ArrayList<File> javaLibraries = project.getJavaLibraries();
        for (File jarLib : javaLibraries) {
            // compare hash of jar contents to name of dexed version
            String md5 = MD5Hash.getMD5Checksum(jarLib);

            File dexLib = new File(project.getDirBuildDexedLibs(), jarLib.getName().replace(".jar", "-" + md5 + ".dex"));
            if (dexLib.exists()) {
                mBuilder.stdout("Dexing of library " + jarLib.getPath() + " has been skipped. Using it's previously dexed version \"" + dexLib.getName() + "\" instead to speed up compilation.");
                continue;
            }

            List<String> args = Arrays.asList(
                                       "--lib",
                                       Environment.getClasspathFile(context).getAbsolutePath(),
                                       "--release",
                                       "--output",
                                       dexLib.getAbsolutePath(),
                                       jarLib.getAbsolutePath()
            );

            mBuilder.stdout("Dexing library " + jarLib.getPath() + " => " + dexLib.getAbsolutePath());
            try {
                D8.main(args.toArray(new String[0]));
            } catch (Throwable e) {
                mBuilder.stdout(e.getMessage());
                return false;
            }
            mBuilder.stdout("Dexed library " + dexLib.getAbsolutePath());
        }

        mBuilder.stdout("Dexing libraries completed");
        return true;
    }

    /**
     * Merge all classed has been build by {@link CompileJavaTask} to a single file .dex
     */
    private boolean dexBuildClasses(@NonNull JavaProject project) throws IOException {
        mBuilder.stdout("Dexing build classes");

        List<String> args = Arrays.asList(
                                "--lib",
                                Environment.getClasspathFile(context).getAbsolutePath(),
                                "--release",
                                "--output",
                                project.getDexFile().getAbsolutePath(),
                                project.getDirBuildClasses().getAbsolutePath()
        );

        try {
            D8.main(args.toArray(new String[0]));
        } catch (Throwable e) {
            mBuilder.stdout(e.getMessage());
            return false;
        }
        mBuilder.stdout("Dexed build classes " + project.getDexFile().getName());
        return true;
    }

    private boolean dexMerge(@NonNull JavaProject project) throws IOException {
        mBuilder.stdout("Merging dex files");
        File[] dexedLibs = project.getDirBuildDexedLibs().listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().endsWith(".dex");
            }
        });
        if (dexedLibs.length >= 1) {
            for (File dexedLib : dexedLibs) {
                List<String> args = Arrays.asList(
				                       dexedLib.getAbsolutePath(),
									   project.getDexFile().getAbsolutePath(),
									   "--output",
									   project.getDexFile().getAbsolutePath()
				);
				try {
				    D8.main(args.toArray(new String[0]));
				} catch (Throwable e) {
				    mBuilder.stdout(e.getMessage());
				}
            }
        }
        mBuilder.stdout("Merged all dexed files");
        return true;
    }

}
