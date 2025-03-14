package com.upf.bastionbreaker.controller.gameplay;

import com.upf.bastionbreaker.model.entities.Player;
import com.upf.bastionbreaker.model.entities.Tank;
import com.upf.bastionbreaker.model.audio.SoundManager;

public class TransformationManager {
    private static TransformationManager instance;

    private TransformationManager() {
        // Constructeur privé pour le singleton
    }

    public static TransformationManager getInstance() {
        if (instance == null) {
            instance = new TransformationManager();
        }
        return instance;
    }

    /**
     * Transforme le joueur entre Tank et Robot.
     */
    public void transform(Player player) {
        if (player.getCurrentMode() instanceof Tank) {
            player.switchMode();
            SoundManager.stopSound("tank_engine");
        } else {
            player.switchMode();
        }
    }
}
