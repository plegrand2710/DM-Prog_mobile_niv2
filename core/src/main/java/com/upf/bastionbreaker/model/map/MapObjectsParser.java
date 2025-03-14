package com.upf.bastionbreaker.model.map;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class MapObjectsParser {
    private final TiledMap map;

    public MapObjectsParser(TiledMap map) {
        this.map = map;
    }

    public List<GameObject> parseObjects(String layerName) {
        List<GameObject> gameObjects = new ArrayList<>();

        // Vérifier si le calque d'objets existe
        MapLayer layer = map.getLayers().get(layerName);
        if (layer == null) {
            System.out.println("❌ ERREUR : Le calque '" + layerName + "' est introuvable dans le fichier TMX !");
            return gameObjects;
        }

        MapObjects objects = layer.getObjects();

        for (MapObject obj : objects) {
            if (obj instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) obj).getRectangle();
                String name = obj.getName() != null ? obj.getName() : "Unnamed";
                String type = obj.getProperties().get("type", String.class);
                if (type == null) type = "Unknown"; // Valeur par défaut si aucun type

                // Créer un objet générique
                GameObject gameObject = new GameObject(name, type, rect.x, rect.y, rect.width, rect.height);

                // Récupérer toutes les propriétés de l'objet
                Iterator<String> keys = obj.getProperties().getKeys();
                while (keys.hasNext()) { // Utilisation de `while` au lieu d'un `foreach`
                    String key = keys.next();
                    Object value = obj.getProperties().get(key);
                    gameObject.addProperty(key, value);
                }

                gameObjects.add(gameObject);
                System.out.println("✅ Chargé : " + gameObject);
            }
        }

        return gameObjects;
    }
}
