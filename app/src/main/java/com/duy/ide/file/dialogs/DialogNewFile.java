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
      String fileName = this.mNameEditText.getText().toString();
      if (fileName.length() != 0 && fileName.matches("[A-Za-z0-9_./ ]+")) {
         String path = this.mPathExitText.getText().toString();
         if (!fileName.contains(".")) {
            fileName = fileName + this.mSpinnerExt.getSelectedItem().toString();
         }

         File file = new File(path, fileName);

         try {
            file.getParentFile().mkdirs();
            file.createNewFile();
            if (this.mListener != null) {
               this.mListener.onFileCreated(file);
            }
            return true;
         } catch (Exception e) {
            Toast.makeText(this.getContext(), e.getMessage(), 0).show();
            return false;
         }
      } else {
         this.mNameEditText.setError(this.getContext().getString(string.invalid_name));
         return false;
      }
   }

   public static DialogNewFile newInstance(@NonNull String[] extensions, @NonNull String path, DialogNewFile.OnCreateFileListener listener) {
      Bundle bundle = new Bundle();
      DialogNewFile dialog = new DialogNewFile();
      dialog.setArguments(bundle);
      bundle.putStringArray("fileExtensions", extensions);
      bundle.putString("currentDir", path);
      dialog.mListener = listener;
      return dialog;
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
   public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup group, @Nullable Bundle bundle) {
      return inflater.inflate(layout.dialog_new_file_default, group, false);
   }

   public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
      super.onViewCreated(view, bundle);
      if (this.mCurrentDir == null) {
         this.mCurrentDir = this.getArguments().getString("currentDir");
      }

      if (this.fileExtensions == null) {
         this.fileExtensions = this.getArguments().getStringArray("fileExtensions");
      }

      this.mPathExitText = (EditText)view.findViewById(id.edit_path);
      this.mPathExitText.setText(this.mCurrentDir);
      this.mNameEditText = (EditText)view.findViewById(id.edit_input);
      this.mSpinnerExt = (Spinner)view.findViewById(id.spinner_exts);
      ArrayAdapter adapter = new ArrayAdapter(this.getContext(), 17367043, this.fileExtensions);
      adapter.setDropDownViewResource(17367055);
      this.mSpinnerExt.setAdapter(adapter);
      view.findViewById(id.btn_create).setOnClickListener(new OnClickListener() {
         public void onClick(View unused) {
            if (DialogNewFile.this.createNewFile()) {
               DialogNewFile.this.dismiss();
            }

         }
      });
      view.findViewById(id.btn_cancel).setOnClickListener(new OnClickListener() {
         public void onClick(View unused) {
            DialogNewFile.this.dismiss();
         }
      });
      view.findViewById(id.btn_select_path).setOnClickListener(new OnClickListener() {
         public void onClick(View unused) {
            DialogNewFile.this.selectPath();
         }
      });
   }

   public interface OnCreateFileListener {
      void onFileCreated(@NonNull File file);
   }
}