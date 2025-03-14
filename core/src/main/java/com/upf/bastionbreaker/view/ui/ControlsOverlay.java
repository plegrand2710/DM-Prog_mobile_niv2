package com.upf.bastionbreaker.view.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class ControlsOverlay {
    private Stage stage;
    private Skin skin;

    // Boutons
    private TextButton jumpButton;
    private TextButton modeButton;
    private TextButton shootButton;

    private boolean showMovement; // Si true, on affiche le touchpad de déplacement

    public ControlsOverlay(boolean showMovement) {
        this.showMovement = showMovement;
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        skin = new Skin();

        // Chargement des textures pour le touchpad
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

        // Positionnement des boutons modifié pour améliorer l'espacement :
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        // Déplacer "Jump" légèrement vers la gauche
        jumpButton.setPosition(screenWidth - 400, screenHeight - 150);
        // Descendre "Change Mode" pour qu'il soit en dessous du bouton Jump
        modeButton.setPosition(screenWidth - 400, screenHeight - 300);
        // Remonter "Shoot" pour une meilleure accessibilité
        shootButton.setPosition(screenWidth - 400, screenHeight - 450);

        stage.addActor(jumpButton);
        stage.addActor(modeButton);
        stage.addActor(shootButton);

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

    public Stage getStage() {
        return stage;
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
