package com.duy.ide.diagnostic.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.duy.ide.diagnostic.DiagnosticClickListener;
import com.duy.ide.diagnostic.model.Message;
import com.duy.ide.editor.editor.R.color;
import com.duy.ide.editor.editor.R.drawable;
import com.duy.ide.editor.editor.R.id;
import com.duy.ide.editor.editor.R.layout;
import com.jecelyin.common.utils.DrawableUtils;
import java.io.File;
import java.util.List;

public class DiagnosticsAdapter extends Adapter<DiagnosticsAdapter.ViewHolder> {
   private Context mContext;
   private DiagnosticClickListener mDiagnosticClickListener;
   private List<Message> mMessages;

   DiagnosticsAdapter(List<Message> var1, Context var2) {
      this.mMessages = var1;
      this.mContext = var2;
   }

   private void setIcon(DiagnosticsAdapter.ViewHolder var1, Message var2) {
      Drawable var3;
      switch(var2.getKind()) {
      case ERROR:
         var3 = DrawableUtils.tintDrawable(ContextCompat.getDrawable(this.mContext, drawable.baseline_error_24), -65536);
         var1.icon.setImageDrawable(var3);
         break;
      case WARNING:
         var3 = DrawableUtils.tintDrawable(ContextCompat.getDrawable(this.mContext, drawable.baseline_warning_24), ContextCompat.getColor(this.mContext, color.color_diagnostic_warn));
         var1.icon.setImageDrawable(var3);
         break;
      default:
         var1.icon.setImageDrawable((Drawable)null);
      }

   }

   @UiThread
   public void add(Message var1) {
      this.mMessages.add(var1);
      this.notifyItemInserted(this.mMessages.size() - 1);
   }

   public void addAll(List<Message> var1) {
      int var2 = this.mMessages.size();
      this.mMessages.addAll(var1);
      this.notifyItemRangeInserted(var2, var1.size());
   }

   @UiThread
   public void clear() {
      this.mMessages.clear();
      this.notifyDataSetChanged();
   }

   public List<Message> getDiagnostics() {
      return this.mMessages;
   }

   public int getItemCount() {
      return this.mMessages.size();
   }

   public void onBindViewHolder(@NonNull DiagnosticsAdapter.ViewHolder var1, int var2) {
      final Message var3 = (Message)this.mMessages.get(var2);
      String var8;
      if (var3.getLineNumber() >= 1) {
         long var4 = (long)var3.getColumn();
         StringBuilder var6 = new StringBuilder();
         var6.append(var3.getLineNumber());
         var6.append(":");
         Object var7;
         if (var4 >= 1L) {
            var7 = var4;
         } else {
            var7 = "";
         }

         var6.append(var7);
         var8 = var6.toString();
         var1.txtLineCol.setText(var8);
      } else {
         var1.txtLineCol.setText("");
      }

      var8 = var3.getSourcePath();
      if (var8 != null) {
         var1.txtFile.setText((new File(var8)).getName());
      } else {
         var1.txtFile.setText("");
      }

      this.setIcon(var1, var3);
      if (var3.getText().isEmpty()) {
         var1.txtMessage.setVisibility(8);
      } else {
         var1.txtMessage.setVisibility(0);
         var1.txtMessage.setText(var3.getText());
      }

      var1.itemView.setOnClickListener(new OnClickListener() {
         public void onClick(View var1) {
            if (DiagnosticsAdapter.this.mDiagnosticClickListener != null) {
               DiagnosticsAdapter.this.mDiagnosticClickListener.onDiagnosisClick(var3, var1);
            }

         }
      });
   }

   @NonNull
   public DiagnosticsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup var1, int var2) {
      return new DiagnosticsAdapter.ViewHolder(LayoutInflater.from(var1.getContext()).inflate(layout.list_item_diagnostic_default, var1, false));
   }

   @UiThread
   public void remove(Message var1) {
      int var2 = this.mMessages.indexOf(var1);
      if (var2 >= 0) {
         this.mMessages.remove(var2);
         this.notifyItemRemoved(var2);
      }

   }

   @UiThread
   public void setData(List<Message> var1) {
      this.mMessages.clear();
      this.mMessages.addAll(var1);
      this.notifyDataSetChanged();
   }

   public void setDiagnosticClickListener(DiagnosticClickListener var1) {
      this.mDiagnosticClickListener = var1;
   }

   static class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
      ImageView icon;
      TextView txtFile;
      TextView txtLineCol;
      TextView txtMessage;

      ViewHolder(View var1) {
         super(var1);
         this.txtLineCol = (TextView)var1.findViewById(id.txt_line_col);
         this.txtMessage = (TextView)var1.findViewById(id.txt_message);
         this.txtFile = (TextView)var1.findViewById(id.txt_file);
         this.icon = (ImageView)var1.findViewById(id.imageview);
      }
   }
}