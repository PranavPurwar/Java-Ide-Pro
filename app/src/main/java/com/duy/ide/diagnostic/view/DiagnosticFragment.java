package com.duy.ide.diagnostic.view;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ScrollView;
import com.duy.ide.diagnostic.DiagnosticClickListener;
import com.duy.ide.diagnostic.DiagnosticContract.Presenter;
import com.duy.ide.diagnostic.DiagnosticContract.View;
import com.duy.ide.diagnostic.model.Message;
import com.duy.ide.diagnostic.widget.LogView;
import com.duy.ide.editor.editor.R.id;
import com.duy.ide.editor.editor.R.layout;
import java.util.ArrayList;
import java.util.List;

public class DiagnosticFragment extends Fragment implements View, DiagnosticClickListener {
   private static final String KEY_LIST_DIAGNOSTIC = "KEY_LIST_DIAGNOSTIC";
   private static final String KEY_LOG = "KEY_LOG";
   private static final String TAG = "DiagnosticFragment";
   private DiagnosticsAdapter mAdapter;
   private RecyclerView mDiagnosticView;
   private final Handler mHandler = new Handler();
   private ScrollView mLogScroller;
   private LogView mLogView;
   private Presenter mPresenter;
   private ViewPager mViewPager;

   public static DiagnosticFragment newInstance() {
      return new DiagnosticFragment();
   }

   public void addMessage(final Message message) {
      this.mHandler.post(new Runnable() {
         public void run() {
            DiagnosticFragment.this.mAdapter.add(message);
         }
      });
   }

   public void addMessage(final List<Message> messages) {
      this.mHandler.post(new Runnable() {
         public void run() {
            DiagnosticFragment.this.mAdapter.addAll(messages);
         }
      });
   }

   public void clearAll() {
      this.mHandler.post(new Runnable() {
         public void run() {
            DiagnosticFragment.this.mAdapter.clear();
            DiagnosticFragment.this.mLogView.setText("");
         }
      });
   }

   @Nullable
   public android.view.View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup group, @Nullable Bundle bundle) {
      return inflater.inflate(layout.fragment_diagnostic_default, group, false);
   }

   public void onDiagnosisClick(Message message, android.view.View view) {
      if (this.mPresenter != null) {
         this.mPresenter.onDiagnosticClick(view, message);
      }

   }

   public void onSaveInstanceState(@NonNull Bundle bundle) {
      super.onSaveInstanceState(bundle);
      bundle.putSerializable("KEY_LIST_DIAGNOSTIC", new ArrayList(this.mAdapter.getDiagnostics()));
      bundle.putString("KEY_LOG", this.mLogView.getText().toString());
   }

   public void onViewCreated(@NonNull android.view.View view, @Nullable Bundle bundle) {
      super.onViewCreated(view, bundle);
      this.mViewPager = (ViewPager)view.findViewById(id.diagnostic_view_pager);
      this.mViewPager.setAdapter(new DiagnosticPagerAdapter(this));
      this.mViewPager.setOffscreenPageLimit(this.mViewPager.getAdapter().getCount());
      this.mLogView = (LogView)view.findViewById(id.txt_log);
      this.mLogView.setSaveEnabled(false);
      this.mLogScroller = (ScrollView)view.findViewById(id.compiler_output_container);
      this.mDiagnosticView = (RecyclerView)view.findViewById(id.diagnostic_list_view);
      this.mDiagnosticView.setLayoutManager(new LinearLayoutManager(this.getContext()));
      this.mDiagnosticView.addItemDecoration(new DividerItemDecoration(this.getContext(), 1));
      this.mAdapter = new DiagnosticsAdapter(new ArrayList(), this.getContext());
      this.mAdapter.setDiagnosticClickListener(this);
      this.mDiagnosticView.setAdapter(this.mAdapter);
      if (bundle != null) {
         this.showDiagnostic((ArrayList)bundle.getSerializable("KEY_LIST_DIAGNOSTIC"));
         this.printMessage(bundle.getString("KEY_LOG"));
      }

   }

   public void printError(final String err) {
      this.mHandler.post(new Runnable() {
         public void run() {
            DiagnosticFragment.this.mLogView.append(err);
            DiagnosticFragment.this.mLogScroller.fullScroll(130);
            if (DiagnosticFragment.this.mAdapter.getDiagnostics().isEmpty()) {
               DiagnosticFragment.this.mViewPager.setCurrentItem(1);
            }

         }
      });
   }

   public void printMessage(final String message) {
      this.mHandler.post(new Runnable() {
         public void run() {
            DiagnosticFragment.this.mLogView.append(message);
            DiagnosticFragment.this.mLogScroller.fullScroll(130);
         }
      });
   }

   public void removeMessage(final Message message) {
      this.mHandler.post(new Runnable() {
         public void run() {
            DiagnosticFragment.this.mAdapter.remove(message);
         }
      });
   }

   public void setCurrentItem(int pos) {
      if (this.mViewPager != null) {
         this.mViewPager.setCurrentItem(pos);
      }

   }

   public void setPresenter(Presenter presenter) {
      this.mPresenter = presenter;
   }

   public void showDiagnostic(final List<Message> messages) {
      this.mHandler.post(new Runnable() {
         public void run() {
            DiagnosticFragment.this.mAdapter.setData(messages);
            if (!messages.isEmpty()) {
               DiagnosticFragment.this.mViewPager.setCurrentItem(0);
            }

         }
      });
   }
}