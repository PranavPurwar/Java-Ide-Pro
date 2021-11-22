package com.pluscubed.logcat.databinding;

import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.pluscubed.logcat.R;

public class ActivityLogcatBinding extends ViewDataBinding {
    @Nullable
    private static final ViewDataBinding.IncludedLayouts sIncludes = null;
    @Nullable
    private static final SparseIntArray sViewsWithIds = new SparseIntArray();
    @NonNull
    public final FloatingActionButton fab;
    @NonNull
    public final RecyclerView list;
    private long mDirtyFlags = -1;
    @NonNull
    public final LinearLayout mainBackground;
    @NonNull
    public final ProgressBar mainDarkProgressBar;
    @NonNull
    public final ProgressBar mainLightProgressBar;
    @NonNull
    public final Toolbar toolbarActionbar;

    protected boolean onFieldChange(int i, Object obj, int i2) {
        return false;
    }

    public boolean setVariable(int i, @Nullable Object obj) {
        return true;
    }

    static {
        sViewsWithIds.put(R.id.toolbar_actionbar, 1);
        sViewsWithIds.put(R.id.main_dark_progress_bar, 2);
        sViewsWithIds.put(R.id.main_light_progress_bar, 3);
        sViewsWithIds.put(R.id.list, 4);
        sViewsWithIds.put(R.id.fab, 5);
    }

    public ActivityLogcatBinding(@NonNull DataBindingComponent dataBindingComponent, @NonNull View view) {
        super(dataBindingComponent, view, 0);
        Object[] mapBindings = mapBindings(dataBindingComponent, view, 6, sIncludes, sViewsWithIds);
        this.fab = (FloatingActionButton) mapBindings[5];
        this.list = (RecyclerView) mapBindings[4];
        this.mainBackground = (LinearLayout) mapBindings[0];
        this.mainBackground.setTag((Object) null);
        this.mainDarkProgressBar = (ProgressBar) mapBindings[2];
        this.mainLightProgressBar = (ProgressBar) mapBindings[3];
        this.toolbarActionbar = (Toolbar) mapBindings[1];
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
            return this.mDirtyFlags != 0;
        }
    }

    protected void executeBindings() {
        synchronized (this) {
            this.mDirtyFlags = 0;
        }
    }

    @NonNull
    public static ActivityLogcatBinding inflate(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, boolean z) {
        return inflate(layoutInflater, viewGroup, z, DataBindingUtil.getDefaultComponent());
    }

    @NonNull
    public static ActivityLogcatBinding inflate(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, boolean z, @Nullable DataBindingComponent dataBindingComponent) {
        return DataBindingUtil.inflate(layoutInflater, R.layout.activity_logcat, viewGroup, z, dataBindingComponent);
    }

    @NonNull
    public static ActivityLogcatBinding inflate(@NonNull LayoutInflater layoutInflater) {
        return inflate(layoutInflater, DataBindingUtil.getDefaultComponent());
    }

    @NonNull
    public static ActivityLogcatBinding inflate(@NonNull LayoutInflater layoutInflater, @Nullable DataBindingComponent dataBindingComponent) {
        return bind(layoutInflater.inflate(R.layout.activity_logcat, (ViewGroup) null, false), dataBindingComponent);
    }

    @NonNull
    public static ActivityLogcatBinding bind(@NonNull View view) {
        return bind(view, DataBindingUtil.getDefaultComponent());
    }

    @NonNull
    public static ActivityLogcatBinding bind(@NonNull View view, @Nullable DataBindingComponent dataBindingComponent) {
        if (view.getTag().equals("layout/activity_logcat_0")) {
            return new ActivityLogcatBinding(dataBindingComponent, view);
        }
        throw new IllegalArgumentException("view tag isn't correct on view:" + view.getTag());
    }
}
