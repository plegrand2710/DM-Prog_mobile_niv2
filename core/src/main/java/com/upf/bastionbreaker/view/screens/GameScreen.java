package com.upf.bastionbreaker.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.upf.bastionbreaker.model.graphics.TextureManager;
import com.upf.bastionbreaker.model.map.MapManager;
import com.upf.bastionbreaker.model.map.GameObject;
import com.upf.bastionbreaker.model.entities.Checkpoint;
import com.upf.bastionbreaker.model.entities.Obstacle; // Import de la classe Obstacle
import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {
    private MapRenderer mapRenderer;
    private MapManager mapManager; // Gestionnaire de la carte
    private SpriteBatch batch;

    // Listes des checkpoints et obstacles à afficher
    private List<Checkpoint> checkpoints;
    private List<Obstacle> obstacles;

    @Override
    public void show() {
        System.out.println("✅ Initialisation de GameScreen...");

        TextureManager.load();

        try {
            // Charger la carte `.tmx` via MapManager
            mapManager = new MapManager("assets/map/bastion_breaker_map.tmx");
            System.out.println("✅ MapManager chargé avec succès !");

            // Charger les checkpoints
            List<GameObject> checkpointObjects = mapManager.getCheckpoints();
            checkpoints = new ArrayList<>();
            for (GameObject obj : checkpointObjects) {
                checkpoints.add(new Checkpoint(obj));
            }
            System.out.println("📌 Checkpoints chargés : " + checkpoints.size());

            // Charger les obstacles
            List<GameObject> obstacleObjects = mapManager.getObjects("Obstacles");
            obstacles = new ArrayList<>();
            for (GameObject obj : obstacleObjects) {
                obstacles.add(new Obstacle(obj));
            }
            System.out.println("📌 Obstacles chargés : " + obstacles.size());

        } catch (Exception e) {
            System.out.println("❌ ERREUR : Impossible de charger la carte !");
            e.printStackTrace();
        }

        try {
            mapRenderer = new MapRenderer(mapManager.getTiledMap());
            System.out.println("✅ Map rendue avec succès !");
        } catch (Exception e) {
            System.out.println("❌ ERREUR : Impossible de charger MapRenderer !");
            e.printStackTrace();
        }

        batch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        // Nettoyer l'écran et définir une couleur de fond
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (mapRenderer != null) {
            mapRenderer.update(delta);
            mapRenderer.render();
        } else {
            System.out.println("❌ ERREUR : mapRenderer est NULL !");
        }

        // Synchroniser le SpriteBatch avec la caméra
        batch.setProjectionMatrix(mapRenderer.getCamera().combined);

        // Dessiner les obstacles et les checkpoints
        batch.begin();
        // Dessiner d'abord les obstacles
        for (Obstacle obstacle : obstacles) {
            obstacle.render(batch);
        }
        // Dessiner ensuite les checkpoints (ou dans l'ordre souhaité)
        for (Checkpoint checkpoint : checkpoints) {
            checkpoint.render(batch);
        }
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        System.out.println("🔄 Resize GameScreen : " + width + "x" + height);
        if (mapRenderer != null) {
            mapRenderer.getCamera().viewportWidth = MapRenderer.VIEWPORT_WIDTH;
            mapRenderer.getCamera().viewportHeight = MapRenderer.VIEWPORT_HEIGHT;
            mapRenderer.getCamera().update();
        }
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        System.out.println("🚀 Nettoyage de GameScreen...");
        if (mapManager != null) mapManager.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
        if (batch != null) batch.dispose();
    }
}
