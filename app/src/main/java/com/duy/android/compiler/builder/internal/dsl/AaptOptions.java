package com.duy.android.compiler.builder.internal.dsl;

import com.android.annotations.NonNull;
import com.android.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * DSL object for configuring aapt options.
 */
public class AaptOptions implements com.android.builder.model.AaptOptions {

    @Nullable
    private String ignoreAssetsPattern;

    @Nullable
    private List<String> noCompressList;

    private boolean useNewCruncher = true;

    private boolean cruncherEnabled = true;

    private boolean failOnMissingConfigEntry = false;

    @Nullable
    private List<String> additionalParameters;

    public void setIgnoreAssetsPattern(@Nullable String ignoreAssetsPattern) {
        this.ignoreAssetsPattern = ignoreAssetsPattern;
    }

    /**
     * Pattern describing assets to be ignore.
     * <p>
     * <p>See <code>aapt --help</code>
     */
    @Override
    public String getIgnoreAssets() {
        return ignoreAssetsPattern;
    }

    public void setNoCompress(String noCompress) {
        noCompressList = Collections.singletonList(noCompress);
    }

    /**
     * Extensions of files that will not be stored compressed in the APK.
     * <p>
     * <p>Equivalent of the -0 flag. See <code>aapt --help</code>
     */
    @Override
    public Collection<String> getNoCompress() {
        return noCompressList;
    }

    public void setNoCompress(String... noCompress) {
        noCompressList = Arrays.asList(noCompress);
    }

    public void useNewCruncher(boolean value) {
        useNewCruncher = value;
    }

    /**
     * Returns true if the PNGs should be crunched, false otherwise.
     */
    public boolean getCruncherEnabled() {
        return cruncherEnabled;
    }

    /**
     * Enables or disables PNG crunching.
     */
    public void setCruncherEnabled(boolean value) {
        cruncherEnabled = value;
    }

    /**
     * Whether to use the new cruncher.
     */

    public boolean getUseNewCruncher() {
        return useNewCruncher;
    }

    public void setUseNewCruncher(boolean value) {
        useNewCruncher = value;
    }

    public void failOnMissingConfigEntry(boolean value) {
        failOnMissingConfigEntry = value;
    }

    /**
     * Forces aapt to return an error if it fails to find an entry for a configuration.
     * <p>
     * <p>See <code>aapt --help</code>
     */
    @Override

    public boolean getFailOnMissingConfigEntry() {
        return failOnMissingConfigEntry;
    }

    public void setFailOnMissingConfigEntry(boolean value) {
        failOnMissingConfigEntry = value;
    }

    // -- DSL Methods. TODO remove once the instantiator does what I expect it to do.

    /**
     * Sets extensions of files that will not be stored compressed in the APK.
     * <p>
     * <p>Equivalent of the -0 flag. See <code>aapt --help</code>
     */
    public void noCompress(String noCompress) {
        noCompressList = Collections.singletonList(noCompress);
    }

    /**
     * Sets extensions of files that will not be stored compressed in the APK.
     * <p>
     * <p>Equivalent of the -0 flag. See <code>aapt --help</code>
     */
    public void noCompress(String... noCompress) {
        noCompressList = Arrays.asList(noCompress);
    }

    public void additionalParameters(@NonNull String param) {
        additionalParameters = Collections.singletonList(param);
    }

    public void additionalParameters(String... params) {
        additionalParameters = Arrays.asList(params);
    }

    @Nullable
    @Override


    public List<String> getAdditionalParameters() {
        return additionalParameters;
    }

    public void setAdditionalParameters(@Nullable List<String> parameters) {
        additionalParameters = parameters;
    }
}
