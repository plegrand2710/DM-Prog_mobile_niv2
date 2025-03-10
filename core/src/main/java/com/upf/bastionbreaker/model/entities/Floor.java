package com.upf.bastionbreaker.model.entities;

import com.badlogic.gdx.math.Rectangle;
import com.upf.bastionbreaker.model.map.GameObject;
import com.upf.bastionbreaker.view.screens.MapRenderer;

public class Floor {
    private float x, y, width, height;
    private boolean collision;

    public Floor(GameObject gameObject) {
        // Conversion des coordonnées de pixels en unités de tuiles
        this.x = gameObject.getX() / MapRenderer.TILE_SIZE;
        this.y = gameObject.getY() / MapRenderer.TILE_SIZE;
        this.width = gameObject.getWidth() / MapRenderer.TILE_SIZE;
        this.height = gameObject.getHeight() / MapRenderer.TILE_SIZE;

        // Récupérer la propriété "collision"
        Object collisionProp = gameObject.getProperties().get("collision");
        if (collisionProp instanceof Boolean) {
            collision = (Boolean) collisionProp;
        } else if (collisionProp instanceof String) {
            collision = Boolean.parseBoolean((String) collisionProp);
        } else {
            collision = false;
        }
    }

    /**
     * Renvoie le rectangle de collision de ce Floor.
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
