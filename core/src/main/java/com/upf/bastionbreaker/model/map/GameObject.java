package com.upf.bastionbreaker.model.map;

import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Polygon;
import java.util.HashMap;
import java.util.Map;

public class GameObject {
    private String name;
    private String type;
    private float x, y, width, height;
    private Polygon polygon; // Ajout pour stocker les polygones
    private Map<String, Object> properties; // Stockage des propriétés personnalisées

    // Constructeur pour les objets rectangulaires
    public GameObject(String name, String type, float x, float y, float width, float height) {
        this.name = name;
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.polygon = null; // Pas de polygone ici
        this.properties = new HashMap<>();
    }

    // Constructeur pour les objets polygonaux
    public GameObject(String name, String type, PolygonMapObject polygonObject) {
        this.name = name;
        this.type = type;
        this.polygon = polygonObject.getPolygon();
        this.x = polygon.getX();
        this.y = polygon.getY();
        this.width = 0;
        this.height = 0;
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

    // Récupérer les points du polygone sous forme de tableau float[]
    public float[] getPolygonPoints() {
        Object polygonObj = properties.get("polygonPoints");
        if (polygonObj instanceof float[]) {
            return (float[]) polygonObj;
        }
        return null;
    }


    // Vérifier si l'objet est un polygone
    public boolean isPolygon() {
        return polygon != null;
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
            ", polygon=" + (polygon != null ? "YES" : "NO") + // Indiquer si c'est un polygone
            ", properties=" + properties +
            '}';
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
}
