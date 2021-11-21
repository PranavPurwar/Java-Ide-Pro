package com.duy.android.compiler.builder.task.android;

import com.duy.android.compiler.builder.IBuilder;
import com.duy.android.compiler.builder.task.Task;
import com.duy.android.compiler.project.AndroidAppProject;

import com.duy.ide.javaide.utils.RootUtils;

public class InstallApkTask extends Task<AndroidAppProject> {

    public InstallApkTask(IBuilder<? extends AndroidAppProject> builder) {
        super(builder);
    }

    @Override
    public String getTaskName() {
        return "Install apk";
    }

    @Override
    public boolean doFullTaskAction() throws Exception {
        RootUtils.installApk(context, mProject.getApkSigned());
        return true;
    }
}
