package com.upf.bastionbreaker.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.upf.bastionbreaker.model.graphics.TextureManager;
import com.upf.bastionbreaker.model.map.MapManager;
import com.upf.bastionbreaker.model.map.GameObject;
import com.upf.bastionbreaker.model.entities.Checkpoint;
import com.upf.bastionbreaker.model.entities.Obstacle;
import com.upf.bastionbreaker.model.entities.FlyingBox;
import com.upf.bastionbreaker.model.entities.IceBridge;
import com.upf.bastionbreaker.model.entities.Player;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameScreen implements Screen {
    private MapRenderer mapRenderer;
    private MapManager mapManager;
    private SpriteBatch batch;

    // Listes des objets à afficher
    private List<Checkpoint> checkpoints;
    private List<Obstacle> obstacles;
    private List<FlyingBox> flyingBoxes;
    private List<IceBridge> iceBridges;

    private Player player;

    @Override
    public void show() {
        System.out.println("✅ Initialisation de GameScreen...");

        TextureManager.load();

        try {
            // Charger la carte via MapManager
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

            // Charger les FlyingBox
            List<GameObject> flyingBoxObjects = mapManager.getObjects("FlyingBox");
            flyingBoxes = new ArrayList<>();
            for (GameObject obj : flyingBoxObjects) {
                flyingBoxes.add(new FlyingBox(obj));
            }
            System.out.println("📌 FlyingBox chargées : " + flyingBoxes.size());

            // Charger les Ice Bridges depuis le calque "Ice"
            List<GameObject> iceObjects = mapManager.getObjects("Ice");
            iceBridges = new ArrayList<>();
            for (GameObject obj : iceObjects) {
                iceBridges.add(new IceBridge(obj));
            }
            System.out.println("📌 Ice Bridges chargés : " + iceBridges.size());

            // Créer le joueur et définir sa position/taille (à adapter)
            player = new Player();
            player.setPosition(5, 5);
            player.setSize(1, 1);
            // Vous pouvez basculer entre mode Tank et Robot en appelant player.setTankMode(true/false);

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
        // Effacer l'écran
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

        // Vérifier les collisions avec les Ice Bridges
        // Utilisation d'un Iterator pour pouvoir retirer l'élément en cas de collision
        Iterator<IceBridge> it = iceBridges.iterator();
        while (it.hasNext()) {
            IceBridge iceBridge = it.next();
            if (iceBridge.getBounds().overlaps(player.getBounds())) {
                // Si le joueur est trop lourd (poids > limite du pont) et le pont est fragile, il s'effondre
                if (player.getWeight() > iceBridge.getWeightLimit() && iceBridge.isFragile()) {
                    if (player.isTank()) {
                        System.out.println("💥 Le pont de glace s’écroule !");
                    }
                    // Le pont est détruit et ne réapparaît pas
                    it.remove();
                }
            }
        }

        // Rendu des objets
        batch.begin();
        for (Obstacle obstacle : obstacles) {
            obstacle.render(batch);
        }
        for (Checkpoint checkpoint : checkpoints) {
            checkpoint.render(batch);
        }
        for (FlyingBox box : flyingBoxes) {
            box.render(batch);
        }
        for (IceBridge iceBridge : iceBridges) {
            iceBridge.render(batch);
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
