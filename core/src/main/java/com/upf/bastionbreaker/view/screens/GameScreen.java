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
import com.upf.bastionbreaker.view.ui.PauseMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameScreen implements Screen {
    private MapRenderer mapRenderer;
    private MapManager mapManager;
    private SpriteBatch batch;
    private PauseMenu pauseMenu;
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
        this.pauseMenu = new PauseMenu();
        this.inputMode = inputMode;
    }

    @Override
    public void show() {
        Gdx.app.log("DEBUG_GAME", "✅ Initialisation de GameScreen...");
        TextureManager.load();

        if (inputMode.equalsIgnoreCase("touchpad")) {
            Gdx.app.log("DEBUG_GAME", "🎮 Mode de contrôle : Touchpad activé");
            controlsOverlay = new ControlsOverlay(true);
        } else {
            Gdx.app.log("DEBUG_GAME", "🎮 Mode de contrôle : Gyroscope activé");
            controlsOverlay = new ControlsOverlay(false);
        }
        gyroscopeController = new GyroscopeController();

        if (inputMode.equalsIgnoreCase("touchpad")) {
            controlsOverlay.getModeButton().addListener(new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {
                @Override
                public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                    Gdx.app.log("DEBUG_GAME", "🔄 Transformation du joueur");
                    TransformationManager.getInstance().transform(player);
                    player.setPosition(player.getX(), player.getY());
                }
            });
        }

        controlsOverlay.getPauseButton().addListener(new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                Gdx.app.log("DEBUG_GAME", "⏸️ Bouton Pause Pressé !");
                pauseMenu.togglePause(controlsOverlay.getStage());
            }
        });
        try {
            Gdx.app.log("DEBUG_GAME", "📜 Chargement de la carte...");
            mapManager = new MapManager("map/bastion_breaker_map.tmx");
            Gdx.app.log("DEBUG_GAME", "✅ MapManager chargé avec succès !");

            try {
                Gdx.app.log("DEBUG_GAME", "🗺️ Chargement du MapRenderer...");
                mapRenderer = new MapRenderer(mapManager.getTiledMap());
                Gdx.app.log("DEBUG_GAME", "✅ Map rendue avec succès !");
            } catch (Exception e) {
                Gdx.app.error("DEBUG_GAME", "❌ ERREUR : Impossible de charger MapRenderer !", e);
            }

            // Chargement des objets du jeu

            floors = mapManager.getFloors();
            if (floors == null) {
                Gdx.app.error("DEBUG_GAME", "❌ ERREUR : La liste `floors` est NULL après l'initialisation !");
            } else {
                //Gdx.app.log("DEBUG_GAME", "✅ Floors chargés : " + floors.size());
            }

            Gdx.app.log("DEBUG_GAME", "📥 Chargement des checkpoints...");
            checkpoints = new ArrayList<>();
            for (GameObject obj : mapManager.getCheckpoints()) {
                checkpoints.add(new Checkpoint(obj));
            }
            Gdx.app.log("DEBUG_GAME", "✅ Checkpoints chargés : " + checkpoints.size());

            Gdx.app.log("DEBUG_GAME", "📥 Chargement des obstacles...");
            obstacles = new ArrayList<>();
            for (GameObject obj : mapManager.getObjects("Obstacles")) {
                obstacles.add(new Obstacle(obj));
            }
            Gdx.app.log("DEBUG_GAME", "✅ Obstacles chargés : " + obstacles.size());

            Gdx.app.log("DEBUG_GAME", "📥 Chargement des FlyingBoxes...");
            flyingBoxes = new ArrayList<>();
            for (GameObject obj : mapManager.getObjects("FlyingBox")) {
                flyingBoxes.add(new FlyingBox(obj));
            }
            Gdx.app.log("DEBUG_GAME", "✅ FlyingBoxes chargées : " + flyingBoxes.size());

            // Gestion des ChainLinks
            Gdx.app.log("DEBUG_GAME", "📥 Chargement des ChainLinks...");
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
            Gdx.app.log("DEBUG_GAME", "✅ ChainLinks chargés : " + chainLinks.size());

            // Gestion des Bastions
            Gdx.app.log("DEBUG_GAME", "📥 Chargement des Bastions...");
            bastions = new ArrayList<>();
            for (GameObject obj : mapManager.getObjects("Bastion")) {
                bastions.add(new Bastion(obj));
            }
            Gdx.app.log("DEBUG_GAME", "✅ Bastions chargés : " + bastions.size());

            // Gestion des TNTs
            Gdx.app.log("DEBUG_GAME", "📥 Chargement des TNT...");
            tnts = new ArrayList<>();
            for (GameObject obj : mapManager.getObjects("Explosives")) {
                tnts.add(new TNT(obj));
            }
            Gdx.app.log("DEBUG_GAME", "✅ TNTs chargées : " + tnts.size());

            // Initialisation du joueur
            float startX = 5, startY = 5;
            for (Checkpoint cp : checkpoints) {
                if (cp.getName() != null && cp.getName().equalsIgnoreCase("checkpoint0")) {
                    startX = cp.getX();
                    startY = cp.getY();
                    break;
                }
            }

            Gdx.app.log("DEBUG_GAME", "📥 Vérification des IceBridges...");
            List<GameObject> iceObjects = mapManager.getObjects("Ice");
            if (iceObjects == null) {
                Gdx.app.error("DEBUG_GAME", "❌ ERREUR : mapManager.getObjects('Ice') a retourné NULL !");
            } else {
                Gdx.app.log("DEBUG_GAME", "✅ Objets Ice trouvés : " + iceObjects.size());
            }
            iceBridges = (iceObjects != null) ? new ArrayList<>() : new ArrayList<>();
            if (iceObjects != null) {
                for (GameObject obj : iceObjects) {
                    iceBridges.add(new IceBridge(obj));
                }
            }
            Gdx.app.log("DEBUG_GAME", "✅ IceBridges chargés : " + iceBridges.size());

            Gdx.app.log("DEBUG_GAME", "📥 Vérification des FallingBlocks...");
            List<GameObject> fallingBlockObjects = mapManager.getObjects("FallingBlock");
            if (fallingBlockObjects == null) {
                Gdx.app.error("DEBUG_GAME", "❌ ERREUR : mapManager.getObjects('FallingBlock') a retourné NULL !");
            } else {
                Gdx.app.log("DEBUG_GAME", "✅ Objets FallingBlock trouvés : " + fallingBlockObjects.size());
            }
            fallingBlocks = (fallingBlockObjects != null) ? new ArrayList<>() : new ArrayList<>();
            if (fallingBlockObjects != null) {
                for (GameObject obj : fallingBlockObjects) {
                    fallingBlocks.add(new FallingBlock(obj));
                }
            }
            Gdx.app.log("DEBUG_GAME", "✅ FallingBlocks chargés : " + fallingBlocks.size() );

            Gdx.app.log("DEBUG_GAME", "📥 Vérification des Drawbridges...");
            List<GameObject> drawbridgeObjects = mapManager.getObjects("Drawbridges");
            if (drawbridgeObjects == null) {
                Gdx.app.error("DEBUG_GAME", "❌ ERREUR : mapManager.getObjects('Drawbridges') a retourné NULL !");
            } else {
                Gdx.app.log("DEBUG_GAME", "✅ Objets Drawbridges trouvés : " + drawbridgeObjects.size());
            }
            drawbridges = (drawbridgeObjects != null) ? new ArrayList<>() : new ArrayList<>();
            if (drawbridgeObjects != null) {
                for (GameObject obj : drawbridgeObjects) {
                    drawbridges.add(new Drawbridge(obj));
                }
            }
            Gdx.app.log("DEBUG_GAME", "✅ Drawbridges chargés : " + drawbridges.size());

            Gdx.app.log("DEBUG_GAME", "📥 Vérification des UnstablePlatforms...");
            List<GameObject> unstablePlatformObjects = mapManager.getObjects("UnstablePlatforms");
            if (unstablePlatformObjects == null) {
                Gdx.app.error("DEBUG_GAME", "❌ ERREUR : mapManager.getObjects('UnstablePlatforms') a retourné NULL !");
            } else {
                Gdx.app.log("DEBUG_GAME", "✅ Objets UnstablePlatforms trouvés : " + unstablePlatformObjects.size());
            }
            unstablePlatforms = (unstablePlatformObjects != null) ? new ArrayList<>() : new ArrayList<>();
            if (unstablePlatformObjects != null) {
                for (GameObject obj : unstablePlatformObjects) {
                    unstablePlatforms.add(new UnstablePlatform(obj));
                }
            }
            Gdx.app.log("DEBUG_GAME", "✅ UnstablePlatforms chargés : " + unstablePlatforms.size());

            player = new Player(startX, startY);
            Gdx.app.log("DEBUG_GAME", "🟢 Joueur positionné au checkpoint0 : (" + startX + ", " + startY + ")");

        } catch (Exception e) {
            Gdx.app.error("DEBUG_GAME", "❌ ERREUR : Impossible de charger la carte !", e);
        }

        batch = new SpriteBatch();
        Gdx.app.log("DEBUG_GAME", "✅ Initialisation de GameScreen terminée !");
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (obstacles == null) Gdx.app.error("DEBUG_GAME", "❌ ERREUR : obstacles est NULL !");
        if (checkpoints == null) Gdx.app.error("DEBUG_GAME", "❌ ERREUR : checkpoints est NULL !");
        if (flyingBoxes == null) Gdx.app.error("DEBUG_GAME", "❌ ERREUR : flyingBoxes est NULL !");
        if (iceBridges == null) Gdx.app.error("DEBUG_GAME", "❌ ERREUR : iceBridges est NULL !");
        if (chainLinks == null) Gdx.app.error("DEBUG_GAME", "❌ ERREUR : chainLinks est NULL !");
        if (bastions == null) Gdx.app.error("DEBUG_GAME", "❌ ERREUR : bastions est NULL !");
        if (tnts == null) Gdx.app.error("DEBUG_GAME", "❌ ERREUR : tnts est NULL !");
        if (fallingBlocks == null) Gdx.app.error("DEBUG_GAME", "❌ ERREUR : fallingBlocks est NULL !");
        if (drawbridges == null) Gdx.app.error("DEBUG_GAME", "❌ ERREUR : drawbridges est NULL !");
        if (unstablePlatforms == null) Gdx.app.error("DEBUG_GAME", "❌ ERREUR : unstablePlatforms est NULL !");

        if (pauseMenu.getIsVisible()) {
            pauseMenu.render(delta);
            return;
        }
        handleInput();
        handleTouchpadInput();
        handleGyroscopeInput(delta);
        // Mise à jour du joueur
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

        floors = mapManager.getFloors();
        if (floors == null) {
            Gdx.app.error("DEBUG_GAME", "❌ ERREUR : La liste `floors` est NULL après l'initialisation !");
        } else {
            //Gdx.app.log("DEBUG_GAME", "✅ Floors chargés : " + floors.size());
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
        player.update(delta);

        batch.end();

        if (inputMode.equalsIgnoreCase("touchpad") && controlsOverlay != null) {
            controlsOverlay.update(delta);
            controlsOverlay.draw();
        }

        checkHelicopterSound();
    }

    private void handleInput() {
        boolean movingRight = Gdx.input.isKeyPressed(Input.Keys.D);
        boolean movingLeft = Gdx.input.isKeyPressed(Input.Keys.A);

        if (movingRight) {
            player.move(1 * Gdx.graphics.getDeltaTime());
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
        if (inputMode.equalsIgnoreCase("gyroscopic")) {
            handleGyroscopeInput(Gdx.graphics.getDeltaTime());
        } else {
            handleTouchpadInput();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            player.jump();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            TransformationManager.getInstance().transform(player);
        }
    }


    private void handleTouchpadInput() {
        float moveX = controlsOverlay.getMovementKnobX();

        if (Math.abs(moveX) > 0.1f) {
            float adjustedSpeed = moveX * 0.5f * Gdx.graphics.getDeltaTime();
            player.move(adjustedSpeed);

            // 🔹 Active l'animation du joueur
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

    private void handleGyroscopeInput(float delta) {
        if (inputMode.equalsIgnoreCase("touchpad")) return;  // ❌ Bloque le gyroscope en mode Touchpad

        if (!hasGyroscope()) return;  // ❌ Ne rien faire si pas d'accéléromètre

        float tilt = Gdx.input.getAccelerometerY(); // Détecte l'inclinaison gauche/droite

        float threshold = 1.0f;  // Sensibilité minimale pour détecter un mouvement
        float maxSpeed = 4.0f;   // Vitesse maximale en unités par seconde

        if (Math.abs(tilt) > threshold) {
            float movementSpeed = MathUtils.clamp(tilt * 0.5f, -maxSpeed, maxSpeed);  // Calcule la vitesse
            player.move(movementSpeed * delta);

            // 🔹 Ajuste l'animation du joueur
            player.setMovingForward(tilt > 0);
            player.setMovingBackward(tilt < 0);
        } else {
            player.setMovingForward(false);
            player.setMovingBackward(false);
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

    private boolean hasGyroscope() {
        return Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer);
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
            //SoundManager.playLoopingSound("helicopter", 0.6f);
        } else {
            //SoundManager.stopSound("helicopter");
        }
    }

    @Override
    public void resize(int width, int height) {
        if (mapRenderer == null) {
            Gdx.app.error("DEBUG_GAME", "❌ ERREUR : mapRenderer est NULL dans resize() !");
            return;
        }

        if (mapRenderer.getCamera() == null) {
            Gdx.app.error("DEBUG_GAME", "❌ ERREUR : mapRenderer.getCamera() est NULL dans resize() !");

            // 💡 Solution : Forcer la réinitialisation de la caméra ici
            mapRenderer = new MapRenderer(mapManager.getTiledMap());

            if (mapRenderer.getCamera() == null) {
                Gdx.app.error("DEBUG_GAME", "❌ ERREUR CRITIQUE : mapRenderer.getCamera() reste NULL après réinitialisation !");
                return;
            }
        }

        Gdx.app.log("DEBUG_GAME", "📏 Resize détecté : width=" + width + ", height=" + height);
        mapRenderer.getCamera().viewportWidth = MapRenderer.VIEWPORT_WIDTH;
        mapRenderer.getCamera().viewportHeight = MapRenderer.VIEWPORT_HEIGHT;
        mapRenderer.getCamera().update();
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
