package com.upf.bastionbreaker.controller.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class TouchpadController {
    private Stage stage;
    private Touchpad touchpad;
    private TouchpadStyle touchpadStyle;
    private Skin touchpadSkin;
    private Drawable touchBackground;
    private Drawable touchKnob;

    public TouchpadController() {
        // Création du Skin avec les textures du Touchpad
        touchpadSkin = new Skin();
        touchpadSkin.add("touchBackground", new Texture("assets/images/touchBackground.png"));
        touchpadSkin.add("touchKnob", new Texture("assets/images/touchKnob.png"));

        // Création du style du Touchpad
        touchpadStyle = new TouchpadStyle();
        touchBackground = touchpadSkin.getDrawable("touchBackground");
        touchKnob = touchpadSkin.getDrawable("touchKnob");
        touchpadStyle.background = touchBackground;
        touchpadStyle.knob = touchKnob;

        // Création du Touchpad avec une deadzone de 10
        touchpad = new Touchpad(10, touchpadStyle);
        // Position et taille réduite pour être visible en bas à gauche
        touchpad.setBounds(20, 20, 150, 150);

        // Création du Stage et ajout du Touchpad
        stage = new Stage();
        stage.addActor(touchpad);
        // Mettre le Touchpad au premier plan
        touchpad.setZIndex(stage.getRoot().getChildren().size - 1);
        Gdx.input.setInputProcessor(stage);
    }

    public void update(float delta) {
        stage.act(delta);
    }

    public void draw() {
        stage.draw();
    }

    public float getKnobPercentX() {
        return touchpad.getKnobPercentX();
    }

    public float getKnobPercentY() {
        return touchpad.getKnobPercentY();
    }

    public void dispose() {
        stage.dispose();
        touchpadSkin.dispose();
    }
}
