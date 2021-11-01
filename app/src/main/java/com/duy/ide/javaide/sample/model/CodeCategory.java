package com.duy.ide.javaide.sample.model;

import java.io.Serializable;
import java.util.ArrayList;


@SuppressWarnings("DefaultFileTemplate")
public class CodeCategory implements Serializable, Cloneable {
    private String title;
    private String description;
    private String imagePath;
    private String categoryPath;
    private ArrayList<CodeProjectSample> codeSampleEntries = new ArrayList<>();

    public CodeCategory(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public CodeCategory(String title, String description, String imagePath, String categoryPath) {
        this.title = title;
        this.description = description;
        this.imagePath = imagePath;
        this.categoryPath = categoryPath;

    }

    public String getProjectPath() {
        return categoryPath;
    }

    public void setProjectPath(String categoryPath) {
        this.categoryPath = categoryPath;
    }

    public ArrayList<CodeProjectSample> getCodeSampleEntries() {
        return codeSampleEntries;
    }

    public int size() {
        return codeSampleEntries.size();
    }

    public void addCodeItem(CodeProjectSample entry) {
        codeSampleEntries.add(entry);
    }

    public void removeCode(CodeProjectSample entry) {
        codeSampleEntries.remove(entry);
    }

    public void removeCode(int position) {
        if (position > codeSampleEntries.size() - 1) return;
        codeSampleEntries.remove(position);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public CodeProjectSample getProject(int childPosition) {
        return codeSampleEntries.get(childPosition);
    }
}
