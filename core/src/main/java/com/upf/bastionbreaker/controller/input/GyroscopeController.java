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
        // Initialisation si nécessaire (par exemple, calibrage)
    }

    /**
     * Retourne le déplacement horizontal basé sur l'accéléromètre.
     * @return Valeur de déplacement sur l'axe X.
     */
    public float getMovementX() {
        // Récupère la valeur de l'accéléromètre sur l'axe X
        float accelX = Gdx.input.getAccelerometerX();
        // Applique la sensibilité et retourne la valeur ajustée
        return accelX * sensitivity;
    }

    /**
     * Méthode de mise à jour si des traitements périodiques sont nécessaires.
     * @param delta Temps écoulé depuis la dernière mise à jour.
     */
    public void update(float delta) {
        // Aucun traitement requis ici dans cette implémentation.
    }

    /**
     * Permet d'obtenir l'InputProcessor (ici, l'instance courante).
     * @return L'instance de GyroscopeController en tant qu'InputProcessor.
     */
    public InputProcessor getInputProcessor() {
        return this;
    }

    /**
     * Libère les ressources allouées par le contrôleur (aucune dans ce cas).
     */
    public void dispose() {
        // Aucune ressource à libérer.
    }

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

    /**
     * Implémentation de la méthode touchCancelled pour satisfaire l'interface InputProcessor.
     */
    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }
}
