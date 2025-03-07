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
        // Charger la carte .tmx
        map = new TmxMapLoader().load(mapPath);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / 32f); // Ajuste à la taille des tuiles

        // Créer la caméra
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(20, 12, camera); // Taille ajustable selon l'écran
        camera.position.set(10, 6, 0); // Centrer la caméra
        camera.update();
    }

    public void update(float deltaTime) {
        camera.update();
        mapRenderer.setView(camera);
    }

    public void render() {
        mapRenderer.render();
    }

    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
