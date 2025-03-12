package com.upf.bastionbreaker.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class TouchpadController {

    private Stage stage;
    private Touchpad touchpad;
    private Skin skin;

    public TouchpadController(AssetManager assetManager) {
        // Création d'un Stage dédié au Touchpad avec un ScreenViewport
        stage = new Stage(new ScreenViewport());

        // Chargement des textures pour le Touchpad depuis le dossier assets/images
        Texture touchBackground = assetManager.get("images/touchBackground.png", Texture.class);
        Texture touchKnob = assetManager.get("images/touchKnob.png", Texture.class);

        // Création d'un Skin et ajout des Drawables
        skin = new Skin();
        skin.add("touchBackground", new TextureRegionDrawable(new TextureRegion(touchBackground)));
        skin.add("touchKnob", new TextureRegionDrawable(new TextureRegion(touchKnob)));

        // Configuration du style du Touchpad
        Touchpad.TouchpadStyle style = new Touchpad.TouchpadStyle();
        style.background = skin.getDrawable("touchBackground");
        style.knob = skin.getDrawable("touchKnob");

        // Création du Touchpad avec une zone morte (exemple : 10 pixels)
        touchpad = new Touchpad(10, style);

        // Positionnement du Touchpad (par exemple en bas à gauche de l'écran)
        touchpad.setBounds(20, 20, 150, 150); // ajuster la taille et la position selon vos besoins

        // Ajout du Touchpad au stage
        stage.addActor(touchpad);
    }

    // Méthode d'actualisation du stage
    public void update(float delta) {
        stage.act(delta);
    }

    // Méthode de dessin du stage (et donc du Touchpad)
    public void draw() {
        stage.draw();
    }

    // Permet d'accéder au Stage pour le définir comme InputProcessor
    public Stage getStage() {
        return stage;
    }

    // Accès aux informations du Touchpad (pour déplacer le joueur, par exemple)
    public float getKnobPercentX() {
        return touchpad.getKnobPercentX();
    }

    public float getKnobPercentY() {
        return touchpad.getKnobPercentY();
    }

    // Gestion du redimensionnement de l'écran
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
