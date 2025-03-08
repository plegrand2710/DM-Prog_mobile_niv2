package com.upf.bastionbreaker.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MapRenderer {
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private Viewport viewport;

    public MapRenderer(String mapPath) {
        try {
            // Charger la carte `.tmx`
            map = new TmxMapLoader().load(mapPath);
            if (map == null) {
                System.out.println("❌ ERREUR : Impossible de charger la carte " + mapPath);
                return;
            } else {
                System.out.println("✅ Carte chargée avec succès : " + mapPath);
            }

            // Rendu de la carte
            mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / 32f); // Ajustement à la taille des tuiles

            // Initialisation de la caméra et de la viewport
            camera = new OrthographicCamera();
            viewport = new ExtendViewport(640 / 32f, 360 / 32f, camera); // Taille ajustée pour la résolution

            // Centrer la caméra sur le début de la carte
            camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
            camera.update();

        } catch (Exception e) {
            System.out.println("❌ ERREUR : Exception lors du chargement de la carte " + mapPath);
            e.printStackTrace();
        }
    }

    public void update(float deltaTime) {
        if (mapRenderer == null) {
            System.out.println("❌ ERREUR : `mapRenderer` est NULL !");
            return;
        }
        camera.update();
        mapRenderer.setView(camera);
    }

    public void render() {
        if (mapRenderer != null) {
            mapRenderer.render();
        } else {
            System.out.println("❌ ERREUR : `mapRenderer` est NULL, impossible d'afficher la carte.");
        }
    }

    public void dispose() {
        if (map != null) map.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
