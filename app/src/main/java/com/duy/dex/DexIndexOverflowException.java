package com.duy.dex;

/**
 * Thrown when there's an index overflow writing a dex file.
 */
public final class DexIndexOverflowException extends DexException {
    public DexIndexOverflowException(String message) {
        super(message);
    }

    public DexIndexOverflowException(Throwable cause) {
        super(cause);
    }
}
