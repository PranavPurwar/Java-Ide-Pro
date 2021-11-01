package com.duy.ide.javaide.sample.model;

import com.google.common.base.MoreObjects;

import java.io.Serializable;


public class CodeProjectSample implements Serializable{
    /**
     * name of file code
     */
    private String name;
    private String path;
    private String description;
    /**
     * code
     */
    private String content;
    private String query;

    public CodeProjectSample(String name, String path, String description) {
        this.name = name;
        this.path = path;
        this.description = description;
    }

    public CodeProjectSample(String name, CharSequence content) {
        this.name = name;
        this.content = content.toString();
    }

    public CodeProjectSample(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public String getPath() {
        return path;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("path", path)
                .add("description", description)
                .add("content", content)
                .add("query", query)
                .toString();
    }

    public CodeProjectSample clone() {
        return new CodeProjectSample(name, content);
    }
}
