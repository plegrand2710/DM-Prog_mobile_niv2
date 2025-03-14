package com.upf.bastionbreaker.controller.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.MathUtils;
import com.upf.bastionbreaker.model.entities.Player;

/**
 * GyroscopeController gère les entrées basées sur l'accéléromètre du dispositif.
 * Il applique un déplacement horizontal au joueur en fonction de l'inclinaison de l'appareil.
 *
 * Remarque : Ce contrôleur ne doit pas être utilisé en mode "touchpad".
 */
public class GyroscopeController implements InputProcessor {

    // Seuil minimal pour détecter un mouvement
    private static final float THRESHOLD = 1.0f;
    // Vitesse maximale en unités par seconde
    private static final float MAX_SPEED = 4.0f;
    // Facteur d'échelle pour calculer la vitesse à partir du tilt
    private static final float SCALING_FACTOR = 0.5f;

    /**
     * Constructeur qui vérifie la disponibilité de l'accéléromètre.
     */
    public GyroscopeController() {
        if (!hasAccelerometer()) {
            Gdx.app.log("DEBUG_GAME", "⚠️ Accelerometer non disponible !");
        } else {
            Gdx.app.log("DEBUG_GAME", "✅ Accelerometer détecté !");
        }
    }

    /**
     * Vérifie la disponibilité de l'accéléromètre.
     * Pour simplifier, on renvoie true, mais vous pouvez ajuster selon vos besoins.
     *
     * @return true si l'accéléromètre est disponible
     */
    private boolean hasAccelerometer() {
        // Vous pouvez adapter cette méthode selon la plateforme (mobile vs desktop)
        return true;
    }

    /**
     * Applique le mouvement horizontal au joueur en fonction de l'inclinaison de l'accéléromètre.
     * Ce contrôleur doit être désactivé en mode "touchpad".
     *
     * @param player le joueur à déplacer
     * @param delta  temps écoulé depuis la dernière mise à jour
     */
    public void handleInput(Player player, float delta) {
        if (player == null) return;
        if (!hasAccelerometer()) return;

        float tilt = Gdx.input.getAccelerometerY();

        if (Math.abs(tilt) > 0.05f) {  // 🔥 Seuil plus bas pour détecter des mouvements légers
            float movementSpeed = MathUtils.clamp(tilt * 5f, -8.0f, 8.0f);  // 🔥 Facteur de sensibilité augmenté
            player.move(movementSpeed * delta);

            player.setMovingForward(movementSpeed > 0);
            player.setMovingBackward(movementSpeed < 0);

            Gdx.app.log("DEBUG_GAME", "🚀 Mouvement appliqué: " + movementSpeed);
        } else {
            player.setMovingForward(false);
            player.setMovingBackward(false);
        }
    }


    public float getMovementX() {
        float accelX = Gdx.input.getAccelerometerY();  // Utilise l'axe Y de l'accéléromètre
        float movement = MathUtils.clamp(accelX * 0.5f, -4.0f, 4.0f); // Applique un facteur de sensibilité et limite la vitesse

        Gdx.app.log("DEBUG_GAME", "🎮 Accéléromètre Y: " + accelX + " → Calculé: " + movement);
        return movement;
    }


    /**
     * Méthode de mise à jour (aucun traitement périodique requis ici).
     *
     * @param delta temps écoulé depuis la dernière mise à jour
     */
    public void update(float delta) {
        // Aucun traitement périodique nécessaire pour l'instant.
    }

    /**
     * Retourne cette instance en tant qu'InputProcessor, utile pour l'enregistrement dans un InputMultiplexer.
     *
     * @return l'instance de GyroscopeController
     */
    public InputProcessor getInputProcessor() {
        return this;
    }

    /**
     * Libère les ressources, si nécessaire.
     */
    public void dispose() {
        // Aucun nettoyage requis actuellement.
    }

    // Implémentation des méthodes de l'interface InputProcessor (non utilisées ici)

    @Override
    public boolean keyDown(int keycode) { return false; }

    @Override
    public boolean keyUp(int keycode) { return false; }

    @Override
    public boolean keyTyped(char character) { return false; }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }

    @Override
    public boolean mouseMoved(int screenX, int screenY) { return false; }

    @Override
    public boolean scrolled(float amountX, float amountY) { return false; }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) { return false; }
}
