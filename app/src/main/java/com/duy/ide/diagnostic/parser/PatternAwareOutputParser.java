package com.duy.ide.diagnostic.parser;

import android.support.annotation.NonNull;
import com.duy.ide.diagnostic.model.Message;
import com.duy.ide.diagnostic.util.OutputLineReader;
import com.duy.ide.logging.ILogger;
import java.util.List;

public interface PatternAwareOutputParser {
   boolean parse(@NonNull String string, @NonNull OutputLineReader reader, @NonNull List<Message> messages, @NonNull ILogger logger) throws ParsingFailedException;
}