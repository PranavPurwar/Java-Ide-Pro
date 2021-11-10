package com.duy.ide.javaide.editor.autocomplete.model;

public class Statement {
    private final String statement;
    private final int offset;

    public Statement(String statement, int offset) {
        this.statement = statement;
        this.offset = offset;
    }

    public String getStatement() {
        return statement;
    }

    public int getOffset() {
        return offset;
    }
}
