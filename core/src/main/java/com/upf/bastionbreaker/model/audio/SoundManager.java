package com.upf.bastionbreaker.model.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {

    private Sound tankEngineSound;
    private Sound tankShotSound;
    private long tankEngineId = -1; // Identifiant du son en boucle

    public SoundManager() {
        // Chargement des sons depuis le dossier assets/sounds
        tankEngineSound = Gdx.audio.newSound(Gdx.files.internal("sounds/tank_engine.ogg"));
        tankShotSound = Gdx.audio.newSound(Gdx.files.internal("sounds/tank_shot.ogg"));
    }

    // Démarre la boucle du son moteur du tank s'il n'est pas déjà joué
    public void playTankEngine() {
        if (tankEngineId == -1) {
            tankEngineId = tankEngineSound.loop();
        }
    }

    // Arrête le son moteur du tank s'il est en cours de lecture
    public void stopTankEngine() {
        if (tankEngineId != -1) {
            tankEngineSound.stop(tankEngineId);
            tankEngineId = -1;
        }
    }

    // Joue le son d'un tir de tank
    public void playTankShot() {
        tankShotSound.play();
    }

    // Ajuste le volume du son moteur (pour une lecture en boucle)
    public void setVolume(float volume) {
        if (tankEngineId != -1) {
            tankEngineSound.setVolume(tankEngineId, volume);
        }
    }

    public void dispose() {
        tankEngineSound.dispose();
        tankShotSound.dispose();
    }
}
