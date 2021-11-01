package com.duy.ide.javaide;

import java.io.File;

public interface FileChangeListener {
   void doOpenFile(File file);

   void onFileCreated(File file);

   void onFileDeleted(File file);
}