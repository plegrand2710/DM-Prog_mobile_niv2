package com.upf.bastionbreaker.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.upf.bastionbreaker.model.graphics.TextureManager;
import com.upf.bastionbreaker.model.map.GameObject;
import com.upf.bastionbreaker.model.map.MapManager;
import com.upf.bastionbreaker.model.entities.Checkpoint;
import com.upf.bastionbreaker.model.entities.Obstacle;
import com.upf.bastionbreaker.model.entities.FlyingBox;
import com.upf.bastionbreaker.model.entities.IceBridge;
import com.upf.bastionbreaker.model.entities.ChainLink;
import com.upf.bastionbreaker.model.entities.Bastion;
import com.upf.bastionbreaker.model.entities.TNT;
import com.upf.bastionbreaker.model.entities.FallingBlock;
import com.upf.bastionbreaker.model.entities.Drawbridge;
import com.upf.bastionbreaker.model.entities.UnstablePlatform;
import com.upf.bastionbreaker.model.entities.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameScreen implements Screen {
    private MapRenderer mapRenderer;
    private MapManager mapManager;
    private SpriteBatch batch;

    // Listes des objets à afficher
    private List<Checkpoint> checkpoints;
    private List<Obstacle> obstacles;
    private List<FlyingBox> flyingBoxes;
    private List<IceBridge> iceBridges;
    private List<ChainLink> chainLinks;
    private List<Bastion> bastions;
    private List<TNT> tnts;
    private List<FallingBlock> fallingBlocks;
    private List<Drawbridge> drawbridges;
    private List<UnstablePlatform> unstablePlatforms;

    // Dictionnaire pour retrouver un ChainLink via son nom (en minuscules)
    private Map<String, ChainLink> chainLinkMap;

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
            checkpoints = new ArrayList<>();
            for (GameObject obj : mapManager.getCheckpoints()) {
                checkpoints.add(new Checkpoint(obj));
            }

            // Charger les obstacles
            obstacles = new ArrayList<>();
            for (GameObject obj : mapManager.getObjects("Obstacles")) {
                obstacles.add(new Obstacle(obj));
            }

            // Charger les FlyingBox
            flyingBoxes = new ArrayList<>();
            for (GameObject obj : mapManager.getObjects("FlyingBox")) {
                flyingBoxes.add(new FlyingBox(obj));
            }

            // Charger les IceBridges
            iceBridges = new ArrayList<>();
            for (GameObject obj : mapManager.getObjects("Ice")) {
                iceBridges.add(new IceBridge(obj));
            }

            // Charger les ChainLinks
            List<GameObject> chainObjects = mapManager.getObjects("Chains");
            chainLinks = new ArrayList<>();
            chainLinkMap = new HashMap<>();
            for (GameObject obj : chainObjects) {
                ChainLink link = new ChainLink(obj);
                chainLinks.add(link);
                if (obj.getName() != null) {
                    chainLinkMap.put(obj.getName().toLowerCase(), link);
                }
            }
            System.out.println("🔗 ChainLinks chargés : " + chainLinks.size());

            // Charger les Bastions
            bastions = new ArrayList<>();
            for (GameObject obj : mapManager.getObjects("Bastion")) {
                bastions.add(new Bastion(obj));
            }
            System.out.println("📌 Bastions chargés : " + bastions.size());

            // Charger les TNT (Explosives)
            tnts = new ArrayList<>();
            for (GameObject obj : mapManager.getObjects("Explosives")) {
                tnts.add(new TNT(obj));
            }
            System.out.println("📌 TNT chargées : " + tnts.size());

            // Charger les FallingBlocks
            fallingBlocks = new ArrayList<>();
            for (GameObject obj : mapManager.getObjects("FallingBlock")) {
                FallingBlock fb = new FallingBlock(obj);
                fallingBlocks.add(fb);
            }
            System.out.println("📌 FallingBlocks chargés : " + fallingBlocks.size());

            // Charger les Drawbridges
            drawbridges = new ArrayList<>();
            for (GameObject obj : mapManager.getObjects("Drawbridges")) {
                Drawbridge db = new Drawbridge(obj);
                drawbridges.add(db);
            }
            System.out.println("📌 Drawbridges chargés : " + drawbridges.size());

            // Charger les UnstablePlatforms
            unstablePlatforms = new ArrayList<>();
            for (GameObject obj : mapManager.getObjects("UnstablePlatforms")) {
                UnstablePlatform up = new UnstablePlatform(obj);
                unstablePlatforms.add(up);
            }
            System.out.println("📌 UnstablePlatforms chargés : " + unstablePlatforms.size());

            // Lier les FallingBlocks à leur maillon support
            for (FallingBlock fb : fallingBlocks) {
                String topName = fb.getLinkedTopName();
                if (topName != null && !topName.isEmpty()) {
                    ChainLink support = chainLinkMap.get(topName.toLowerCase());
                    if (support != null) {
                        fb.setLinkedChain(support);
                    }
                }
            }

            // Lier les Drawbridges à leur chain link support
            for (Drawbridge db : drawbridges) {
                String supportName = db.getLinkedTopName();
                if (supportName != null && !supportName.isEmpty()) {
                    ChainLink support = chainLinkMap.get(supportName.toLowerCase());
                    if (support != null) {
                        db.setSupportingLink(support);
                    }
                }
            }

            // Lier les UnstablePlatforms à leur chain link support si besoin
            for (UnstablePlatform up : unstablePlatforms) {
                String supportName = up.getLinkedTopName();
                if (supportName != null && !supportName.isEmpty()) {
                    ChainLink support = chainLinkMap.get(supportName.toLowerCase());
                    if (support != null) {
                        up.setLinkedChain(support);
                    }
                }
            }

            // Créer le joueur
            player = new Player();
            player.setPosition(5, 5);
            player.setSize(1, 1);

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
        }

        batch.setProjectionMatrix(mapRenderer.getCamera().combined);

        // Mise à jour de la logique des ChainLinks
        updateChainLinks(delta);

        // Mise à jour de la TNT
        for (TNT tnt : tnts) {
            tnt.update(delta);
            if (player.getBounds().overlaps(tnt.getBounds())) {
                if (!player.isTank() && tnt.isPushable()) {
                    tnt.push(0.1f, 0);
                }
            }
        }

        // Mise à jour des FallingBlocks
        for (FallingBlock fb : fallingBlocks) {
            fb.update(delta);
        }

        // Mise à jour des Drawbridges
        for (Drawbridge db : drawbridges) {
            db.update(delta);
        }

        // Mise à jour des UnstablePlatforms
        for (UnstablePlatform up : unstablePlatforms) {
            up.update(delta, player.getBounds());
        }

        // Rendu de tous les objets
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
        for (IceBridge ice : iceBridges) {
            ice.render(batch);
        }
        for (ChainLink link : chainLinks) {
            link.render(batch);
        }
        for (Bastion bastion : bastions) {
            bastion.render(batch);
        }
        for (TNT tnt : tnts) {
            tnt.render(batch);
        }
        for (FallingBlock fb : fallingBlocks) {
            fb.render(batch);
        }
        for (Drawbridge db : drawbridges) {
            db.render(batch);
        }
        for (UnstablePlatform up : unstablePlatforms) {
            up.render(batch);
        }
        // Rendu du joueur (à compléter selon votre implémentation)
        batch.end();
    }

    private void updateChainLinks(float delta) {
        for (ChainLink link : chainLinks) {
            link.update(delta);
        }
        for (ChainLink link : chainLinks) {
            if (!link.isDestroyed() && !link.isFalling()) {
                String topName = link.getLinkedTop();
                if (topName != null) {
                    ChainLink topLink = chainLinkMap.get(topName.toLowerCase());
                    if (topLink == null || topLink.isDestroyed() || topLink.isFalling()) {
                        link.fall();
                    }
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
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
        if (mapManager != null) mapManager.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
        if (batch != null) batch.dispose();
    }
}
