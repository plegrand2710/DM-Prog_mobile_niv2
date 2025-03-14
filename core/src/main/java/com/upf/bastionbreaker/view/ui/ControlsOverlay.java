package com.upf.bastionbreaker.view.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class ControlsOverlay {
    private Stage stage;
    private Skin skin;

    // Boutons
    private TextButton jumpButton;
    private TextButton modeButton;
    private TextButton shootButton;
    private TextureAtlas buttonAtlas;
    private ImageButton pauseButton; // Bouton Pause

    private boolean showMovement; // Si true, on affiche le touchpad de déplacement

    public ControlsOverlay(boolean showMovement) {
        this.showMovement = showMovement;
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        skin = new Skin();
        buttonAtlas = new TextureAtlas(Gdx.files.internal("atlas/game/game.atlas"));

        TextureRegion pauseRegion = buttonAtlas.findRegion("Icon-Pause");

        if (pauseRegion == null) {
            Gdx.app.error("DEBUG_UI", "❌ ERREUR : L'image 'pause' n'a pas été trouvée dans l'Atlas !");
        }

        skin.add("touchBackground", new Texture("images/touchBackground.png"));
        skin.add("touchKnob", new Texture("images/touchKnob.png"));
        TouchpadStyle touchpadStyle = new TouchpadStyle();
        Drawable background = skin.getDrawable("touchBackground");
        Drawable knob = skin.getDrawable("touchKnob");
        touchpadStyle.background = background;
        touchpadStyle.knob = knob;

        if (showMovement) {
            Table table = new Table();
            table.setFillParent(true);
            table.bottom().left();
            table.row();
            stage.addActor(table);
        }

        pauseButton = new ImageButton(new TextureRegionDrawable(pauseRegion));

        // Positionnement en haut à gauche
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        pauseButton.setPosition(50, screenHeight - 100);
        pauseButton.setSize(80, 80); // Ajuste la taille si nécessaire

        // Création d'un style de bouton par défaut
        BitmapFont font = new BitmapFont();
        skin.add("default", font);
        TextButtonStyle buttonStyle = new TextButtonStyle();
        buttonStyle.font = skin.getFont("default");

        jumpButton = new TextButton("Jump", buttonStyle);
        modeButton = new TextButton("Change Mode", buttonStyle);
        shootButton = new TextButton("Shoot", buttonStyle);

        jumpButton.setTransform(true);
        jumpButton.setScale(3f);
        modeButton.setTransform(true);
        modeButton.setScale(3f);
        shootButton.setTransform(true);
        shootButton.setScale(3f);


        jumpButton.setPosition(screenWidth - 400, screenHeight - 150);
        modeButton.setPosition(screenWidth - 400, screenHeight - 300);
        shootButton.setPosition(screenWidth - 400, screenHeight - 450);

        stage.addActor(jumpButton);
        stage.addActor(modeButton);
        stage.addActor(shootButton);
        stage.addActor(pauseButton);

        Gdx.input.setInputProcessor(stage);
    }

    public void update(float delta) {
        stage.act(delta);
    }

    public void draw() {
        stage.draw();
    }

    public TextButton getJumpButton() {
        return jumpButton;
    }

    public TextButton getModeButton() {
        return modeButton;
    }

    public TextButton getShootButton() {
        return shootButton;
    }

    public ImageButton getPauseButton() { return pauseButton; }
    public Stage getStage() {
        return stage;
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
