package com.duy.common.interfaces;


import android.support.annotation.Nullable;

public interface Action<T> {
    void execute(@Nullable T t);
}
