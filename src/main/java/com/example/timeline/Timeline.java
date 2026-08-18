package com.example.timeline;

import javafx.scene.paint.Color;

public enum Timeline {

    ENGLISH("English Monarch", 0, 2, Color.CRIMSON),
    FRENCH("French Monarch", 2, 2, Color.STEELBLUE);

    private final String type;
    private final int startRow;
    private final int rowCount;
    private final Color color;

    Timeline(String type, int startRow, int rowCount, Color color) {
        this.type = type;
        this.startRow = startRow;
        this.rowCount = rowCount;
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public int getStartRow() {
        return startRow;
    }

    public int getRowCount() {
        return rowCount;
    }

    public Color getColor() {
        return color;
    }
}
