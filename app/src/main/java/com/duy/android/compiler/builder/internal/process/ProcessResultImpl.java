package com.duy.android.compiler.builder.internal.process;

import com.android.ide.common.process.ProcessException;
import com.android.ide.common.process.ProcessResult;

/**
 * Internal implementation of ProcessResult used by DefaultProcessExecutor.
 */
class ProcessResultImpl implements ProcessResult {

    private final String mCommand;
    private final int mExitValue;
    private final Exception mFailure;

    ProcessResultImpl(String command, int exitValue) {
        this(command, exitValue, null);
    }

    ProcessResultImpl(String command, Exception failure) {
        this(command, -1, failure);
    }

    ProcessResultImpl(String command, int exitValue, Exception failure) {
        mCommand = command;
        mExitValue = exitValue;
        mFailure = failure;
    }

    @Override
    public ProcessResult assertNormalExitValue() throws ProcessException {
        if (mExitValue != 0) {
            throw new ProcessException(
                    String.format("Return code %d for process '%s'", mExitValue, mCommand));
        }

        return this;
    }

    @Override
    public int getExitValue() {
        return mExitValue;
    }

    @Override
    public ProcessResult rethrowFailure() throws ProcessException {
        if (mFailure != null) {
            throw new ProcessException("", mFailure);
        }

        return this;
    }
}
