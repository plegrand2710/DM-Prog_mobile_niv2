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

    // Touchpads pour déplacement et orientation
    private Touchpad movementTouchpad;
    private Touchpad aimTouchpad;

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

        // Création du touchpad d'orientation (toujours affiché) en bas à droite
        aimTouchpad = new Touchpad(10, touchpadStyle);
        float screenWidth = Gdx.graphics.getWidth();
        aimTouchpad.setBounds(screenWidth - 170, 20, 150, 150);
        // Ajout du touchpad d'orientation au stage
        stage.addActor(aimTouchpad);

        if (showMovement) {
            // Création du touchpad de mouvement (affiché uniquement si demandé) en bas à gauche
            movementTouchpad = new Touchpad(10, touchpadStyle);
            movementTouchpad.setBounds(20, 20, 150, 150);
            Table table = new Table();
            table.setFillParent(true);
            table.bottom().left();
            table.add(movementTouchpad).pad(20).size(150);
            table.row();
            stage.addActor(table);
        }

        // Création d'un style de bouton par défaut
        BitmapFont font = new BitmapFont();
        skin.add("default", font);
        TextButtonStyle buttonStyle = new TextButtonStyle();
        buttonStyle.font = skin.getFont("default");

        // Création des boutons
        jumpButton = new TextButton("Jump", buttonStyle);
        modeButton = new TextButton("Change Mode", buttonStyle);
        shootButton = new TextButton("Shoot", buttonStyle);

        // Positionnement des boutons
        jumpButton.setPosition(screenWidth - 200, Gdx.graphics.getHeight() - 100);
        modeButton.setPosition(screenWidth - 200, Gdx.graphics.getHeight() - 160);
        shootButton.setPosition(screenWidth - 200, 200);

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

    // Accesseurs pour le touchpad de mouvement
    public float getMovementKnobX() {
        return (showMovement && movementTouchpad != null) ? movementTouchpad.getKnobPercentX() : 0;
    }

    public float getMovementKnobY() {
        return (showMovement && movementTouchpad != null) ? movementTouchpad.getKnobPercentY() : 0;
    }

    // Accesseurs pour le touchpad d'orientation
    public float getAimKnobX() {
        return aimTouchpad.getKnobPercentX();
    }

    public float getAimKnobY() {
        return aimTouchpad.getKnobPercentY();
    }

    // Accesseurs pour les boutons
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
