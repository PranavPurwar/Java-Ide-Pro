package com.duy.dx .util;

import com.duy.dex.util.ExceptionWithContext;

/**
 * Exception due to a mutability problem.
 */
public class MutabilityException
        extends ExceptionWithContext {
    public MutabilityException(String message) {
        super(message);
    }

    public MutabilityException(Throwable cause) {
        super(cause);
    }

    public MutabilityException(String message, Throwable cause) {
        super(message, cause);
    }
}
