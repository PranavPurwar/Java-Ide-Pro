package com.duy.ide.javaide.editor.autocomplete.parser;

import android.os.FileObserver;
import android.util.Log;

import com.android.annotations.Nullable;
import com.duy.android.compiler.project.JavaProject;
import com.duy.ide.javaide.editor.autocomplete.model.PackageDescription;

import java.io.File;
import java.util.ArrayList;


public class PackageManager {
    private static final String TAG = "AutoCompletePackage";
    private PackageDescription root;
    private FileObserver fileObserver;

    public PackageManager() {
        root = PackageDescription.root();
    }

    public void init(JavaProject projectFile, JavaClassManager classReader) {
        try {
            Log.d(TAG, "init() called with: classReader = [" + classReader + "]");

            ArrayList<IClass> classes = classReader.getAllClasses();
            for (IClass clazz : classes) {
                root.put(clazz.getFullClassName());
            }
// TODO: 16-Aug-17 file watcher
//        final String parentPath = projectFile.getDirSrcJava().getPath();
//        fileObserver = new FileObserver(parentPath) {
//            @Override
//            public void onEvent(int event, String path) {
//                remove(path, parentPath);
//                add(path, parentPath);
//            }
//        };
//        fileObserver.startWatching();
        } catch (Exception ignored) {}
    }

    private void add(String child, String parent) {
        if (child.startsWith(parent)) {
            child = child.substring(parent.length() + 1);
            child = child.replace(File.separator, ".");
            root.put(child);
        }
    }

    @Nullable
    private PackageDescription remove(String child, String parent) {
        if (child.startsWith(parent)) {
            child = child.substring(parent.length() + 1);
            child = child.replace(File.separator, ".");
            return root.remove(child);
        }
        return null;
    }

    public void destroy() {
        if (fileObserver != null) fileObserver.stopWatching();
    }

    @Nullable
    public PackageDescription trace(String child) {
        return this.root.get(child);
    }

}
