package com.upf.bastionbreaker.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.upf.bastionbreaker.controller.input.TouchpadController;
import com.upf.bastionbreaker.model.audio.SoundManager;
import com.upf.bastionbreaker.model.entities.Tank;
import com.upf.bastionbreaker.model.entities.Bastion;
import com.upf.bastionbreaker.model.entities.Checkpoint;
import com.upf.bastionbreaker.model.entities.ChainLink;
import com.upf.bastionbreaker.model.entities.Drawbridge;
import com.upf.bastionbreaker.model.entities.FallingBlock;
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
import com.upf.bastionbreaker.model.physics.WorldManager;
import com.upf.bastionbreaker.view.ui.PauseMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameScreen implements Screen {
    private MapRenderer mapRenderer;
    private MapManager mapManager;
    private SpriteBatch batch;

    private OrthographicCamera camera;
    private Viewport viewport;
    private ParallaxBackground parallaxBackground;
    private TextureAtlas backgroundAtlas;

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

    // Dictionnaire pour les ChainLinks
    private Map<String, ChainLink> chainLinkMap;
    private PauseMenu menuPause;

    private Player player;

    // Contrôleurs d'entrée
    private ControlsOverlay controlsOverlay;
    private GyroscopeController gyroscopeController;
    private TouchpadController leftTouchpadController;
    private TouchpadController rightTouchpadController;

    // Mode d'entrée ("touchpad" ou "gyroscopic")
    private String inputMode;

    // Limites de la carte (en unités de tuiles)
    private float mapWidth = MapRenderer.MAP_WIDTH_TILES;
    private float mapHeight = MapRenderer.MAP_HEIGHT_TILES;

    // Box2D Debug renderer
    private Box2DDebugRenderer debugRenderer;
    private TextureAtlas gameAtlas;

    private Stage stage;

    public GameScreen(String inputMode, TextureAtlas gameAtlas) {
        this.inputMode = inputMode;
        this.gameAtlas = gameAtlas;
    }

    @Override
    public void show() {
        System.out.println("✅ Initialisation de GameScreen...");
        TextureManager.load();

        menuPause = new PauseMenu();
        stage = new Stage();
        backgroundAtlas = new TextureAtlas(Gdx.files.internal("atlas/background/background.atlas"));

        TextureRegion[] parallaxLayers = new TextureRegion[]{
            backgroundAtlas.findRegion("background", 1), // Fond le plus éloigné (ex: ciel)
            backgroundAtlas.findRegion("background", 2), // Second plan (ex: montagnes)
            backgroundAtlas.findRegion("background", 3), // Milieu (ex: arbres lointains)
            backgroundAtlas.findRegion("background", 4), // Premier plan (ex: arbres proches)
            backgroundAtlas.findRegion("background", 5)  // Très proche (ex: herbe, sol)
        };

        if (parallaxLayers[0] == null) Gdx.app.error("DEBUG_GAME", "❌ ERREUR : `background 1` introuvable !");
//        if (parallaxLayers[1] == null) Gdx.app.error("DEBUG_GAME", "❌ ERREUR : `background 2` introuvable !");
//        if (parallaxLayers[2] == null) Gdx.app.error("DEBUG_GAME", "❌ ERREUR : `background 3` introuvable !");
//        if (parallaxLayers[3] == null) Gdx.app.error("DEBUG_GAME", "❌ ERREUR : `background 4` introuvable !");
//        if (parallaxLayers[4] == null) Gdx.app.error("DEBUG_GAME", "❌ ERREUR : `background 5` introuvable !");


        float[] speeds = { 0.6f, 0.7f, 0.8f, 0.9f, 1.0f }; // Différentes vitesses (le plus proche bouge plus)

        parallaxBackground = new ParallaxBackground(parallaxLayers, speeds);



        MapManager.initialize("map/bastion_breaker_map.tmx");
        mapManager = MapManager.getInstance();

        if (mapManager == null) {
            Gdx.app.error("DEBUG_GAME", "❌ ERREUR CRITIQUE : `mapManager` est NULL après l'initialisation !");
            return;
        }

        try {
            if (mapManager.getTiledMap() == null) {
                Gdx.app.error("DEBUG_GAME", "❌ ERREUR : `mapManager.getTiledMap()` est NULL !");
                return;
            }

            mapRenderer = new MapRenderer(mapManager.getTiledMap());

            if (mapRenderer.getCamera() == null) {
                Gdx.app.error("DEBUG_GAME", "❌ ERREUR : `mapRenderer.getCamera()` est NULL !");
                return;
            }

            Gdx.app.log("DEBUG_GAME", "✅ MapRenderer chargé !");
        } catch (Exception e) {
            Gdx.app.error("DEBUG_GAME", "❌ ERREUR : Impossible de charger MapRenderer !", e);
            return;
        }

        if (inputMode.equalsIgnoreCase("touchpad")) {
            controlsOverlay = new ControlsOverlay(true);
            // Par exemple, le touchpad de gauche pour le mouvement
            leftTouchpadController = new TouchpadController(gameAtlas, 50, 50, 200, 200);
            // Et le touchpad de droite pour une action (exemple : viser ou autre)
            rightTouchpadController = new TouchpadController(gameAtlas, Gdx.graphics.getWidth() - 250, 50, 200, 200);

            // Combiner les stages avec un InputMultiplexer
            InputMultiplexer multiplexer = new InputMultiplexer();
            multiplexer.addProcessor(leftTouchpadController.getStage());
            multiplexer.addProcessor(rightTouchpadController.getStage());
            multiplexer.addProcessor(controlsOverlay.getStage());
            Gdx.input.setInputProcessor(multiplexer);
        }

        if (!inputMode.equalsIgnoreCase("touchpad")) {
            controlsOverlay = new ControlsOverlay(false);
            gyroscopeController = new GyroscopeController();
            // Ici, vous pouvez aussi ajouter le gyroscopeController dans un InputMultiplexer
            // si vous souhaitez combiner d'autres InputProcessors.
            Gdx.input.setInputProcessor(controlsOverlay.getStage());
        }



        controlsOverlay.getModeButton().addListener(new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                TransformationManager.getInstance().transform(player);
                // Dans une version physique, on n'ajuste plus manuellement la position
            }
        });


        controlsOverlay.getPauseButton().addListener(new com.badlogic.gdx.scenes.scene2d.utils.ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                System.out.println("⏸️ Pause activée !");
                menuPause.togglePause(controlsOverlay.getStage());
            }
        });

        try {

            // Charger les checkpoints
            checkpoints = new ArrayList<>();
            for (GameObject obj : mapManager.getCheckpoints()) {
                checkpoints.add(new Checkpoint(obj));
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
            // Création du joueur (qui va créer son body Box2D)
            try {
                player = new Player(startX, startY);
                if (player == null) {
                    throw new NullPointerException("❌ ERREUR CRITIQUE : `player` est NULL après instanciation !");
                }
                System.out.println("🟢 Joueur positionné au checkpoint0 : (" + startX + ", " + startY + ")");
            } catch (Exception e) {
                System.err.println("❌ ERREUR : Impossible de créer le joueur !");
                e.printStackTrace();
                return;
            }

            // Charger les autres objets (les obstacles créent leurs bodies Box2D)
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


            // Lier certains objets (comme dans votre version précédente)
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

        } catch (Exception e) {
            System.out.println("❌ ERREUR : Impossible de charger la carte !");
            e.printStackTrace();
        }


        // Initialiser la physique
        WorldManager.initialize();
        mapManager.createFloorBodies(WorldManager.getWorld());
        debugRenderer = new Box2DDebugRenderer();

//        camera = mapRenderer.getCamera(); // 🎯 On utilise la caméra de la map !
//        viewport = new FitViewport(1555, 720, camera);
//        viewport.apply();

        camera = new OrthographicCamera();
        // Dans GameScreen.show()
        viewport = new FitViewport(MapRenderer.VIEWPORT_WIDTH, MapRenderer.VIEWPORT_HEIGHT, camera);
        viewport.apply();
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        camera.update();

        camera.update();

        batch = new SpriteBatch();

    }

    @Override
    public void render(float delta) {
        if (player == null) {
            Gdx.app.error("DEBUG_GAME", "❌ ERREUR : `player` est NULL ! Impossible d'exécuter `render()`.");
            return;
        }

        if (menuPause.getIsVisible()) {
            menuPause.render(delta);  // ✅ Assurer le rendu du menu pause
            return;  // ❌ Ne pas exécuter le reste du jeu pendant la pause
        }

        // Mettre à jour la physique Box2D
        WorldManager.update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Sélection du mode d'input
        if (inputMode.equalsIgnoreCase("gyroscopic") && gyroscopeController != null) {
            // Appel du gyroscope pour déplacer le joueur
            gyroscopeController.handleInput(player, delta);
        } else {
            // Gestion du clavier et du touchpad
            handleInput();
        }

        player.update(delta);

        updateCameraPosition();

        camera.update();
        parallaxBackground.update(delta, camera);

        batch.begin();
        parallaxBackground.render(batch, camera);
        batch.end();
        mapRenderer.setView(camera);
        mapRenderer.render();
        batch.begin();

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

        if (inputMode.equalsIgnoreCase("touchpad")) {
            leftTouchpadController.update(delta);
            leftTouchpadController.draw();
            rightTouchpadController.update(delta);
            rightTouchpadController.draw();

            if (controlsOverlay != null) {
                controlsOverlay.update(delta);
                controlsOverlay.draw();
            }
        }

        if (inputMode.equalsIgnoreCase("gyroscopic") && gyroscopeController != null) {
            float gyroX = gyroscopeController.getMovementX();
            if (Math.abs(gyroX) > 0.05f) {
                player.move(gyroX);
                player.setMovingForward(gyroX > 0);
                player.setMovingBackward(gyroX < 0);

                if (controlsOverlay != null) {
                    controlsOverlay.update(delta);
                    controlsOverlay.draw();
                }
                if (controlsOverlay.getJumpButton().isPressed()) {
                    player.jump();
                }
            } else {
                player.setMovingForward(false);
                player.setMovingBackward(false);
            }
        } else {
            // Gestion du clavier et du touchpad
            handleInput();
        }


        // Affichage du debug de Box2D (optionnel)
        //debugRenderer.render(WorldManager.getWorld(), mapRenderer.getCamera().combined);


    }


    private void handleInput() {
        float moveX = 0;

        // Gestion clavier : valeurs brutes
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            moveX = 1;
            player.setMovingForward(true);
            player.setMovingBackward(false);
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            moveX = -1;
            player.setMovingBackward(true);
            player.setMovingForward(false);
        } else {
            player.setMovingForward(false);
            player.setMovingBackward(false);
        }

        if (inputMode.equalsIgnoreCase("touchpad") && leftTouchpadController != null) {
            float touchValue = leftTouchpadController.getKnobPercentX();  // Récupère l'input du stick
            if (Math.abs(touchValue) > 0.05f) {  // Seuil de détection
                moveX = touchValue * 2f;
            }
            if (controlsOverlay.getJumpButton().isPressed()) {
                player.jump();
            }
        }

        // Appliquer le mouvement avec la valeur normalisée
        player.move(moveX);

        // Saut avec la touche espace (si non utilisé via le touchpad)
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            player.jump();
        }

        // Transformation du mode
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            TransformationManager.getInstance().transform(player);
        }
        //Gdx.app.log("DEBUG_GAME", "🎮 Touchpad X: " + leftTouchpadController.getKnobPercentX());

    }


//    private void updateCameraPosition() {
//        OrthographicCamera cam = mapRenderer.getCamera();
//        float halfViewportWidth = cam.viewportWidth / 2;
//        float halfViewportHeight = cam.viewportHeight / 2;
//        float targetX = player.getX() + player.getCurrentMode().getWidth() / 2;
//        float targetY = player.getY() + player.getCurrentMode().getHeight() / 2;
//        cam.position.x = MathUtils.clamp(targetX, halfViewportWidth, mapWidth - halfViewportWidth);
//        cam.position.y = MathUtils.clamp(targetY, halfViewportHeight, mapHeight - halfViewportHeight);
//        cam.update();
//    }


    private void updateCameraPosition() {
        float halfViewportWidth = camera.viewportWidth / 2;
        float halfViewportHeight = camera.viewportHeight / 2;
        float targetX = player.getX() + player.getCurrentMode().getWidth() / 2;
        float targetY = player.getY() + player.getCurrentMode().getHeight() / 2;
        camera.position.x = MathUtils.clamp(targetX, halfViewportWidth, mapWidth - halfViewportWidth);
        camera.position.y = MathUtils.clamp(targetY, halfViewportHeight, mapHeight - halfViewportHeight);
        camera.update();
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
        if (mapRenderer == null) {
            Gdx.app.error("DEBUG_GAME", "❌ ERREUR : `mapRenderer` est NULL dans resize() !");
            return;
        }

        if (mapRenderer.getCamera() == null) {
            Gdx.app.error("DEBUG_GAME", "❌ ERREUR : `mapRenderer.getCamera()` est NULL dans resize() !");

            mapRenderer = new MapRenderer(mapManager.getTiledMap());

            if (mapRenderer.getCamera() == null) {
                Gdx.app.error("DEBUG_GAME", "❌ ERREUR CRITIQUE : `mapRenderer.getCamera()` reste NULL après réinitialisation !");
                return;
            }
        }

        Gdx.app.log("DEBUG_GAME", "📏 Resize détecté : width=" + width + ", height=" + height);
        mapRenderer.getCamera().viewportWidth = MapRenderer.VIEWPORT_WIDTH;
        mapRenderer.getCamera().viewportHeight = MapRenderer.VIEWPORT_HEIGHT;
        mapRenderer.getCamera().update();

        viewport.update(width, height, true);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();
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
        debugRenderer.dispose();
    }
}
