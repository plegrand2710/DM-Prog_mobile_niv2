package com.upf.bastionbreaker.view.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class CheckPointScreen {
    private Stage stage;
    private SpriteBatch batch;
    private TextureAtlas gameAtlas;
    private int lastCheckpointReached = 2;
    private int totalCheckpoints = 4;
    private int selectedCheckpoint = -1;
    private PauseMenu pauseMenu;
    private boolean isVisible = false;

    public CheckPointScreen(PauseMenu pauseMenu) {
        this.pauseMenu = pauseMenu;
        Gdx.app.log("DEBUG_CHECKPOINT", "📌 Début de l'initialisation de CheckPointScreen");

        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();
        gameAtlas = new TextureAtlas(Gdx.files.internal("atlas/game/game.atlas"));

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        Gdx.input.setInputProcessor(stage);

        // 📌 Ajout du titre "Sélection du Checkpoint"
        Image title = new Image(new TextureRegionDrawable(gameAtlas.findRegion("Levels-Default")));
        title.setSize(screenWidth * 0.4f, screenHeight * 0.15f);
        title.setPosition((screenWidth - title.getWidth()) / 2, screenHeight * 0.8f);
        stage.addActor(title);
        Gdx.app.log("DEBUG_CHECKPOINT", "📌 Titre ajouté");

        // 📌 Position des étoiles (checkpoints)
        float starSize = screenWidth * 0.1f;
        float totalStarsWidth = totalCheckpoints * starSize + (totalCheckpoints - 1) * 20;
        float starsX = (screenWidth - totalStarsWidth) / 2;
        float starsY = screenHeight * 0.5f;

        ImageButton[] starButtons = new ImageButton[totalCheckpoints];

        for (int i = 0; i < totalCheckpoints; i++) {
            TextureRegion starRegion = (i <= lastCheckpointReached) ?
                gameAtlas.findRegion("Star-Star-Active") :
                gameAtlas.findRegion("Star-Star-Unactive");

            if (starRegion == null) {
                Gdx.app.error("DEBUG_CHECKPOINT", "❌ ERREUR: Texture d'étoile introuvable !");
                continue;
            }

            starButtons[i] = new ImageButton(new TextureRegionDrawable(starRegion));
            starButtons[i].setSize(starSize, starSize);
            starButtons[i].setPosition(starsX + i * (starSize + 20), starsY);

            final int checkpointIndex = i;
            starButtons[i].addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.log("DEBUG_CHECKPOINT", "⭐ Sélection du checkpoint " + (checkpointIndex + 1));
                    selectedCheckpoint = checkpointIndex;
                }
            });

            stage.addActor(starButtons[i]);
        }
        Gdx.app.log("DEBUG_CHECKPOINT", "✅ Checkpoints ajoutés");

        // 📌 Bouton "Valider"
        TextureRegion validateRegion = gameAtlas.findRegion("Play-Default");
        ImageButton validateButton = new ImageButton(new TextureRegionDrawable(validateRegion));
        validateButton.setSize(screenWidth * 0.2f, screenHeight * 0.1f);
        validateButton.setPosition((screenWidth - validateButton.getWidth()) / 2, screenHeight * 0.3f);

        validateButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedCheckpoint != -1) {
                    Gdx.app.log("DEBUG_CHECKPOINT", "🚀 Téléportation au checkpoint " + (selectedCheckpoint + 1));
                    teleportToCheckpoint(selectedCheckpoint);
                } else {
                    Gdx.app.log("DEBUG_CHECKPOINT", "⚠️ Aucun checkpoint sélectionné !");
                }
            }
        });

        stage.addActor(validateButton);
        Gdx.app.log("DEBUG_CHECKPOINT", "✅ Bouton de validation ajouté");

        // 📌 Bouton "Retour"
        TextureRegion backRegion = gameAtlas.findRegion("Home-Default");
        ImageButton backButton = new ImageButton(new TextureRegionDrawable(backRegion));
        backButton.setSize(screenWidth * 0.2f, screenHeight * 0.1f);
        backButton.setPosition((screenWidth - backButton.getWidth()) / 2, screenHeight * 0.15f);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("DEBUG_CHECKPOINT", "🔙 Retour au menu pause");
                toggleVisibility(false);
                pauseMenu.togglePause();
            }
        });

        stage.addActor(backButton);
        Gdx.app.log("DEBUG_CHECKPOINT", "✅ Bouton retour ajouté");

        Gdx.app.log("DEBUG_CHECKPOINT", "✅ Initialisation complète de CheckPointScreen");
    }

    private void teleportToCheckpoint(int checkpointIndex) {
        Gdx.app.log("DEBUG_CHECKPOINT", "🚀 Joueur téléporté au checkpoint " + (checkpointIndex + 1));
        toggleVisibility(false);
        pauseMenu.togglePause();
    }

    public void render(float delta) {
        if (!isVisible) return;
        Gdx.app.log("DEBUG_CHECKPOINT", "🖥️ CheckPointScreen en cours de rendu...");

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    public void toggleVisibility(boolean visible) {
        isVisible = visible;
        Gdx.input.setInputProcessor(visible ? stage : null);
        Gdx.app.log("DEBUG_CHECKPOINT", visible ? "👀 CheckPointScreen visible" : "🙈 CheckPointScreen caché");
    }

    public void dispose() {
        Gdx.app.log("DEBUG_CHECKPOINT", "🗑️ Destruction de CheckPointScreen");
        stage.dispose();
        gameAtlas.dispose();
        batch.dispose();
    }
}
