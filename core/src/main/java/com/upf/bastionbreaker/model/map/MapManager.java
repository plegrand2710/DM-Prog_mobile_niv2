package com.upf.bastionbreaker.model.map;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import java.util.Collections;
import java.util.List;

public class MapManager {
    private TiledMap tiledMap;
    private MapObjectsParser objectsParser;

    public MapManager(String mapPath) {
        try {
            // Charger la carte `.tmx`
            tiledMap = new TmxMapLoader().load(mapPath);
            System.out.println("✅ Carte chargée avec succès : " + mapPath);
        } catch (Exception e) {
            System.out.println("❌ ERREUR : Impossible de charger la carte " + mapPath);
            e.printStackTrace();
            return;
        }

        // Initialiser l’analyse des objets (seulement sur les calques d’objets)
        objectsParser = new MapObjectsParser(tiledMap);
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public List<GameObject> getObstacles() {
        return getObjectsSafely("Obstacles");
    }

    public List<GameObject> getCheckpoints() {
        return getObjectsSafely("Checkpoints");
    }

    public List<GameObject> getEnemies() {
        return getObjectsSafely("Enemies");
    }

    /**
     * Récupère les objets d'un calque en toute sécurité.
     * Vérifie que le calque existe et qu’il contient bien des objets.
     */
    private List<GameObject> getObjectsSafely(String layerName) {
        MapLayer layer = tiledMap.getLayers().get(layerName);
        if (layer == null) {
            System.out.println("⚠️  ATTENTION : Le calque '" + layerName + "' est introuvable !");
            return Collections.emptyList();
        }

        // Vérifier que le calque contient des objets
        if (layer.getObjects() == null || layer.getObjects().getCount() == 0) {
            System.out.println("⚠️  ATTENTION : Le calque '" + layerName + "' est vide ou n'est pas un groupe d'objets !");
            return Collections.emptyList();
        }

        List<GameObject> objects = objectsParser.parseObjects(layerName);
        if (objects.isEmpty()) {
            System.out.println("⚠️  ATTENTION : Le calque '" + layerName + "' est vide !");
        } else {
            System.out.println("✅ " + objects.size() + " objets chargés depuis '" + layerName + "'.");
        }
        return objects;
    }

    public void dispose() {
        if (tiledMap != null) {
            tiledMap.dispose();
        }
    }
}
