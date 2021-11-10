package com.duy.ide.javaide.editor.autocomplete.parser;

import android.support.annotation.NonNull;

import com.android.annotations.Nullable;
import com.duy.android.compiler.project.JavaProject;
import com.duy.common.interfaces.Filter;

import java.io.File;
import java.util.List;


public class JavaDexClassLoader {
    private static final Filter<IClass> mClassFilter = new Filter<IClass>() {
        @Override
        public boolean accept(IClass aClass) {
            return !(aClass.isAnnotation() || aClass.isInterface() || aClass.isEnum());
        }
    };
    private static final Filter<IClass> mEnumFilter = new Filter<IClass>() {
        @Override
        public boolean accept(IClass aClass) {
            return aClass.isEnum();
        }
    };
    private static final Filter<IClass> mInterfaceFilter = new Filter<IClass>() {
        @Override
        public boolean accept(IClass aClass) {
            return aClass.isInterface();
        }
    };
    private static final Filter<IClass> mAnnotationFilter = new Filter<IClass>() {
        @Override
        public boolean accept(IClass aClass) {
            return aClass.isAnnotation();
        }
    };

    private JavaClassManager mClassReader;

    public JavaDexClassLoader(File classpath, File outDir) {
        mClassReader = JavaClassManager.getInstance(classpath, outDir);
    }

    public JavaClassManager getClassReader() {
        return mClassReader;
    }

    @NonNull
    public List<IClass> findAllWithPrefix(String simpleNamePrefix) {
        return mClassReader.find(simpleNamePrefix, null);
    }

    @NonNull
    public List<IClass> findAllWithPrefix(@NonNull String simpleNamePrefix,
                                          @Nullable Filter<IClass> filter) {
        return mClassReader.find(simpleNamePrefix, filter);
    }

    public List<IClass> findClasses(String simpleNamePrefix) {
        return mClassReader.find(simpleNamePrefix, mClassFilter);
    }

    public List<IClass> findInterfaces(String simpleNamePrefix) {
        return mClassReader.find(simpleNamePrefix, mInterfaceFilter);
    }

    public List<IClass> findEnums(String simpleNamePrefix) {
        return mClassReader.find(simpleNamePrefix, mEnumFilter);

    }

    public List<IClass> findAnnotations(String simpleNamePrefix) {
        return mClassReader.find(simpleNamePrefix, mAnnotationFilter);
    }


    public void loadAllClasses(JavaProject projectFile) {
        mClassReader.loadFromProject(projectFile);
    }

    public void updateClass(List<IClass> classes) {
        for (IClass aClass : classes) {
            mClassReader.update(aClass);
        }
    }
}
