package com.duy.android.compiler.gradleapi;

/**
 * Interface of Task configuration Actions.
 */
public interface TaskConfigAction<T> extends Action<T> {

    /**
     * Return the name of the task to be configured.
     */
    String getName();

    /**
     * Return the class type of the task to be configured.
     */
    Class<T> getType();
}
