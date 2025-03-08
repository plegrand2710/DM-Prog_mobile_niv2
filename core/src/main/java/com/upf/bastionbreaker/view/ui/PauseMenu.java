package com.upf.bastionbreaker.view.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class PauseMenu {
    private Stage stage;
    private TextureAtlas gameAtlas;
    private ImageButton resumeButton, optionsButton, quitButton;
    private BitmapFont font;
    private SpriteBatch batch;
    private boolean isVisible = false;

    private Texture blurredTexture;
    private int lastCheckpointReached = 2;



    public PauseMenu() {
        Gdx.app.log("DEBUG_PAUSE", "📌 Début de l'initialisation du menu pause");

        stage = new Stage(new ScreenViewport());
        batch = new SpriteBatch();

        gameAtlas = new TextureAtlas(Gdx.files.internal("atlas/game/game.atlas"));

        TextureRegion buttonPlay = gameAtlas.findRegion("Play-Default");
        TextureRegion buttonOptions = gameAtlas.findRegion("Levels-Default");
        TextureRegion buttonExit = gameAtlas.findRegion("Home-Default");


        if (buttonPlay == null || buttonOptions == null || buttonExit == null) {
            Gdx.app.error("DEBUG_PAUSE", "❌ ERREUR: Textures introuvable !");
        }

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        TextureRegionDrawable containerDrawable = new TextureRegionDrawable(gameAtlas.findRegion("Button-Dummy@2x"));
        Image menuContainer = new Image(containerDrawable);

        float containerScale = 2.7f;
        float containerWidth = containerDrawable.getRegion().getRegionWidth() * containerScale;
        float containerHeight = containerDrawable.getRegion().getRegionHeight() * containerScale;

        float containerX = (Gdx.graphics.getWidth() - containerWidth) / 2;
        float containerY = (0.3472f * screenHeight) - 200;

        menuContainer.setSize(containerWidth, containerHeight);
        menuContainer.setPosition(containerX, containerY);
        stage.addActor(menuContainer);



        float buttonWidth = containerWidth * 0.6f;
        float buttonHeight = containerHeight * 0.15f;
        float buttonX = containerX + (containerWidth - buttonWidth) / 5.5f;
        float startY = (screenHeight - containerHeight) / 2 + containerHeight * 0.65f;

        resumeButton = new ImageButton(new TextureRegionDrawable(buttonPlay));
        resumeButton.setSize(buttonWidth, buttonHeight);
        resumeButton.setPosition(buttonX, startY);
        stage.addActor(resumeButton);

        optionsButton = new ImageButton(new TextureRegionDrawable(buttonOptions));
        optionsButton.setSize(buttonWidth, buttonHeight);
        optionsButton.setPosition(buttonX, startY - (buttonHeight - buttonHeight/8));
        stage.addActor(optionsButton);

        quitButton = new ImageButton(new TextureRegionDrawable(buttonExit));
        quitButton.setSize(buttonWidth, buttonHeight);
        quitButton.setPosition(buttonX, startY - 2 * ((buttonHeight - buttonHeight/8) + 1));
        stage.addActor(quitButton);


        font = new BitmapFont();
        font.getData().setScale(2);
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;

        float labelXOffset = buttonWidth * 0.7f;

        Label resumeLabel = new Label("Reprendre", labelStyle);
        resumeLabel.setPosition(buttonX + labelXOffset, startY + buttonHeight / 3f);
        stage.addActor(resumeLabel);

        Label optionsLabel = new Label("CheckPoint", labelStyle);
        optionsLabel.setPosition(buttonX + labelXOffset, startY - (buttonHeight - buttonHeight / 8) + buttonHeight / 3f);
        stage.addActor(optionsLabel);

        Label quitLabel = new Label("Menu", labelStyle);
        quitLabel.setPosition(buttonX + labelXOffset, startY - 2 * ((buttonHeight - buttonHeight / 8) + 1) + buttonHeight / 3f);
        stage.addActor(quitLabel);



        int starCount = 4;
        float starSize = containerWidth * 0.12f;
        float totalStarsWidth = starCount * starSize + (starCount - 1) * 10;
        float starsX = containerX + (containerWidth - totalStarsWidth) / 2;
        float starsY = containerY + (containerHeight * 0.15f);

        for (int i = 0; i < starCount; i++) {
            TextureRegion starRegion = (i < lastCheckpointReached) ?
                gameAtlas.findRegion("Star-Star-Active") :
                gameAtlas.findRegion("Star-Star-Unactive");

            if (starRegion == null) {
                Gdx.app.error("DEBUG_PAUSE", "❌ ERREUR: La texture de l'étoile est introuvable !");
                continue;
            }

            Image star = new Image(new TextureRegionDrawable(starRegion));
            star.setSize(starSize, starSize);
            star.setPosition(starsX + i * (starSize + 10), starsY);
            stage.addActor(star);
        }



        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                Gdx.app.log("DEBUG_PAUSE", "🟢 Bouton 'Reprendre' cliqué");
                togglePause();
            }
        });

        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                Gdx.app.log("DEBUG_PAUSE", "🟡 Options ouvertes");
            }
        });

        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        Gdx.app.log("DEBUG_PAUSE", "✅ Initialisation complète du menu pause");
    }

    private void captureScreen() {
        Gdx.app.log("DEBUG_PAUSE", "📸 Capture d'écran pour le fond du menu pause...");

        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(0, 0, width, height);
        if (pixmap == null) {
            Gdx.app.log("DEBUG_PAUSE", "❌ Échec de la capture de l'écran !");
            return;
        }

        Pixmap flippedPixmap = new Pixmap(width, height, pixmap.getFormat());
        for (int y = 0; y < height; y++) {
            flippedPixmap.drawPixmap(pixmap, 0, y, width, 1, 0, height - y - 1, width, 1);
        }
        pixmap.dispose();

        if (blurredTexture != null) blurredTexture.dispose();
        blurredTexture = new Texture(flippedPixmap);
        blurredTexture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        flippedPixmap.dispose();

        Gdx.app.log("DEBUG_PAUSE", "✅ Capture de l'écran réussie et corrigée !");
    }


    public void render(float delta) {
        if (!isVisible) return;


        if (blurredTexture == null) {
            captureScreen();
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.setColor(1, 1, 1, 0.6f);
        if (blurredTexture != null) {
            batch.draw(blurredTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
        batch.setColor(1, 1, 1, 1);
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    public void togglePause() {
        isVisible = !isVisible;
        Gdx.input.setInputProcessor(isVisible ? stage : null);
        blurredTexture = null;
        Gdx.app.log("DEBUG_PAUSE", "🔁 Toggle pause : " + isVisible);
    }

    public void dispose() {
        Gdx.app.log("DEBUG_PAUSE", "🗑️ Destruction du menu pause");
        stage.dispose();
        gameAtlas.dispose();
        font.dispose();
        if (blurredTexture != null) blurredTexture.dispose();
        batch.dispose();
    }
}
