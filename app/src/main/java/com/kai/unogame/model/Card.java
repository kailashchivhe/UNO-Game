package com.kai.unogame.model;

public class Card {
    int id;
    int color;
    String type;
    String value;

    public Card(int id, int color, String type, String value) {
        this.id = id;
        this.color = color;
        this.type = type;
        this.value = value;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
