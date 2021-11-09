package com.duy.ide.diagnostic;

import com.duy.ide.diagnostic.model.Message;

public interface DiagnosticListener {
   void clear();

   void report(Message message);
}