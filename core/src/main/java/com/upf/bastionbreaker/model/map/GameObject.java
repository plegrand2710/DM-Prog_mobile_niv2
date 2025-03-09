package com.upf.bastionbreaker.model.map;

import java.util.HashMap;
import java.util.Map;

public class GameObject {
    private String name;
    private String type;
    private float x, y, width, height;
    private Map<String, Object> properties; // Stockage des propriétés personnalisées

    public GameObject(String name, String type, float x, float y, float width, float height) {
        this.name = name;
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.properties = new HashMap<>();
    }

    // Ajouter une propriété personnalisée
    public void addProperty(String key, Object value) {
        properties.put(key, value);
    }

    // Récupérer une propriété en fonction de son type
    public <T> T getProperty(String key, Class<T> type) {
        Object value = properties.get(key);
        if (value != null && type.isInstance(value)) {
            return type.cast(value);
        }
        return null; // Retourne null si la propriété n'existe pas
    }

    // Affichage détaillé pour le debug
    @Override
    public String toString() {
        return "GameObject{" +
            "name='" + name + '\'' +
            ", type='" + type + '\'' +
            ", x=" + x +
            ", y=" + y +
            ", width=" + width +
            ", height=" + height +
            ", properties=" + properties +
            '}';
    }

    // Getters pour accéder aux valeurs si besoin
    public String getName() { return name; }
    public String getType() { return type; }
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
}
