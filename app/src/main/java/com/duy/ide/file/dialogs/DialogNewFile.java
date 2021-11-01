/* Decompiler 22ms, total 556ms, lines 138 */
package com.duy.ide.file.dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.duy.file.explorer.FileExplorerActivity;
import com.duy.ide.editor.editor.R.id;
import com.duy.ide.editor.editor.R.layout;
import com.duy.ide.editor.editor.R.string;
import java.io.File;

public class DialogNewFile extends AppCompatDialogFragment {
   private static final String KEY_CURRENT_DIR = "currentDir";
   private static final String KEY_FILE_EXTENSIONS = "fileExtensions";
   private static final int RC_SELECT_PATH = 991;
   private String[] fileExtensions;
   private String mCurrentDir;
   @Nullable
   private DialogNewFile.OnCreateFileListener mListener;
   private EditText mNameEditText;
   private EditText mPathExitText;
   private Spinner mSpinnerExt;

   private boolean createNewFile() {
      String var1 = this.mNameEditText.getText().toString();
      if (this.mNameEditText.length() != 0 && var1.matches("[A-Za-z0-9_./ ]+")) {
         String var2 = this.mPathExitText.getText().toString();
         String var3 = var1;
         if (!var1.contains(".")) {
            StringBuilder var5 = new StringBuilder();
            var5.append(var1);
            var5.append(this.mSpinnerExt.getSelectedItem().toString());
            var3 = var5.toString();
         }

         File var6 = new File(var2, var3);

         try {
            var6.getParentFile().mkdirs();
            var6.createNewFile();
            if (this.mListener != null) {
               this.mListener.onFileCreated(var6);
            }

            return true;
         } catch (Exception var4) {
            Toast.makeText(this.getContext(), var4.getMessage(), 0).show();
            return false;
         }
      } else {
         this.mNameEditText.setError(this.getContext().getString(string.invalid_name));
         return false;
      }
   }

   public static DialogNewFile newInstance(@NonNull String[] var0, @NonNull String var1, DialogNewFile.OnCreateFileListener var2) {
      Bundle var3 = new Bundle();
      DialogNewFile var4 = new DialogNewFile();
      var4.setArguments(var3);
      var3.putStringArray("fileExtensions", var0);
      var3.putString("currentDir", var1);
      var4.mListener = var2;
      return var4;
   }

   private void selectPath() {
      FileExplorerActivity.startPickPathActivity(this, this.mCurrentDir, "UTF-8", 991);
   }

   public void onActivityResult(int var1, int var2, Intent var3) {
      super.onActivityResult(var1, var2, var3);
      if (var1 == 991 && var2 == -1) {
         String var4 = FileExplorerActivity.getFile(var3);
         if (var4 != null) {
            this.mCurrentDir = var4;
            this.mPathExitText.setText(var4);
         }
      }

   }

   @Nullable
   public View onCreateView(@NonNull LayoutInflater var1, @Nullable ViewGroup var2, @Nullable Bundle var3) {
      return var1.inflate(layout.dialog_new_file_default, var2, false);
   }

   public void onViewCreated(@NonNull View var1, @Nullable Bundle var2) {
      super.onViewCreated(var1, var2);
      if (this.mCurrentDir == null) {
         this.mCurrentDir = this.getArguments().getString("currentDir");
      }

      if (this.fileExtensions == null) {
         this.fileExtensions = this.getArguments().getStringArray("fileExtensions");
      }

      this.mPathExitText = (EditText)var1.findViewById(id.edit_path);
      this.mPathExitText.setText(this.mCurrentDir);
      this.mNameEditText = (EditText)var1.findViewById(id.edit_input);
      this.mSpinnerExt = (Spinner)var1.findViewById(id.spinner_exts);
      ArrayAdapter var3 = new ArrayAdapter(this.getContext(), 17367043, this.fileExtensions);
      var3.setDropDownViewResource(17367055);
      this.mSpinnerExt.setAdapter(var3);
      var1.findViewById(id.btn_create).setOnClickListener(new OnClickListener() {
         public void onClick(View var1) {
            if (DialogNewFile.this.createNewFile()) {
               DialogNewFile.this.dismiss();
            }

         }
      });
      var1.findViewById(id.btn_cancel).setOnClickListener(new OnClickListener() {
         public void onClick(View var1) {
            DialogNewFile.this.dismiss();
         }
      });
      var1.findViewById(id.btn_select_path).setOnClickListener(new OnClickListener() {
         public void onClick(View var1) {
            DialogNewFile.this.selectPath();
         }
      });
   }

   public interface OnCreateFileListener {
      void onFileCreated(@NonNull File var1);
   }
}