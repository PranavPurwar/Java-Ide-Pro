package com.duy.android.compiler.builder.internal.process;

import com.android.annotations.NonNull;
import com.android.ide.common.process.ProcessException;
import com.android.ide.common.process.ProcessResult;

public class FailedProcessResult implements ProcessResult {
    @NonNull
    private final ProcessException failure;

    FailedProcessResult(@NonNull ProcessException failure) {
        this.failure = failure;
    }

    @Override
    public ProcessResult assertNormalExitValue() throws ProcessException {
        throw failure;
    }

    @Override
    public int getExitValue() {
        return -1;
    }

    @Override
    public ProcessResult rethrowFailure() throws ProcessException {
        throw failure;
    }
}
