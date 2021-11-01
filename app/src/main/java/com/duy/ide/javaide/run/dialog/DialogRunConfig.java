package com.duy.ide.javaide.run.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.duy.android.compiler.project.ClassFile;
import com.duy.android.compiler.project.ClassUtil;
import com.duy.android.compiler.project.JavaProject;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.apache.commons.io.FileUtils;

public class DialogRunConfig extends AppCompatDialogFragment {
   public static final String ARGS = "program_args";
   public static final String TAG = "DialogRunConfig";
   @Nullable
   private DialogRunConfig.OnConfigChangeListener listener;
   private EditText mArgs;
   private Spinner mClasses;
   private EditText mPackage;
   private SharedPreferences mPref;
   private JavaProject mProject;

   public static DialogRunConfig newInstance(JavaProject var0) {
      DialogRunConfig var1 = new DialogRunConfig();
      var1.mProject = var0;
      return var1;
   }

   private void save() {
      this.mPref = PreferenceManager.getDefaultSharedPreferences(this.getContext());
      this.mPref.edit().putString("program_args", this.mArgs.getText().toString()).apply();
      Object var1 = this.mClasses.getSelectedItem();
      if (var1 != null) {
         if (!ClassUtil.hasMainFunction(new File((new ClassFile(var1.toString())).getPath(this.mProject)))) {
            Toast.makeText(this.getContext(), "Can not find main function", 0).show();
         }

         this.mProject.setPackageName(this.mPackage.getText().toString());
         if (this.listener != null) {
            this.listener.onConfigChange(this.mProject);
         }

         this.dismiss();
      }

   }

   private void setupSpinnerMainClass(View var1, JavaProject var2) {
      ArrayList var3 = this.listClassName((File)var2.getJavaSrcDirs().get(0));
      ArrayAdapter var4 = new ArrayAdapter(this.getContext(), 17367043, var3);
      var4.setDropDownViewResource(17367055);
      this.mClasses = (Spinner)var1.findViewById(2131296668);
      this.mClasses.setAdapter(var4);
   }

   public ArrayList<String> listClassName(File var1) {
      if (!var1.exists()) {
         return new ArrayList();
      } else {
         Collection var2 = FileUtils.listFiles(var1, new String[]{"java"}, true);
         ArrayList var3 = new ArrayList();
         String var5 = var1.getPath();
         Iterator var6 = var2.iterator();

         while(var6.hasNext()) {
            String var4 = ((File)var6.next()).getPath();
            var3.add(var4.substring(var5.length() + 1, var4.length() - 5).replace(File.separator, "."));
         }

         return var3;
      }
   }

   public void onAttach(Context var1) {
      super.onAttach(var1);

      try {
         this.listener = (DialogRunConfig.OnConfigChangeListener)this.getActivity();
      } catch (ClassCastException var2) {
         var2.printStackTrace();
      }

   }

   @Nullable
   public View onCreateView(LayoutInflater var1, @Nullable ViewGroup var2, @Nullable Bundle var3) {
      return var1.inflate(2131427403, var2, false);
   }

   public void onStart() {
      super.onStart();
      Dialog var1 = this.getDialog();
      if (var1 != null) {
         Window var2 = var1.getWindow();
         if (var2 != null) {
            var2.setLayout(-1, -2);
         }
      }

   }

   public void onViewCreated(View var1, @Nullable Bundle var2) {
      super.onViewCreated(var1, var2);
      this.mProject = (JavaProject)this.getArguments().getSerializable("DEX_FILE");
      this.mPref = PreferenceManager.getDefaultSharedPreferences(this.getContext());
      if (this.mProject != null) {
         this.setupSpinnerMainClass(var1, this.mProject);
         this.mArgs = (EditText)var1.findViewById(2131296419);
         this.mArgs.setText(this.mPref.getString("program_args", ""));
         this.mPackage = (EditText)var1.findViewById(2131296425);
         this.mPackage.setText(this.mProject.getPackageName());
         var1.findViewById(2131296350).setOnClickListener(new OnClickListener() {
            public void onClick(View var1) {
               DialogRunConfig.this.dismiss();
            }
         });
         var1.findViewById(2131296359).setOnClickListener(new OnClickListener() {
            public void onClick(View var1) {
               DialogRunConfig.this.save();
            }
         });
      }
   }

   public interface OnConfigChangeListener {
      void onConfigChange(JavaProject var1);
   }
}