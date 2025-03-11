package com.upf.bastionbreaker.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.upf.bastionbreaker.model.entities.Floor;
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
    private List<Floor> floors;

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

            // Charger les Floor
            floors = mapManager.getFloors();

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

            // Initialiser le joueur à partir du checkpoint0
            // On cherche dans la liste des checkpoints celui dont le nom est "checkpoint0"
            float startX = 5, startY = 5; // Valeurs par défaut
            for (Checkpoint cp : checkpoints) {
                // Supposons que Checkpoint dispose d'une méthode getName() pour obtenir son identifiant
                if ("checkpoint0".equalsIgnoreCase(cp.toString()) || cp.toString().contains("checkpoint0")) {
                    // Pour cet exemple, nous utilisons les coordonnées du checkpoint0
                    startX = cp.getBoundingBox().x;
                    startY = cp.getBoundingBox().y;
                    break;
                }
            }
            player = new Player(startX, startY); // Démarrage à la position du checkpoint0, en mode Tank par défaut

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
        // Effacer l'écran AVANT tout rendu
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput(); // Gestion des entrées utilisateur

        // Appliquer la gravité sur le joueur
        applyPlayerGravity();

        // Mise à jour du joueur
        player.update(delta);

        // Mise à jour des autres objets
        updateChainLinks(delta);
        for (TNT tnt : tnts) {
            tnt.update(delta);
            if (player.getBoundingBox().overlaps(tnt.getBounds())) {
                // On vérifie que le joueur n'est pas en mode Tank (exemple de vérification)
                // (Vous pouvez ajuster cette condition selon votre implémentation)
                if (!player.getTexture().equals("tank") && tnt.isPushable()) {
                    tnt.push(0.1f, 0);
                }
            }
        }
        for (FallingBlock fb : fallingBlocks) {
            fb.update(delta);
        }
        for (Drawbridge db : drawbridges) {
            db.update(delta);
        }
        for (UnstablePlatform up : unstablePlatforms) {
            up.update(delta, player.getBoundingBox());
        }

        // Résolution des collisions avec le sol
        resolveFloorCollisions();

        // Rendre la carte
        if (mapRenderer != null) {
            mapRenderer.update(delta);
            mapRenderer.render();
        }

        batch.setProjectionMatrix(mapRenderer.getCamera().combined);
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
        // Rendu du joueur
        player.render(batch);
        // Optionnel : dessiner le Floor pour le debug
        // for (Floor floor : floors) {
        //     // batch.draw(floorTexture, floor.getBounds().x, floor.getBounds().y, floor.getBounds().width, floor.getBounds().height);
        // }
        batch.end();
    }


    /**
     * Gestion des entrées clavier pour le joueur.
     * D : avancer, A : reculer, SPACE : sauter (mode Robot), T : transformer.
     */
    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.setMovingForward(true);
        } else {
            player.setMovingForward(false);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.setMovingBackward(true);
        } else {
            player.setMovingBackward(false);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            player.jump();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            player.transform();
        }
    }

    /**
     * Applique la gravité au joueur et corrige sa position pour qu'il reste sur le sol.
     */
    private void applyPlayerGravity() {
        // Simple simulation de gravité : le joueur descend progressivement si rien ne le soutient.
        // Vous pouvez ajuster cette valeur selon vos besoins.
        float gravityForce = 0.05f;
        player.setPosition(player.getBoundingBox().x, player.getBoundingBox().y - gravityForce);
    }

    /**
     * Vérifie et résout les collisions entre le joueur et le Floor pour éviter qu'il ne traverse le sol.
     */
    private void resolveFloorCollisions() {
        for (Floor floor : floors) {
            float floorTop = floor.getBounds().y + floor.getBounds().height;
            if (player.getBoundingBox().overlaps(floor.getBounds())) {
                if (player.getBoundingBox().y < floorTop) {
                    player.setPosition(player.getBoundingBox().x, floorTop);
                }
            }
        }
    }

    /**
     * Met à jour les ChainLinks.
     */
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
