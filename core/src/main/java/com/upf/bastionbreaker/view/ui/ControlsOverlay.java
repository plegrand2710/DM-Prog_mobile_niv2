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

public class ControlsOverlay {
    private Stage stage;
    private Skin skin;

    // Touchpads pour mouvement et orientation
    private Touchpad movementTouchpad;
    private Touchpad aimTouchpad;

    // Boutons
    private TextButton jumpButton;
    private TextButton modeButton;

    private boolean showMovement; // Si true, on affiche le touchpad de mouvement

    public ControlsOverlay(boolean showMovement) {
        this.showMovement = showMovement;
        stage = new Stage();
        skin = new Skin();

        // Chargement des textures pour le touchpad
        skin.add("touchBackground", new Texture("assets/images/touchBackground.png"));
        skin.add("touchKnob", new Texture("assets/images/touchKnob.png"));
        TouchpadStyle touchpadStyle = new TouchpadStyle();
        Drawable background = skin.getDrawable("touchBackground");
        Drawable knob = skin.getDrawable("touchKnob");
        touchpadStyle.background = background;
        touchpadStyle.knob = knob;

        // Création du touchpad d'orientation (toujours affiché)
        aimTouchpad = new Touchpad(10, touchpadStyle);
        float screenWidth = Gdx.graphics.getWidth();
        aimTouchpad.setBounds(screenWidth - 170, 20, 150, 150);

        if (showMovement) {
            // Création du touchpad de mouvement (affiché uniquement si demandé)
            movementTouchpad = new Touchpad(10, touchpadStyle);
            movementTouchpad.setBounds(20, 20, 150, 150);
            // Utilisation d'une Table pour organiser le layout du touchpad de mouvement
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
        TextButtonStyle textButtonStyle = new TextButtonStyle();
        textButtonStyle.font = skin.getFont("default");

        // Création des boutons
        jumpButton = new TextButton("Jump", textButtonStyle);
        modeButton = new TextButton("Change Mode", textButtonStyle);
        jumpButton.setPosition(screenWidth - 200, Gdx.graphics.getHeight() - 100);
        modeButton.setPosition(screenWidth - 200, Gdx.graphics.getHeight() - 160);
        stage.addActor(jumpButton);
        stage.addActor(modeButton);

        // S'assurer que le stage reçoit les entrées
        Gdx.input.setInputProcessor(stage);
    }

    public void update(float delta) {
        stage.act(delta);
    }

    public void draw() {
        stage.draw();
    }

    // Si le touchpad de mouvement est affiché, renvoie sa valeur ; sinon, 0.
    public float getMovementKnobX() {
        return (showMovement && movementTouchpad != null) ? movementTouchpad.getKnobPercentX() : 0;
    }

    public float getMovementKnobY() {
        return (showMovement && movementTouchpad != null) ? movementTouchpad.getKnobPercentY() : 0;
    }

    // Le touchpad d'orientation est toujours affiché
    public float getAimKnobX() {
        return aimTouchpad.getKnobPercentX();
    }

    public float getAimKnobY() {
        return aimTouchpad.getKnobPercentY();
    }

    public boolean isJumpPressed() {
        return jumpButton.isPressed();
    }

    public boolean isModePressed() {
        return modeButton.isPressed();
    }

    // Nouvel accesseur pour le bouton "Change Mode"
    public TextButton getModeButton() {
        return modeButton;
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
