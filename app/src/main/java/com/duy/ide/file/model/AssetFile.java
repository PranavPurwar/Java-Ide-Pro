package com.duy.ide.file.model;

import android.content.res.AssetManager;
import android.net.Uri;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public class AssetFile implements IFileObject {
   private final AssetManager assetManager;
   private final String path;

   public AssetFile(AssetManager manager, String path) {
      this.assetManager = manager;
      this.path = path;
   }

   public InputStream openInputStream() throws IOException {
      return this.assetManager.open(this.path);
   }

   public OutputStream openOutputStream() {
      throw new UnsupportedOperationException();
   }

   public Reader openReader() throws IOException {
      return new InputStreamReader(this.openInputStream());
   }

   public Writer openWriter() {
      throw new UnsupportedOperationException();
   }

   public Uri toUri() {
      return null;
   }
}