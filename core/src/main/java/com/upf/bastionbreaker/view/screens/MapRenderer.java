package com.upf.bastionbreaker.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
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

    /**
     * Constructeur qui prend directement une carte TiledMap en argument.
     */
    public MapRenderer(TiledMap tiledMap) {
        if (tiledMap == null) {
            System.out.println("❌ ERREUR : La carte TiledMap fournie est NULL !");
            return;
        }

        this.map = tiledMap;
        System.out.println("✅ Carte TMX chargée avec succès !");

        // Création du renderer avec l'échelle ajustée
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / TILE_SIZE);

        // Initialisation de la caméra
        camera = new OrthographicCamera();
        viewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera);

        // Positionner la caméra **en bas à gauche** (0, 0)
        camera.position.set(VIEWPORT_WIDTH / 2, VIEWPORT_HEIGHT / 2, 0);
        camera.update();
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

    public void setView(OrthographicCamera camera) {
        if (camera == null) {
            System.out.println("❌ ERREUR : La caméra fournie est NULL !");
            return;
        }
        this.camera = camera;
        if (mapRenderer != null) {
            mapRenderer.setView(camera);
        } else {
            System.out.println("❌ ERREUR : `mapRenderer` est NULL, impossible de définir la vue.");
        }
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
