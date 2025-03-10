package com.upf.bastionbreaker.model.physics;

import com.upf.bastionbreaker.model.entities.ChainLink;

public class PhysicManager {
    // Gravité appliquée aux objets (valeur à ajuster en fonction du gameplay)
    private static final float GRAVITY = 9.8f;

    /**
     * Applique la gravité à un ChainLink qui est en chute libre.
     * Met à jour la vitesse de chute et la position Y.
     *
     * @param link  Le maillon de chaîne concerné.
     * @param delta Le temps écoulé depuis la dernière frame.
     */
    public static void applyGravity(ChainLink link, float delta) {
        if (link.isFalling() && !link.isDestroyed()) {
            // Mise à jour de la vitesse de chute
            float newFallSpeed = link.getFallSpeed() + GRAVITY * delta;
            link.setFallSpeed(newFallSpeed);

            // Mise à jour de la position Y (le maillon descend)
            float newY = link.getY() - newFallSpeed * delta;
            link.setY(newY);
        }
    }
}
