package com.upf.bastionbreaker.model.map;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import com.badlogic.gdx.physics.box2d.*;
import com.upf.bastionbreaker.model.entities.Floor;
import com.upf.bastionbreaker.view.screens.MapRenderer;

public class MapManager {
    // Instance singleton
    private static MapManager instance;

    private TiledMap tiledMap;
    private MapObjectsParser objectsParser;
    private final Map<String, List<GameObject>> loadedObjects = new HashMap<>();

    // Constructeur qui charge la carte et les objets
    public MapManager(String mapPath) {
        try {
            // Charger la carte .tmx
            tiledMap = new TmxMapLoader().load(mapPath);
            System.out.println("✅ Carte chargée avec succès : " + mapPath);
        } catch (Exception e) {
            System.out.println("❌ ERREUR : Impossible de charger la carte " + mapPath);
            e.printStackTrace();
            return;
        }

        // Initialiser l'analyse des objets
        objectsParser = new MapObjectsParser(tiledMap);
        // Charger tous les objets définis dans la carte
        loadAllObjects();
        // Assigner l'instance pour le singleton
        instance = this;
    }

    // Méthode d'accès au singleton
    public static MapManager getInstance() {
        return instance;
    }

    // Charge tous les objets à partir des différents calques
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
        return tiledMap;
    }

    // Récupère la liste des objets pour un calque donné
    public List<GameObject> getObjects(String layerName) {
        return loadedObjects.getOrDefault(layerName, Collections.emptyList());
    }

    // Récupère uniquement les objets du calque "Checkpoints"
    public List<GameObject> getCheckpoints() {
        return getObjects("Checkpoints");
    }

    // Méthode de récupération sécurisée des objets d'un calque
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

    public void createFloorBodies(World world) {
        Floor.createFloors(world);
    }

    public void dispose() {
        if (tiledMap != null) {
            tiledMap.dispose();
        }
    }
}
