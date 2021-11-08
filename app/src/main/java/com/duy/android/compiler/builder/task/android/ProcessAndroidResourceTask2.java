package com.duy.android.compiler.builder.task.android;

import android.os.Build;

import com.android.builder.dependency.LibraryDependency;
import com.duy.android.compiler.builder.AndroidAppBuilder;
import com.duy.android.compiler.builder.task.Task;
import com.duy.android.compiler.builder.util.Argument;
import com.duy.android.compiler.env.Environment;
import com.duy.android.compiler.project.AndroidAppProject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * $ aapt
 * Android Asset Packaging Tool
 * <p>
 * Usage:
 * aapt l[ist] [-v] [-a] file.{zip,jar,apk}
 * List contents of Zip-compatible archive.
 * <p>
 * aapt d[ump] [--values] WHAT file.{apk} [asset [asset ...]]
 * badging          Print the label and icon for the app declared in APK.
 * permissions      Print the permissions from the APK.
 * resources        Print the resource table from the APK.
 * configurations   Print the configurations in the APK.
 * xmltree          Print the compiled xmls in the given assets.
 * xmlstrings       Print the strings of the given compiled xml assets.
 * <p>
 * aapt p[ackage] [-d][-f][-m][-u][-v][-x][-z][-M AndroidManifest.xml] \
 * [-0 extension [-0 extension ...]] [-g tolerance] [-j jarfile] \
 * [--min-sdk-version VAL] [--target-sdk-version VAL] \
 * [--max-sdk-version VAL] [--app-version VAL] \
 * [--app-version-name TEXT] [--custom-package VAL] \
 * [-I base-package [-I base-package ...]] \
 * [-A asset-source-dir]  [-G class-list-file] [-P public-definitions-file] \
 * [-S resource-sources [-S resource-sources ...]]         [-F apk-file] [-J R-file-dir] \
 * [raw-files-dir [raw-files-dir] ...]
 * <p>
 * Package the android resources.  It will read assets and resources that are
 * supplied with the -M -A -S or raw-files-dir arguments.  The -J -P -F and -R
 * options control which files are output.
 * <p>
 * aapt r[emove] [-v] file.{zip,jar,apk} file1 [file2 ...]
 * Delete specified files from Zip-compatible archive.
 * <p>
 * aapt a[dd] [-v] file.{zip,jar,apk} file1 [file2 ...]
 * Add specified files to Zip-compatible archive.
 * <p>
 * aapt v[ersion]
 * Print program version.
 * <p>
 * Modifiers:
 * -a  print Android-specific data (resources, manifest) when listing
 * -c  specify which configurations to include.  The default is all
 * configurations.  The value of the parameter should be a comma
 * separated list of configuration values.  Locales should be specified
 * as either a language or language-region pair.  Some examples:
 * en
 * port,en
 * port,land,en_US
 * If you put the special locale, zz_ZZ on the list, it will perform
 * pseudolocalization on the default locale, modifying all of the
 * strings so you can look for strings that missed the
 * internationalization process.  For example:
 * port,land,zz_ZZ
 * -d  one or more device assets to include, separated by commas
 * -f  force overwrite of existing files
 * -g  specify a pixel tolerance to force images to grayscale, default 0
 * -j  specify a jar or zip file containing classes to include
 * -k  junk path of file(s) added
 * -m  make package directories under location specified by -J
 * -u  update existing packages (add new, replace older, remove deleted files)
 * -v  verbose output
 * -x  create extending (non-application) resource IDs
 * -z  require localization of resource attributes marked with
 * localization="suggested"
 * -A  additional directory in which to find raw asset files
 * -G  A file to output proguard options into.
 * -F  specify the apk file to output
 * -I  add an existing package to base include set
 * -J  specify where to output R.java resource constant definitions
 * -M  specify full path to AndroidManifest.xml to include in zip
 * -P  specify where to output public resource definitions
 * -S  directory in which to find resources.  Multiple directories will be scanned
 * and the first match found (left to right) will take precedence.
 * -0  specifies an additional extension for which such files will not
 * be stored compressed in the .apk.  An empty string means to not
 * compress any files at all.
 * --min-sdk-version
 * inserts android:minSdkVersion in to manifest.
 * --target-sdk-version
 * inserts android:targetSdkVersion in to manifest.
 * --max-sdk-version
 * inserts android:maxSdkVersion in to manifest.
 * --values
 * when used with "dump resources" also includes resource values.
 * --version-code
 * inserts android:versionCode in to manifest.
 * --version-name
 * inserts android:versionName in to manifest.
 * --custom-package
 * generates R.java into a different package.
 *
 * @link https://elinux.org/Android_aapt
 * @link https://android.googlesource.com/platform/frameworks/base.git/+/master/tools/aapt/Main.cpp
 */
public class ProcessAndroidResourceTask2 extends Task<AndroidAppProject> {
    public ProcessAndroidResourceTask2(AndroidAppBuilder builder) {
        super(builder);
    }

    @Override
    public String getTaskName() {
        return "Process android resource";
    }

    public boolean doFullTaskAction() throws Exception {
        File aaptFile = getAaptFile();
        Argument args = new Argument();
        args.add(aaptFile.getAbsolutePath());
        args.add("compile");
        args.add("--dir");
        args.add(mProject.getResDirs().getAbsolutePath());
        args.add("-o");
        args.add("/storage/emulated/0/compiled");
        return execAapt(args);
    }

    private File getAaptFile() {
        return new File(Environment.getBinDir(context), "aapt2");
    }

    private boolean execAapt(Argument args) throws InterruptedException, IOException {
        final int[] exitCode = new int[1];
        final Process aaptProcess = Runtime.getRuntime().exec(args.toArray());
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    exitCode[0] = aaptProcess.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        BufferedReader reader = new BufferedReader(new InputStreamReader(aaptProcess.getInputStream()));
        do {
            try {
                String s = reader.readLine();
                if (s == null) {
                    break;
                }
                mBuilder.stdout(s);
            } catch (Exception e) {
                break;
            }
        } while (thread.isAlive());
        reader.close();
        reader = new BufferedReader(new InputStreamReader(aaptProcess.getErrorStream()));
        do {
            try {
                String s = reader.readLine();
                if (s == null) {
                    break;
                }
                mBuilder.stderr(s);
                // TODO: 03-Jun-18 improve it , use com.android.ide.common.blame.parser.aapt.AaptOutputParser
                if (s.startsWith("ERROR")) {
                    return false;
                }
            } catch (Exception e) {
                break;
            }
        } while (thread.isAlive());
        thread.join();


        mBuilder.stdout("AAPT exit code " + exitCode[0]);
        return exitCode[0] == 0;
    }

    /**
     * Gets the number of cores available in this device, across all processors.
     * Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu"
     * <p>
     * From StackOverflow: http://stackoverflow.com/a/10377934
     *
     * @return The number of cores, or Runtime.availableProcessors() if failed to get result
     */
    private int getNumCores() {
        //Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                //Check if filename is "cpu", followed by a single digit number
                if (Pattern.matches("cpu[0-9]+", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }

        try {
            //Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            //Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            //Return the number of cores (virtual CPU devices)
            return files.length;
        } catch (Exception e) {
            //Default to return 1 core
            return Runtime.getRuntime().availableProcessors();
        }
    }

    public static final class AAPTOptions {
        /**
         * android.jar
         */
        private static final String ANDROID_JAR_PATH = "androidJarPath";
        /**
         * Output directory for R.java
         */
        private static final String SOURCE_OUTPUT_DIR = "sourceOutputDir";
        /**
         * Output directory for resource.ap_
         */
        private static final String RESOURCE_OUTPUT_APK = "resourceOutputApk";
        /**
         * Public resource definition of library R.txt
         */
        private static final String LIBRARY_SYMBOL_TABLE_FILES = "librarySymbolTableFiles";
        /**
         * Output public resource definition for application
         */
        private static final String OUTPUT_TEXT_SYMBOL = "--output-text-symbols";

        private static final String VERBOSE = "verbose";

        private static final String PROGUARD_OUTPUT_FILE = "proguardOutputFile";

        private static final String MAIN_DEX_LIST_PROGUARD_OUTPUT_FILE = "mainDexListProguardOutputFile";

        private static final String customPackageForR = "customPackageForR";
    }

}