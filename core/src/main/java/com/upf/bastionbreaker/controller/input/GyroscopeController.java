package com.upf.bastionbreaker.controller.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;

/**
 * GyroscopeController gère les entrées basées sur l'accéléromètre du dispositif.
 * La méthode getMovementX() retourne une valeur indiquant le déplacement horizontal
 * en fonction de l'inclinaison de l'appareil.
 */
public class GyroscopeController implements InputProcessor {

    // Sensibilité de l'entrée du gyroscope (ajustable selon les besoins)
    private float sensitivity = 0.02f;

    public GyroscopeController() {
        // Initialisation si nécessaire (par exemple, calibrage de l'accéléromètre)
    }

    /**
     * Retourne le déplacement horizontal basé sur l'accéléromètre.
     * @return Valeur de déplacement sur l'axe X, ajustée par la sensibilité.
     */
    public float getMovementX() {
        // Récupère la valeur de l'accéléromètre sur l'axe X
        float accelX = Gdx.input.getAccelerometerX();
        // Selon votre dispositif, vous pourriez avoir besoin d'inverser la valeur (ex : -accelX)
        return accelX * sensitivity;
    }

    /**
     * Méthode de mise à jour. Aucun traitement périodique n'est requis ici,
     * mais cette méthode est là pour l'éventuelle application de filtres ou calibrations.
     * @param delta Temps écoulé depuis la dernière mise à jour.
     */
    public void update(float delta) {
        // Aucun traitement n'est nécessaire pour l'instant.
    }

    /**
     * Retourne l'instance actuelle en tant qu'InputProcessor.
     * @return L'instance de GyroscopeController.
     */
    public InputProcessor getInputProcessor() {
        return this;
    }

    /**
     * Libère les ressources, si nécessaire.
     */
    public void dispose() {
        // Aucune ressource spécifique à libérer pour l'instant.
    }

    // Méthodes de l'interface InputProcessor (aucune action par défaut)
    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }
}
