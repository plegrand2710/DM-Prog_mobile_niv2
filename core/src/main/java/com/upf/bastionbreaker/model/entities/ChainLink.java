package com.upf.bastionbreaker.model.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.upf.bastionbreaker.model.map.GameObject;
import com.upf.bastionbreaker.model.graphics.TextureManager;
import com.upf.bastionbreaker.view.screens.MapRenderer;
import com.upf.bastionbreaker.model.physics.PhysicManager;

public class ChainLink {
    private float x, y, width, height;
    private TextureRegion texture;
    private String linkedTop;      // Nom/ID de l'objet ou maillon attaché au-dessus
    private String linkedBottom;   // Nom/ID de l'objet ou maillon attaché en dessous

    private boolean isDestroyed;   // Indique si le maillon est détruit
    private boolean isFalling;     // Indique si le maillon est en chute libre
    private float fallSpeed;       // Vitesse de chute (utilisée par PhysicManager)

    public ChainLink(GameObject gameObject) {
        // Conversion des coordonnées (pixels -> unités de tuiles)
        this.x = gameObject.getX() / MapRenderer.TILE_SIZE;
        this.y = gameObject.getY() / MapRenderer.TILE_SIZE;
        this.width = gameObject.getWidth() / MapRenderer.TILE_SIZE;
        this.height = gameObject.getHeight() / MapRenderer.TILE_SIZE;

        // Récupérer la texture via l'atlas
        String spriteName = gameObject.getProperty("sprite", String.class);
        if (spriteName == null) {
            spriteName = "chain"; // Valeur par défaut
        }
        TextureAtlas atlas = TextureManager.getGameAtlas();
        this.texture = atlas.findRegion(spriteName);
        if (this.texture == null) {
            System.out.println("❌ ERREUR : Texture '" + spriteName + "' introuvable pour ChainLink !");
        }

        // Récupérer les propriétés de liaison
        this.linkedTop = gameObject.getProperty("linked_top", String.class);
        this.linkedBottom = gameObject.getProperty("linked_bottom", String.class);

        this.isDestroyed = false;
        this.isFalling = false;
        this.fallSpeed = 0f;
    }

    /**
     * Met à jour le maillon.
     * Si le maillon est en chute libre et non détruit, la gravité est appliquée via PhysicManager.
     *
     * @param delta Temps écoulé depuis la dernière frame.
     */
    public void update(float delta) {
        if (isFalling && !isDestroyed) {
            // Délégation à PhysicManager pour appliquer la gravité
            PhysicManager.applyGravity(this, delta);
        }
    }

    /**
     * Affiche le maillon de chaîne s'il n'est pas détruit.
     *
     * @param batch SpriteBatch utilisé pour le rendu.
     */
    public void render(SpriteBatch batch) {
        if (!isDestroyed && texture != null) {
            batch.draw(texture, x, y, width, height);
        }
    }

    /**
     * Détruit le maillon (il ne sera plus affiché et ne subira plus d'interactions).
     */
    public void destroy() {
        isDestroyed = true;
        System.out.println("💥 La chaîne se brise et tombe !");
    }

    /**
     * Déclenche la chute libre du maillon.
     */
    public void fall() {
        if (!isFalling) {
            isFalling = true;
            fallSpeed = 0f;
        }
    }

    /**
     * Renvoie le rectangle de collision du maillon.
     *
     * @return Rectangle de collision.
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // Getters et Setters pour fallSpeed et y (utilisés par PhysicManager)

    public float getFallSpeed() {
        return fallSpeed;
    }

    public void setFallSpeed(float fallSpeed) {
        this.fallSpeed = fallSpeed;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    // Autres getters

    public boolean isDestroyed() {
        return isDestroyed;
    }

    public boolean isFalling() {
        return isFalling;
    }

    public String getLinkedTop() {
        return linkedTop;
    }

    public String getLinkedBottom() {
        return linkedBottom;
    }

    public float getX() {
        return x;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
