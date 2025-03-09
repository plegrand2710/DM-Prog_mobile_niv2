package com.upf.bastionbreaker.model.map;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapManager {
    private TiledMap tiledMap;
    private MapObjectsParser objectsParser;
    private final Map<String, List<GameObject>> loadedObjects = new HashMap<>();

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

        // Initialiser l’analyse des objets
        objectsParser = new MapObjectsParser(tiledMap);

        // Charger tous les objets définis
        loadAllObjects();
    }

    /**
     * Charge tous les objets interactifs définis dans la carte.
     */
    private void loadAllObjects() {
        String[] objectLayers = {
            "Obstacles", "Checkpoints", "Enemies", "Bastion",
            "FlyingBox", "WaterZones", "WindZones", "Platforms",
            "Chains", "Explosives", "Lava", "Ladders", "Bridges"
        };

        for (String layerName : objectLayers) {
            List<GameObject> objects = getObjectsSafely(layerName);
            loadedObjects.put(layerName, objects);
        }
    }

    public TiledMap getTiledMap() {
        return tiledMap;
    }

    /**
     * Récupère une liste d'objets chargés pour un calque donné.
     */
    public List<GameObject> getObjects(String layerName) {
        return loadedObjects.getOrDefault(layerName, Collections.emptyList());
    }

    /**
     * Récupère les objets d'un calque en toute sécurité.
     * Vérifie que le calque existe et contient bien des objets.
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
