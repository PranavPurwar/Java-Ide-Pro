package com.duy.ide.diagnostic;

import android.view.View;
import com.duy.ide.diagnostic.model.Message;

public interface DiagnosticClickListener {
   void onDiagnosisClick(Message message, View view);
}