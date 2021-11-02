package com.duy.android.compiler.builder.internal.dependency;

import com.android.annotations.NonNull;
import com.android.builder.dependency.LibraryDependency;
import com.android.builder.dependency.SymbolFileProvider;

import java.io.File;

/**
 * Implementation of SymbolFileProvider that can be used as a Task input.
 */
public class SymbolFileProviderImpl implements SymbolFileProvider {

    private final File manifest;
    private final File symbolFile;
    private final boolean isOptional;

    public SymbolFileProviderImpl(@NonNull LibraryDependency library) {
        manifest = library.getManifest();
        symbolFile = library.getSymbolFile();
        isOptional = library.isOptional();
    }

    @Override
    @NonNull
    public File getManifest() {
        return manifest;
    }

    @Override
    @NonNull
    public File getSymbolFile() {
        return symbolFile;
    }

    @Override
    public boolean isOptional() {
        return isOptional;
    }
}
