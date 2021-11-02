package com.duy.android.compiler.builder.sdk;

import com.android.annotations.NonNull;

import java.io.File;

/**
 * General information about the SDK.
 */
public class SdkInfo {

    @NonNull
    private final File mAnnotationJar;
    @NonNull
    private final File mAdb;
    @NonNull
    private final File mZipAlign;

    SdkInfo(@NonNull File annotationJar,
            @NonNull File adb,
            @NonNull File zipAlign) {
        mAnnotationJar = annotationJar;
        mAdb = adb;
        mZipAlign = zipAlign;
    }

    /**
     * Returns the location of the annotations jar for compilation targets that are <= 15.
     */
    @NonNull
    public File getAnnotationsJar() {
        return mAnnotationJar;
    }

    /**
     * Returns the location of the adb tool.
     */
    @NonNull
    public File getAdb() {
        return mAdb;
    }

    /**
     * Returns the location of the zip align tool.
     */
    @NonNull
    public File getZipAlign() {
        return mZipAlign;
    }
}
