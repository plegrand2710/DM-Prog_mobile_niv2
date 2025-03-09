package com.upf.bastionbreaker.model.map;

public class GameObject {
    public String name;
    public String type;
    public float x, y, width, height;

    public GameObject(String name, String type, float x, float y, float width, float height) {
        this.name = name;
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
