package com.upf.bastionbreaker.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.upf.bastionbreaker.model.graphics.TextureManager;
import com.upf.bastionbreaker.model.map.MapManager;
import com.upf.bastionbreaker.model.map.GameObject;
import com.upf.bastionbreaker.model.entities.Obstacle;
import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {
    private MapRenderer mapRenderer;
    private MapManager mapManager; // Gestionnaire de carte TMX
    private SpriteBatch batch;
    private List<Obstacle> obstacles; // Liste des obstacles à afficher

    @Override
    public void show() {
        System.out.println("✅ Initialisation de GameScreen...");

        TextureManager.load();
        batch = new SpriteBatch();
        obstacles = new ArrayList<>();

        try {
            // Charger la carte `.tmx` via `MapManager`
            mapManager = new MapManager("assets/map/bastion_breaker_map.tmx");
            System.out.println("✅ MapManager chargé avec succès !");
        } catch (Exception e) {
            System.out.println("❌ ERREUR : Impossible de charger MapManager !");
            e.printStackTrace();
        }

        try {
            // Charger et afficher la carte
            mapRenderer = new MapRenderer(mapManager.getTiledMap());
            System.out.println("✅ Map rendue avec succès !");
        } catch (Exception e) {
            System.out.println("❌ ERREUR : Impossible de charger MapRenderer !");
            e.printStackTrace();
        }

        // Charger et stocker les obstacles
        chargerObstacles();

        // Affichage des objets chargés depuis la carte TMX
        afficherObjets("Obstacles");
        afficherObjets("Checkpoints");
        afficherObjets("Enemies");
        afficherObjets("Bastion");
        afficherObjets("FlyingBox");
        afficherObjets("WaterZones");
        afficherObjets("WindZones");
        afficherObjets("Platforms");
        afficherObjets("Chains");
        afficherObjets("Explosives");
        afficherObjets("Lava");
        afficherObjets("Ladders");
        afficherObjets("Bridges");
    }

    /**
     * Charge et initialise tous les obstacles en récupérant leurs propriétés depuis la carte.
     */
    private void chargerObstacles() {
        List<GameObject> obstacleObjects = mapManager.getObjects("Obstacles");
        if (obstacleObjects.isEmpty()) {
            System.out.println("⚠️ Aucun obstacle trouvé.");
            return;
        }

        for (GameObject obj : obstacleObjects) {
            Obstacle obstacle = new Obstacle(
                obj.getName(),
                obj.getX(),
                obj.getY(),
                obj.getWidth(),
                obj.getHeight(),
                obj.getProperties()
            );

            obstacles.add(obstacle);
        }

        System.out.println("✅ " + obstacles.size() + " obstacles chargés !");
    }

    /**
     * Affiche les objets chargés d'un calque donné dans la console.
     */
    private void afficherObjets(String layerName) {
        List<GameObject> objets = mapManager.getObjects(layerName);
        if (objets.isEmpty()) {
            System.out.println("⚠️  Aucun objet dans '" + layerName + "'.");
        } else {
            System.out.println("📌 " + objets.size() + " objets chargés depuis '" + layerName + "'.");
        }
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
            System.out.println("❌ ERREUR : `mapRenderer` est NULL !");
        }

        // Rendu des obstacles
        batch.begin();
        for (Obstacle obstacle : obstacles) {
            obstacle.render(batch);
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
