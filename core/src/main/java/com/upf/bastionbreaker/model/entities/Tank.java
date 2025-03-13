package com.upf.bastionbreaker.model.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.upf.bastionbreaker.model.graphics.TextureManager;

public class Tank extends PlayerMode {
    private Sound engineSound; // 🔹 Déclaration de l'attribut engineSound

    public Tank() {
        // 🔹 Correction : Passer "tank" si `PlayerMode` attend un String au lieu d'un TextureRegion
        super("tank", 2.0f, 120, 2.0f, 1.5f, false);

        // Chargement du son du moteur du Tank
        try {
            engineSound = Gdx.audio.newSound(Gdx.files.internal("sounds/tank_engine.ogg"));
        } catch (Exception e) {
            Gdx.app.error("DEBUG_GAME", "❌ ERREUR : Impossible de charger le son tank_engine.ogg", e);
            engineSound = null; // Évite le crash en mettant à null
        }
    }

    public void playEngineSound() {
        if (engineSound != null) {
            engineSound.loop(); // 🔄 Joue le son en boucle tant que le tank avance
        }
    }

    public void stopEngineSound() {
        if (engineSound != null) {
            engineSound.stop(); // ⏹️ Arrête le son lorsque le tank s'arrête
        }
    }

    // Nettoyage de la mémoire en fermant le son lors de la destruction
    public void dispose() {
        if (engineSound != null) {
            engineSound.dispose();
        }
    }
}
