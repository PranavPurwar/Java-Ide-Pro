package com.pluscubed.logcat.databinding;

import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.pluscubed.logcat.R;

public class ListItemLogcatBinding extends ViewDataBinding {
    @Nullable
    private static final ViewDataBinding.IncludedLayouts sIncludes = null;
    @Nullable
    private static final SparseIntArray sViewsWithIds = new SparseIntArray();
    @NonNull
    public final TextView logLevelText;
    @NonNull
    public final TextView logOutputText;
    private long mDirtyFlags = -1;
    @NonNull
    private final RelativeLayout mboundView0;
    @NonNull
    public final TextView pidText;
    @NonNull
    public final TextView tagText;
    @NonNull
    public final TextView timestampText;

    protected boolean onFieldChange(int i, Object obj, int i2) {
        return false;
    }

    public boolean setVariable(int i, @Nullable Object obj) {
        return true;
    }

    static {
        sViewsWithIds.put(R.id.pid_text, 1);
        sViewsWithIds.put(R.id.timestamp_text, 2);
        sViewsWithIds.put(R.id.tag_text, 3);
        sViewsWithIds.put(R.id.log_level_text, 4);
        sViewsWithIds.put(R.id.log_output_text, 5);
    }

    public ListItemLogcatBinding(@NonNull DataBindingComponent dataBindingComponent, @NonNull View view) {
        super(dataBindingComponent, view, 0);
        Object[] mapBindings = mapBindings(dataBindingComponent, view, 6, sIncludes, sViewsWithIds);
        this.logLevelText = (TextView) mapBindings[4];
        this.logOutputText = (TextView) mapBindings[5];
        this.mboundView0 = (RelativeLayout) mapBindings[0];
        this.mboundView0.setTag((Object) null);
        this.pidText = (TextView) mapBindings[1];
        this.tagText = (TextView) mapBindings[3];
        this.timestampText = (TextView) mapBindings[2];
        setRootTag(view);
        invalidateAll();
    }

    public void invalidateAll() {
        synchronized (this) {
            this.mDirtyFlags = 1;
        }
        requestRebind();
    }

    public boolean hasPendingBindings() {
        synchronized (this) {
            if (this.mDirtyFlags != 0) {
                return true;
            }
            return false;
        }
    }

    protected void executeBindings() {
        synchronized (this) {
            long j = this.mDirtyFlags;
            this.mDirtyFlags = 0;
        }
    }

    @NonNull
    public static ListItemLogcatBinding inflate(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, boolean z) {
        return inflate(layoutInflater, viewGroup, z, DataBindingUtil.getDefaultComponent());
    }

    @NonNull
    public static ListItemLogcatBinding inflate(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, boolean z, @Nullable DataBindingComponent dataBindingComponent) {
        return DataBindingUtil.inflate(layoutInflater, R.layout.list_item_logcat, viewGroup, z, dataBindingComponent);
    }

    @NonNull
    public static ListItemLogcatBinding inflate(@NonNull LayoutInflater layoutInflater) {
        return inflate(layoutInflater, DataBindingUtil.getDefaultComponent());
    }

    @NonNull
    public static ListItemLogcatBinding inflate(@NonNull LayoutInflater layoutInflater, @Nullable DataBindingComponent dataBindingComponent) {
        return bind(layoutInflater.inflate(R.layout.list_item_logcat, (ViewGroup) null, false), dataBindingComponent);
    }

    @NonNull
    public static ListItemLogcatBinding bind(@NonNull View view) {
        return bind(view, DataBindingUtil.getDefaultComponent());
    }

    @NonNull
    public static ListItemLogcatBinding bind(@NonNull View view, @Nullable DataBindingComponent dataBindingComponent) {
        if ("layout/list_item_logcat_0".equals(view.getTag())) {
            return new ListItemLogcatBinding(dataBindingComponent, view);
        }
        throw new RuntimeException("view tag isn't correct on view:" + view.getTag());
    }
}
