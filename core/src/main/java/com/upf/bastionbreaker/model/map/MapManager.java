package com.upf.bastionbreaker.model.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.upf.bastionbreaker.model.entities.Floor;
import java.util.ArrayList;

public class MapManager {
    private TiledMap tiledMap;
    private MapObjectsParser objectsParser;
    private final Map<String, List<GameObject>> loadedObjects = new HashMap<>();

    public MapManager(String mapPath) {
        try {
            // Charger la carte .tmx
            tiledMap = new TmxMapLoader().load(mapPath);
            Gdx.app.log("DEBUG_GAME", "✅ Carte chargée avec succès :" + mapPath );

            System.out.println();
        } catch (Exception e) {
            Gdx.app.log("DEBUG_GAME","❌ ERREUR : Impossible de charger la carte " + mapPath);
            e.printStackTrace();
            return;
        }

        // Initialiser l'analyse des objets
        objectsParser = new MapObjectsParser(tiledMap);

        // Charger tous les objets définis dans la carte
        loadAllObjects();
    }

    /**
     * Charge tous les objets interactifs définis dans la carte.
     */
    private void loadAllObjects() {
        String[] objectLayers = {
            "Lava",
            "FallingBlock",
            "Bastion",
            "Chains",
            "UnstablePlatforms",
            "Enemies",
            "Checkpoints",
            "Obstacles",
            "Hill",
            "FlyingBox",
            "Ice",
            "WindZones",
            "Drawbridges",
            "Boss",
            "Explosives",
            "Ladders",
            "WaterZones",
            "Floor"
        };

        for (String layerName : objectLayers) {
            List<GameObject> objects = getObjectsSafely(layerName);
            loadedObjects.put(layerName, objects);
        }
    }

    public TiledMap getTiledMap() {
        Gdx.app.log("DEBUG_GAME", "passage tile map " + tiledMap);
        return tiledMap;
    }

    /**
     * Récupère une liste d'objets chargés pour un calque donné.
     */
    public List<GameObject> getObjects(String layerName) {
        return loadedObjects.getOrDefault(layerName, Collections.emptyList());
    }

    /**
     * Récupère uniquement les checkpoints.
     */
    public List<GameObject> getCheckpoints() {
        return getObjects("Checkpoints");
    }

    /**
     * Récupère les objets d'un calque en toute sécurité.
     * Vérifie que le calque existe et contient des objets.
     */
    private List<GameObject> getObjectsSafely(String layerName) {
        MapLayer layer = tiledMap.getLayers().get(layerName);
        if (layer == null) {
            System.out.println("⚠️ ATTENTION : Le calque '" + layerName + "' est introuvable !");
            return Collections.emptyList();
        }

        if (layer.getObjects() == null || layer.getObjects().getCount() == 0) {
            System.out.println("⚠️ ATTENTION : Le calque '" + layerName + "' est vide ou n'est pas un groupe d'objets !");
            return Collections.emptyList();
        }

        List<GameObject> objects = objectsParser.parseObjects(layerName);
        if (objects.isEmpty()) {
            System.out.println("⚠️ ATTENTION : Le calque '" + layerName + "' est vide !");
        } else {
            System.out.println("✅ " + objects.size() + " objets chargés depuis '" + layerName + "'.");
        }
        return objects;
    }

    public List<Floor> getFloors() {
        List<GameObject> floorObjects = getObjects("Floor");
        List<Floor> floors = new ArrayList<>();
        for (GameObject obj : floorObjects) {
            floors.add(new Floor(obj));
        }
        if (floors.isEmpty()) {
            System.out.println("⚠️ ATTENTION : Aucun objet Floor n'a été trouvé dans le calque 'Floor' !");
        } else {
            //System.out.println("✅ " + floors.size() + " Floor(s) chargés depuis 'Floor'.");
        }
        return floors;
    }

    public void dispose() {
        if (tiledMap != null) {
            tiledMap.dispose();
        }
    }
}
