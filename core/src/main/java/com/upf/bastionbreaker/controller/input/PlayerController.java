package com.upf.bastionbreaker.controller.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.upf.bastionbreaker.model.entities.Player;
import com.upf.bastionbreaker.controller.input.TouchpadController;
import com.upf.bastionbreaker.controller.input.GyroscopeController;
import com.upf.bastionbreaker.view.ui.ControlsOverlay;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class PlayerController {
    private Player player;
    private TouchpadController touchpadController;
    private GyroscopeController gyroscopeController;
    private ControlsOverlay controlsOverlay;

    private boolean jumpPressed = false;
    private boolean modePressed = false;

    public PlayerController(Player player, TouchpadController touchpadController, GyroscopeController gyroscopeController, ControlsOverlay controlsOverlay) {
        this.player = player;
        this.touchpadController = touchpadController;
        this.gyroscopeController = gyroscopeController;
        this.controlsOverlay = controlsOverlay;
    }

    public void update(float delta) {
        handleInput();
        player.update(delta);
        controlsOverlay.update(delta);
    }

    private void handleInput() {
        float moveX = touchpadController.getKnobPercentX(); // Touchpad (entre -1 et 1)
        float gyroX = gyroscopeController.getMovementX(); // Gyroscope

        // Déplacement du joueur (choisir entre gyroscope ou touchpad)
        if (Math.abs(gyroX) > 0.1f) { // Gyroscope actif
            if (gyroX > 0.1f) {
                player.setMovingForward(true);
                player.setMovingBackward(false);
            } else {
                player.setMovingForward(false);
                player.setMovingBackward(true);
            }
        } else { // Sinon utiliser le touchpad
            if (moveX > 0.1f) {
                player.setMovingForward(true);
                player.setMovingBackward(false);
            } else if (moveX < -0.1f) {
                player.setMovingForward(false);
                player.setMovingBackward(true);
            } else {
                player.setMovingForward(false);
                player.setMovingBackward(false);
            }
        }

        // Gestion du saut (uniquement si le bouton Jump est pressé)
        TextButton jumpButton = controlsOverlay.getJumpButton();
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) || jumpButton.isPressed()) {
            if (!jumpPressed) { // Éviter le spam de saut
                player.jump();
                jumpPressed = true;
            }
        } else {
            jumpPressed = false;
        }

        // Gestion de la transformation Tank ↔ Robot
        TextButton modeButton = controlsOverlay.getModeButton();
        if (Gdx.input.isKeyPressed(Input.Keys.T) || modeButton.isPressed()) {
            if (!modePressed) { // Éviter les transformations en boucle
                player.switchMode();
                modePressed = true;
            }
        } else {
            modePressed = false;
        }
    }
}
