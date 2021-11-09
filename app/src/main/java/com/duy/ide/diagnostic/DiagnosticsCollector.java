package com.duy.ide.diagnostic;

import com.duy.ide.diagnostic.model.Message;
import java.util.ArrayList;

public class DiagnosticsCollector implements DiagnosticListener {
   private final ArrayList<Message> messages = new ArrayList();

   public void clear() {
      this.messages.clear();
   }

   public ArrayList<Message> getMessages() {
      return this.messages;
   }

   public void report(Message message) {
      this.messages.add(message);
   }
}