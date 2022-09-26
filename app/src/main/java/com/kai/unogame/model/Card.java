package com.kai.unogame.model;

public class Card {
    int id;
    String color;
    String type;
    String value;

    public Card(int id, String color, String type, String value) {
        this.id = id;
        this.color = color;
        this.type = type;
        this.value = value;
    }

    public Card(){

    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
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

    public int getId() {
        return id;
    }
}
