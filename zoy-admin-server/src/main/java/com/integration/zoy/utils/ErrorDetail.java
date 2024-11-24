package com.integration.zoy.utils;

public class ErrorDetail {
    private int row;
    private int column;
    private String field;
    private String errorDescription;

    public ErrorDetail(int row, int column, String field, String errorDescription) {
        this.row = row;
        this.column = column;
        this.field = field;
        this.errorDescription = errorDescription;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public String getField() {
        return field;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    @Override
    public String toString() {
        return "Row " + row + ", Column " + column + " (" + field + "): " + errorDescription;
    }
}

