package com.upf.bastionbreaker.model.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.upf.bastionbreaker.model.map.GameObject;
import com.upf.bastionbreaker.model.graphics.TextureManager;
import com.upf.bastionbreaker.view.screens.MapRenderer;

public class Drawbridge {
    private float x, y, width, height;
    private TextureRegion texture;
    private String linkedTopName;
    private float fallSpeed;
    private String state; // "closed" ou "open"
    private float pivotX, pivotY; // Coordonnées du pivot (données dans le TMX)
    private float angle; // Angle de rotation actuel, en degrés
    private boolean traversable; // Devient true quand le pont est complètement tombé

    // Le chain link supportant le pont (par exemple, ChainLink5 attaché au Bridgelock)
    private ChainLink supportingLink;

    public Drawbridge(GameObject gameObject) {
        // Conversion des coordonnées de l'objet (en pixels) en unités de tuiles
        this.x = gameObject.getX() / MapRenderer.TILE_SIZE;
        this.y = gameObject.getY() / MapRenderer.TILE_SIZE;
        this.width = gameObject.getWidth() / MapRenderer.TILE_SIZE;
        this.height = gameObject.getHeight() / MapRenderer.TILE_SIZE;
        this.linkedTopName = gameObject.getProperty("linked_top", String.class);


        // Récupération de fall_speed (par défaut 3.0)
        Object fallSpeedProp = gameObject.getProperties().get("fall_speed");
        if (fallSpeedProp instanceof Number) {
            this.fallSpeed = ((Number)fallSpeedProp).floatValue();
        } else if (fallSpeedProp instanceof String) {
            try {
                this.fallSpeed = Float.parseFloat((String)fallSpeedProp);
            } catch (NumberFormatException e) {
                this.fallSpeed = 3.0f;
            }
        } else {
            this.fallSpeed = 3.0f;
        }

        // Récupération de l'état initial, par défaut "closed"
        Object stateProp = gameObject.getProperties().get("state");
        if (stateProp instanceof String) {
            this.state = (String) stateProp;
        } else {
            this.state = "closed";
        }

        // Récupération des coordonnées du pivot, données par "x_position" et "y_position"
        Object pivotXProp = gameObject.getProperties().get("x_position");
        Object pivotYProp = gameObject.getProperties().get("y_position");
        if (pivotXProp instanceof Number) {
            this.pivotX = ((Number) pivotXProp).floatValue() / MapRenderer.TILE_SIZE;
        } else if (pivotXProp instanceof String) {
            try {
                this.pivotX = Float.parseFloat((String)pivotXProp) / MapRenderer.TILE_SIZE;
            } catch (NumberFormatException e) {
                this.pivotX = 0;
            }
        } else {
            this.pivotX = 0;
        }
        if (pivotYProp instanceof Number) {
            this.pivotY = ((Number) pivotYProp).floatValue() / MapRenderer.TILE_SIZE;
        } else if (pivotYProp instanceof String) {
            try {
                this.pivotY = Float.parseFloat((String)pivotYProp) / MapRenderer.TILE_SIZE;
            } catch (NumberFormatException e) {
                this.pivotY = 0;
            }
        } else {
            this.pivotY = 0;
        }

        // Récupération du sprite depuis le TMX (sans valeur par défaut)
        String spriteName = gameObject.getProperty("sprite", String.class);
        if (spriteName == null) {
            System.out.println("⚠️ ATTENTION : Drawbridge n'a pas de sprite défini dans le TMX !");
            this.texture = null;
        } else {
            TextureAtlas atlas = TextureManager.getGameAtlas();
            this.texture = atlas.findRegion(spriteName);
            if (this.texture == null) {
                System.out.println("❌ ERREUR : Texture '" + spriteName + "' introuvable pour Drawbridge !");
            }
        }

        // Initialisation de la rotation et de l'état
        this.angle = 0f; // fermé : aucune rotation
        this.traversable = false;
    }

    /**
     * Permet de définir le chain link supportant ce pont (ex : ChainLink5 attaché au Bridgelock).
     */
    public void setSupportingLink(ChainLink link) {
        this.supportingLink = link;
    }

    /**
     * Met à jour le Drawbridge.
     * Si le supportingLink est détruit (c'est-à-dire que le Bridgelock est éliminé), le pont passe en état "open"
     * et commence à tomber (rotation autour du pivot) avec la vitesse fallSpeed jusqu'à atteindre 90°.
     *
     * @param delta Temps écoulé depuis la dernière frame.
     */
    public void update(float delta) {
        if (state.equals("closed")) {
            // Tant que le pont est fermé, il reste suspendu.
            // On vérifie si le supportingLink est détruit.
            if (supportingLink == null || supportingLink.isDestroyed()) {
                state = "open";
                System.out.println("💥 Le pont-levis se libère et commence à tomber !");
            }
        }
        if (state.equals("open")) {
            // Faire tourner le pont autour du pivot.
            if (angle < 90f) {
                // Incrément de l'angle basé sur fallSpeed et delta.
                angle += fallSpeed * delta * 10; // Le multiplicateur ajuste la vitesse de rotation.
                if (angle > 90f) {
                    angle = 90f;
                }
            } else {
                // Une fois que le pont est complètement tombé (90°), il devient traversable.
                traversable = true;
            }
        }
    }

    /**
     * Affiche le Drawbridge.
     * S'il est fermé, il est dessiné normalement.
     * S'il est ouvert, il est dessiné avec rotation autour du pivot (x_position, y_position).
     *
     * @param batch SpriteBatch utilisé pour le rendu.
     */
    public void render(SpriteBatch batch) {
        if (texture == null) return;
        if (state.equals("closed")) {
            // Dessiner le pont sans rotation
            batch.draw(texture, x, y, width, height);
        } else {
            // Calcul de l'origine de rotation relative à l'image
            float originX = pivotX - x;
            float originY = pivotY - y;
            batch.draw(texture, x, y, originX, originY, width, height, 1, 1, angle);
        }
    }

    /**
     * Indique si le pont est ouvert et traversable.
     */
    public boolean isTraversable() {
        return traversable;
    }

    /**
     * Retourne le rectangle de collision du Drawbridge (pour la détection de passage du joueur).
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // Getters utiles pour la configuration
    public float getPivotX() {
        return pivotX;
    }

    public float getPivotY() {
        return pivotY;
    }

    public String getState() {
        return state;
    }

    public String getLinkedTopName() {
        return linkedTopName;
    }

}
