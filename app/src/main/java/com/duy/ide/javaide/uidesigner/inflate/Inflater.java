package com.duy.ide.javaide.uidesigner.inflate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.duy.ide.javaide.uidesigner.dynamiclayoutinflator.DynamicLayoutInflator;

import java.io.File;
import java.io.FileInputStream;


public class Inflater {
    private final Object delegate;
    private ViewGroup container;
    private TextView txtError;
    private LayoutInflater layoutInflater;
    private Context context;

    public Inflater(Context context, Object delegate, ViewGroup container, TextView txtError) {
        this.context = context;
        this.delegate = delegate;
        this.container = container;
        this.layoutInflater = LayoutInflater.from(context);
        this.txtError = txtError;
    }

    public void inflate(File file) {
        try {
            View view = DynamicLayoutInflator.inflate(context, new FileInputStream(file), container);
            DynamicLayoutInflator.setDelegate(view, delegate);
            container.removeAllViews();
            container.addView(view, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT));
        } catch (Exception e) {
            e.printStackTrace();
            txtError.setText(e.getMessage());
            txtError.setVisibility(View.VISIBLE);
        }
    }
}
