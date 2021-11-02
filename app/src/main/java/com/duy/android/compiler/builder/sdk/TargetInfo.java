package com.duy.android.compiler.builder.sdk;

import com.android.annotations.NonNull;
import com.android.sdklib.BuildToolInfo;
import com.android.sdklib.IAndroidTarget;

/**
 * The SDK Target information needed to build a project.
 */
public class TargetInfo {

    @NonNull
    private final IAndroidTarget mTarget;
    @NonNull
    private final BuildToolInfo mBuildToolInfo;

    TargetInfo(
            @NonNull IAndroidTarget target,
            @NonNull BuildToolInfo buildToolInfo) {

        mTarget = target;
        mBuildToolInfo = buildToolInfo;
    }

    /**
     * Returns the compilation target
     * @return the target.
     */
    @NonNull
    public IAndroidTarget getTarget() {
        return mTarget;
    }

    /**
     * Returns the BuildToolInfo
     * @return the build tool info
     */
    @NonNull
    public BuildToolInfo getBuildTools() {
        return mBuildToolInfo;
    }
}