package com.duy.ide.file.model;

import android.net.Uri;
import android.support.annotation.Nullable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public interface IFileObject {
   InputStream openInputStream() throws IOException;

   OutputStream openOutputStream() throws FileNotFoundException;

   Reader openReader() throws IOException;

   Writer openWriter() throws IOException;

   @Nullable
   Uri toUri();
}