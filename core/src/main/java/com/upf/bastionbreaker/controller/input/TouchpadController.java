package com.upf.bastionbreaker.controller.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.upf.bastionbreaker.model.entities.Player;

public class TouchpadController {
    private Stage stage;
    private Touchpad touchpad;

    // Ajout de paramètres pour la position et la taille
    public TouchpadController(TextureAtlas gameAtlas, float x, float y, float width, float height) {
        if (gameAtlas.findRegion("touchBackground") == null || gameAtlas.findRegion("touchKnob") == null) {
            Gdx.app.error("DEBUG_GAME", "❌ ERREUR : Textures du touchpad introuvables !");
            return;
        }

        Gdx.app.log("DEBUG_GAME", "✅ Touchpad textures chargées avec succès !");

        // Création du Stage propre (note : on ne définit pas ici l'input processor)
        stage = new Stage(new ScreenViewport());

        // Création du Touchpad
        Touchpad.TouchpadStyle touchpadStyle = new Touchpad.TouchpadStyle();
        touchpadStyle.background = new TextureRegionDrawable(gameAtlas.findRegion("touchBackground"));
        touchpadStyle.knob = new TextureRegionDrawable(gameAtlas.findRegion("touchKnob"));

        touchpad = new Touchpad(10, touchpadStyle);
        touchpad.setBounds(x, y, width, height);
        touchpad.setTouchable(Touchable.enabled); // Permet de capter les événements tactiles

        stage.addActor(touchpad);
        Gdx.app.log("DEBUG_GAME", "✅ Touchpad ajouté au stage !");
    }

    public void handleInput(Player player, float delta) {
        if (player == null) return;

        float moveX = getKnobPercentX();
        if (Math.abs(moveX) > 0.1f) {
            float adjustedSpeed = moveX * 3.0f * delta; // Augmentation pour plus de réactivité
            player.move(adjustedSpeed);
            player.setMovingForward(moveX > 0);
            player.setMovingBackward(moveX < 0);
        } else {
            player.setMovingForward(false);
            player.setMovingBackward(false);
        }
    }

    public float getKnobPercentX() {
        if (touchpad == null) {
            Gdx.app.error("DEBUG_GAME", "❌ ERREUR : `touchpad` est NULL dans `getKnobPercentX()` !");
            return 0f;
        }
        float value = touchpad.getKnobPercentX();
        //Gdx.app.log("DEBUG_GAME", "🎮 Valeur touchpad X: " + value);
        return value;
    }

    public void update(float delta) {
        stage.act(delta);
    }

    public void draw() {
        stage.draw();
    }

    public Stage getStage() {
        return stage;
    }

    public void dispose() {
        stage.dispose();
    }
}
