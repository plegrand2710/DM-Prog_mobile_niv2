package com.upf.bastionbreaker.model.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.upf.bastionbreaker.model.map.GameObject;
import com.upf.bastionbreaker.model.graphics.TextureManager;
import com.upf.bastionbreaker.view.screens.MapRenderer;

public class TNT {
    private float x, y, width, height;
    private TextureRegion texture;
    private int hp;
    private float blastRadius;
    private int damage;
    private boolean pushable;
    private float respawnTime;

    private boolean active;      // Indique si la TNT est actuellement présente
    private float respawnTimer;  // Temps écoulé depuis l'explosion

    public TNT(GameObject gameObject) {
        // Conversion des coordonnées (pixels -> unités de tuiles)
        this.x = gameObject.getX() / MapRenderer.TILE_SIZE;
        this.y = gameObject.getY() / MapRenderer.TILE_SIZE;
        this.width = gameObject.getWidth() / MapRenderer.TILE_SIZE;
        this.height = gameObject.getHeight() / MapRenderer.TILE_SIZE;

        // Initialisation des propriétés
        Object hpProp = gameObject.getProperties().get("HP");
        if (hpProp instanceof Number) {
            this.hp = ((Number) hpProp).intValue();
        } else {
            this.hp = 1;
        }

        Object blastRadiusProp = gameObject.getProperties().get("blast_radius");
        if (blastRadiusProp instanceof Number) {
            this.blastRadius = ((Number) blastRadiusProp).floatValue();
        } else {
            this.blastRadius = 5.0f;
        }

        Object damageProp = gameObject.getProperties().get("damage");
        if (damageProp instanceof Number) {
            this.damage = ((Number) damageProp).intValue();
        } else {
            this.damage = 1000;
        }

        Object pushableProp = gameObject.getProperties().get("pushable");
        if (pushableProp instanceof Boolean) {
            this.pushable = (Boolean) pushableProp;
        } else if (pushableProp instanceof String) {
            this.pushable = Boolean.parseBoolean((String) pushableProp);
        } else {
            this.pushable = true;
        }

        Object respawnProp = gameObject.getProperties().get("respawn_time");
        if (respawnProp instanceof Number) {
            this.respawnTime = ((Number) respawnProp).floatValue();
        } else if (respawnProp instanceof String) {
            try {
                this.respawnTime = Float.parseFloat((String) respawnProp);
            } catch (NumberFormatException e) {
                this.respawnTime = 30.0f;
            }
        } else {
            this.respawnTime = 30.0f;
        }

        // Chargement de la texture depuis l'atlas (sprite par défaut "tnt")
        String spriteName = gameObject.getProperty("sprite", String.class);
        if (spriteName == null) {
            spriteName = "tnt";
        }
        TextureAtlas atlas = TextureManager.getGameAtlas();
        this.texture = atlas.findRegion(spriteName);
        if (this.texture == null) {
            System.out.println("❌ ERREUR : Texture '" + spriteName + "' introuvable pour TNT !");
        }

        // La TNT est active au départ
        this.active = true;
        this.respawnTimer = 0f;
    }

    /**
     * Met à jour la TNT.
     * Si elle n'est pas active, incrémente le timer de respawn et réactive la TNT si le délai est atteint.
     */
    public void update(float delta) {
        if (!active) {
            respawnTimer += delta;
            if (respawnTimer >= respawnTime) {
                active = true;
                respawnTimer = 0f;
                System.out.println("🔄 TNT réapparue");
            }
        }
    }

    /**
     * Affiche la TNT uniquement si elle est active.
     */
    public void render(SpriteBatch batch) {
        if (active && texture != null) {
            batch.draw(texture, x, y, width, height);
        }
    }

    /**
     * Permet de pousser la TNT si elle est active et pushable.
     * Seul le mode Robot pourra utiliser cette méthode pour déplacer la TNT.
     *
     * @param dx Déplacement horizontal.
     * @param dy Déplacement vertical.
     */
    public void push(float dx, float dy) {
        if (active && pushable) {
            x += dx;
            y += dy;
        }
    }

    /**
     * Déclenche l'explosion de la TNT.
     * Cette méthode la met en état inactif et déclenche les effets (dégâts, animation, etc.).
     */
    public void explode() {
        if (active) {
            active = false;
            System.out.println("💥 TNT explosée ! Dégâts infligés dans un rayon de " + blastRadius + " unités.");
            // Ici, vous pouvez appeler un ExplosionManager pour :
            // - Appliquer 1000 dégâts aux objets dans blastRadius
            // - Jouer une animation d'explosion
        }
    }

    /**
     * Retourne le rectangle de collision de la TNT.
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // Getters
    public boolean isActive() {
        return active;
    }

    public float getBlastRadius() {
        return blastRadius;
    }

    public int getDamage() {
        return damage;
    }

    public boolean isPushable() {
        return pushable;
    }
}
