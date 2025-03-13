package com.upf.bastionbreaker.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.upf.bastionbreaker.model.audio.SoundManager;
import com.upf.bastionbreaker.model.entities.Tank;
import com.upf.bastionbreaker.model.entities.Bastion;
import com.upf.bastionbreaker.model.entities.Checkpoint;
import com.upf.bastionbreaker.model.entities.ChainLink;
import com.upf.bastionbreaker.model.entities.Drawbridge;
import com.upf.bastionbreaker.model.entities.FallingBlock;
import com.upf.bastionbreaker.model.entities.Floor;
import com.upf.bastionbreaker.model.entities.FlyingBox;
import com.upf.bastionbreaker.model.entities.IceBridge;
import com.upf.bastionbreaker.model.entities.Obstacle;
import com.upf.bastionbreaker.model.entities.Player;
import com.upf.bastionbreaker.model.entities.TNT;
import com.upf.bastionbreaker.model.entities.UnstablePlatform;
import com.upf.bastionbreaker.model.map.GameObject;
import com.upf.bastionbreaker.model.map.MapManager;
import com.upf.bastionbreaker.model.graphics.TextureManager;
import com.upf.bastionbreaker.controller.input.GyroscopeController;
import com.upf.bastionbreaker.view.ui.ControlsOverlay;
import com.upf.bastionbreaker.controller.gameplay.TransformationManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameScreen implements Screen {
    private MapRenderer mapRenderer;
    private MapManager mapManager;
    private SpriteBatch batch;

    // Listes d'objets
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

    // Dictionnaire pour les ChainLinks
    private Map<String, ChainLink> chainLinkMap;

    private Player player;

    // Contrôleurs d'entrée
    private ControlsOverlay controlsOverlay;  // Gère l'affichage des touchpads et boutons
    private GyroscopeController gyroscopeController;

    // Mode d'entrée ("touchpad" ou "gyroscopic")
    private String inputMode;

    // Limites de la carte (en unités de tuiles)
    private float mapWidth = MapRenderer.MAP_WIDTH_TILES;
    private float mapHeight = MapRenderer.MAP_HEIGHT_TILES;

    public GameScreen(String inputMode) {
        this.inputMode = inputMode;
    }

    @Override
    public void show() {
        System.out.println("✅ Initialisation de GameScreen...");
        TextureManager.load();

        if (inputMode.equalsIgnoreCase("touchpad")) {
            controlsOverlay = new ControlsOverlay(true);
        } else {
            controlsOverlay = new ControlsOverlay(false);
        }
        gyroscopeController = new GyroscopeController();

        if (inputMode.equalsIgnoreCase("touchpad")) {
            controlsOverlay.getModeButton().addListener(new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {
                @Override
                public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                    TransformationManager.getInstance().transform(player);
                    player.setPosition(player.getX(), player.getY());
                }
            });
        }

        try {
            mapManager = new MapManager("assets/map/bastion_breaker_map.tmx");
            System.out.println("✅ MapManager chargé avec succès !");

            // Charger les checkpoints
            checkpoints = new ArrayList<>();
            for (GameObject obj : mapManager.getCheckpoints()) {
                checkpoints.add(new Checkpoint(obj));
            }

            // Charger les autres objets
            obstacles = new ArrayList<>();
            for (GameObject obj : mapManager.getObjects("Obstacles")) {
                obstacles.add(new Obstacle(obj));
            }
            flyingBoxes = new ArrayList<>();
            for (GameObject obj : mapManager.getObjects("FlyingBox")) {
                flyingBoxes.add(new FlyingBox(obj));
            }
            iceBridges = new ArrayList<>();
            for (GameObject obj : mapManager.getObjects("Ice")) {
                iceBridges.add(new IceBridge(obj));
            }
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
            bastions = new ArrayList<>();
            for (GameObject obj : mapManager.getObjects("Bastion")) {
                bastions.add(new Bastion(obj));
            }
            System.out.println("📌 Bastions chargés : " + bastions.size());
            tnts = new ArrayList<>();
            for (GameObject obj : mapManager.getObjects("Explosives")) {
                tnts.add(new TNT(obj));
            }
            System.out.println("📌 TNT chargées : " + tnts.size());
            fallingBlocks = new ArrayList<>();
            for (GameObject obj : mapManager.getObjects("FallingBlock")) {
                FallingBlock fb = new FallingBlock(obj);
                fallingBlocks.add(fb);
            }
            System.out.println("📌 FallingBlocks chargés : " + fallingBlocks.size());
            drawbridges = new ArrayList<>();
            for (GameObject obj : mapManager.getObjects("Drawbridges")) {
                Drawbridge db = new Drawbridge(obj);
                drawbridges.add(db);
            }
            System.out.println("📌 Drawbridges chargés : " + drawbridges.size());
            unstablePlatforms = new ArrayList<>();
            for (GameObject obj : mapManager.getObjects("UnstablePlatforms")) {
                UnstablePlatform up = new UnstablePlatform(obj);
                unstablePlatforms.add(up);
            }
            System.out.println("📌 UnstablePlatforms chargés : " + unstablePlatforms.size());
            floors = mapManager.getFloors();

            // Lier FallingBlocks, Drawbridges, UnstablePlatforms à leurs supports
            for (FallingBlock fb : fallingBlocks) {
                String topName = fb.getLinkedTopName();
                if (topName != null && !topName.isEmpty()) {
                    ChainLink support = chainLinkMap.get(topName.toLowerCase());
                    if (support != null) {
                        fb.setLinkedChain(support);
                    }
                }
            }
            for (Drawbridge db : drawbridges) {
                String supportName = db.getLinkedTopName();
                if (supportName != null && !supportName.isEmpty()) {
                    ChainLink support = chainLinkMap.get(supportName.toLowerCase());
                    if (support != null) {
                        db.setSupportingLink(support);
                    }
                }
            }
            for (UnstablePlatform up : unstablePlatforms) {
                String supportName = up.getLinkedTopName();
                if (supportName != null && !supportName.isEmpty()) {
                    ChainLink support = chainLinkMap.get(supportName.toLowerCase());
                    if (support != null) {
                        up.setLinkedChain(support);
                    }
                }
            }

            // Positionner le joueur à partir du checkpoint "checkpoint0"
            float startX = 5, startY = 5;
            for (Checkpoint cp : checkpoints) {
                if (cp.getName() != null && cp.getName().equalsIgnoreCase("checkpoint0")) {
                    startX = cp.getX();
                    startY = cp.getY();
                    break;
                }
            }
            player = new Player(startX, startY);
            System.out.println("🟢 Joueur positionné au checkpoint0 : (" + startX + ", " + startY + ")");

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
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput();

        // Mise à jour du joueur
        player.update(delta);
        // Gestion des collisions avec le sol
        handleFloorCollisions();
        // Mise à jour de la caméra pour suivre le joueur
        updateCameraPosition();

        // Mise à jour des objets dynamiques
        updateChainLinks(delta);
        for (TNT tnt : tnts) {
            tnt.update(delta);
            if (player.getBoundingBox().overlaps(tnt.getBounds())) {
                if (!(player.getCurrentMode() instanceof Tank) && tnt.isPushable()) {
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

        if (inputMode.equalsIgnoreCase("touchpad")) {
            float moveX = controlsOverlay.getMovementKnobX();
            if (Math.abs(moveX) > 0.1f) {
                mapRenderer.getCamera().translate(moveX * 0.02f, 0);
            }
        }

        // Ajustement dynamique du son du tank
        if (player.getCurrentMode() instanceof Tank) {
            float touchIntensity = inputMode.equalsIgnoreCase("touchpad")
                ? Math.abs(controlsOverlay.getMovementKnobX()) : 0;
            float volume = 0.4f + 0.3f * touchIntensity;
            SoundManager.adjustVolume("tank_engine", volume);
        }

        mapRenderer.update(delta);
        mapRenderer.render();

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
        player.render(batch);
        batch.end();

        if (inputMode.equalsIgnoreCase("touchpad") && controlsOverlay != null) {
            controlsOverlay.update(delta);
            controlsOverlay.draw();
        }

        checkHelicopterSound();
    }

    private void handleInput() {
        // 🟢 Gestion clavier
        boolean movingRight = Gdx.input.isKeyPressed(Input.Keys.D);
        boolean movingLeft = Gdx.input.isKeyPressed(Input.Keys.A);

        if (movingRight) {
            player.move(1 * Gdx.graphics.getDeltaTime()); // Déplacement fluide
            player.setMovingForward(true);
            player.setMovingBackward(false);
        } else if (movingLeft) {
            player.move(-1 * Gdx.graphics.getDeltaTime());
            player.setMovingBackward(true);
            player.setMovingForward(false);
        } else {
            player.setMovingForward(false);
            player.setMovingBackward(false);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            player.jump();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            TransformationManager.getInstance().transform(player);
        }

        // 🟢 Gestion Touchpad (ne pas écraser clavier)
        if (inputMode.equalsIgnoreCase("touchpad")) {
            float moveX = controlsOverlay.getMovementKnobX();

            if (Math.abs(moveX) > 0.1f) {
                float adjustedSpeed = moveX * 0.5f * Gdx.graphics.getDeltaTime();
                player.move(adjustedSpeed);

                // 🔹 Active le son et le mouvement
                player.setMovingForward(moveX > 0);
                player.setMovingBackward(moveX < 0);
            } else {
                player.setMovingForward(false);
                player.setMovingBackward(false);
            }

            if (controlsOverlay.getJumpButton().isPressed()) {
                player.jump();
            }
        }
    }




    private void handleFloorCollisions() {
        boolean onGround = false;

        for (Floor floor : floors) {
            if (player.getBoundingBox().overlaps(floor.getBounds())) {
                float floorTop = floor.getBounds().y + floor.getBounds().height;
                if (player.getY() < floorTop) {
                    player.setPosition(player.getX(), floorTop);
                    onGround = true;
                }
            }
        }

        player.setOnGround(onGround); // ✅ Met à jour `isOnGround` !
    }


    /**
     * Met à jour la position de la caméra pour suivre le joueur sans sortir de la carte.
     */
    private void updateCameraPosition() {
        OrthographicCamera cam = mapRenderer.getCamera();
        float halfViewportWidth = cam.viewportWidth / 2;
        float halfViewportHeight = cam.viewportHeight / 2;
        float targetX = player.getX() + player.getCurrentMode().getWidth() / 2;
        float targetY = player.getY() + player.getCurrentMode().getHeight() / 2;
        cam.position.x = MathUtils.clamp(targetX, halfViewportWidth, mapWidth - halfViewportWidth);
        cam.position.y = MathUtils.clamp(targetY, halfViewportHeight, mapHeight - halfViewportHeight);
        cam.update();
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

    private void checkHelicopterSound() {
        boolean helicopterInView = false;
        for (GameObject enemy : mapManager.getObjects("Enemies")) {
            String sprite = enemy.getProperty("sprite", String.class);
            if (sprite != null && sprite.equalsIgnoreCase("helicopter.gif")) {
                float ex = enemy.getX() / MapRenderer.TILE_SIZE;
                float ey = enemy.getY() / MapRenderer.TILE_SIZE;
                if (mapRenderer.getCamera().frustum.pointInFrustum(ex, ey, 0)) {
                    helicopterInView = true;
                    break;
                }
            }
        }
        if (helicopterInView) {
            SoundManager.playLoopingSound("helicopter", 0.6f);
        } else {
            SoundManager.stopSound("helicopter");
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
        if (controlsOverlay != null) controlsOverlay.dispose();
    }
}
