package com.upf.bastionbreaker.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MapRenderer {
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private Viewport viewport;

    public static final float TILE_SIZE = 32f; // Taille d'une tuile
    public static final int MAP_WIDTH_TILES = 270; // Largeur de la carte en tuiles
    public static final int MAP_HEIGHT_TILES = 20; // Hauteur de la carte en tuiles
    public static final float VIEWPORT_WIDTH = 34; // Largeur visible en tuiles
    public static final float VIEWPORT_HEIGHT = 17; // Hauteur visible en tuiles

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
            mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / TILE_SIZE); // Ajustement à l'échelle

            // Initialisation de la caméra
            camera = new OrthographicCamera();
            viewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera); // Viewport mobile

            // Positionner la caméra **en bas à gauche** (0, 0)
            camera.position.set(VIEWPORT_WIDTH / 2, VIEWPORT_HEIGHT / 2, 0);
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
