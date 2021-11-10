package com.duy.ide.javaide.projectview;

import com.duy.android.compiler.project.JavaProject;


public class ProjectFilePresenter implements ProjectFileContract.Presenter {
    private ProjectFileContract.View view;

    public ProjectFilePresenter(ProjectFileContract.View view) {
        view.setPresenter(this);
        this.view = view;
    }

    @Override
    public void show(JavaProject projectFile, boolean expand) {
        view.display(projectFile, expand);
    }

    @Override
    public void refresh(JavaProject projectFile) {
        view.refresh();
    }
}
