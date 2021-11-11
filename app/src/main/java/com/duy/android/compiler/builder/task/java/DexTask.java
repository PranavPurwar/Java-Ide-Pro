package com.duy.android.compiler.builder.task.java;

import android.support.annotation.NonNull;
import android.util.Log;

import com.duy.android.compiler.builder.IBuilder;
import com.duy.android.compiler.builder.task.Task;
import com.duy.android.compiler.builder.util.MD5Hash;
import com.duy.android.compiler.builder.util.Argument;
import com.duy.android.compiler.project.JavaProject;
import com.duy.dex.Dex;
import com.duy.dx.merge.CollisionPolicy;
import com.duy.dx.merge.DexMerger;
import com.duy.dx.command.dexer.DxContext;
import com.duy.dx.command.dexer.Main;

import java.lang.reflect.Method;
import java.io.File;
import java.util.List;
import java.util.Arrays;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;

public class DexTask extends Task<JavaProject> {
    private static final String TAG = "Dexer";

    public DexTask(IBuilder<? extends JavaProject> builder) {
        super(builder);
    }

    @Override
    public String getTaskName() {
        return "Dx";
    }

    @Override
    public boolean doFullTaskAction() throws Exception {
        Log.d(TAG, "convertToDexFormat() called with: projectFile = [" + mProject + "]");

        mBuilder.stdout("Android dx");

        if (!dexLibs(mProject)) {
            return false;
        }
        if (!dexBuildClasses(mProject)) {
            return false;
        }
        if (!dexMerge(mProject)) {
            return false;
        }
        return true;
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
                               "--debug",
                "--verbose",
                               "--output=" + dexLib.getAbsolutePath(),
                                       jarLib.getAbsolutePath()
            );

            mBuilder.stdout("Dexing library " + jarLib.getPath() + " => " + dexLib.getAbsolutePath());
            try {
                Main.clearInternTables();
                Main.Arguments arguments = new Main.Arguments();
                Method parseMethod = Main.Arguments.class.getDeclaredMethod("parse", String[].class);
                parseMethod.setAccessible(true);
                parseMethod.invoke(arguments, (Object) args.toArray(new String[0]));

                Main.run(arguments);
            } catch (Exception e) {
                mBuilder.stdout(e.getMessage());
                return false;
            }
            mBuilder.stdout("Dexed library " + dexLib.getAbsolutePath());
        }

        mBuilder.stdout("Dex libraries completed");
        return true;
    }

    /**
     * Merge all classed has been build by {@link CompileJavaTask} to a single file .dex
     */
    private boolean dexBuildClasses(@NonNull JavaProject project) throws IOException {
        mBuilder.stdout("Merging build classes");

        List<String> args = Arrays.asList(
                                "--debug",
                                "--verbose",
                                "--output=" + project.getDexFile().getAbsolutePath(),
                                project.getDirBuildClasses().getAbsolutePath()
        );

        try {
            Main.clearInternTables();
            Main.Arguments arguments = new Main.Arguments();
            Method parseMethod = Main.Arguments.class.getDeclaredMethod("parse", String[].class);
            parseMethod.setAccessible(true);
            parseMethod.invoke(arguments, (Object) args.toArray(new String[0]));

            Main.run(arguments);
        } catch (Exception e) {
            mBuilder.stdout(e.getMessage());
            return false;
        }
        mBuilder.stdout("Merged build classes " + project.getDexFile().getName());
        return true;
    }

    private boolean dexMerge(@NonNull JavaProject projectFile) throws IOException {
        mBuilder.stdout("Merging dex files");
        File[] dexedLibs = projectFile.getDirBuildDexedLibs().listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile() && pathname.getName().endsWith(".dex");
            }
        });
        if (dexedLibs.length >= 1) {
            for (File dexedLib : dexedLibs) {
                Dex[] toBeMerged = {new Dex(projectFile.getDexFile()), new Dex(dexedLib)};
                DexMerger dexMerger = new DexMerger(toBeMerged, CollisionPolicy.KEEP_FIRST, new DxContext());
                dexMerger.merge().writeTo(projectFile.getDexFile());
            }
        }
        mBuilder.stdout("Merged all dexed files");
        return true;
    }

}
