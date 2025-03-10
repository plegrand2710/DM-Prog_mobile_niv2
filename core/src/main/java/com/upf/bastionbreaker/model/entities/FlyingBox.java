package com.upf.bastionbreaker.model.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.upf.bastionbreaker.model.map.GameObject;
import com.upf.bastionbreaker.model.graphics.TextureManager;
import com.upf.bastionbreaker.view.screens.MapRenderer;

public class FlyingBox {
    private float x, y, width, height;
    private TextureRegion texture;
    private int hp;
    private boolean destructible;
    private float respawnTime;
    private String effectType; // "heal" ou "shield"

    private boolean active;       // True si la FlyingBox est disponible
    private float respawnTimer;   // Temps écoulé depuis sa collecte

    public static final float DEFAULT_RESPAWN_DELAY = 60f; // délai par défaut en secondes

    public FlyingBox(GameObject gameObject) {
        // Conversion des coordonnées de pixels en unités de tuiles
        this.x = gameObject.getX() / MapRenderer.TILE_SIZE;
        this.y = gameObject.getY() / MapRenderer.TILE_SIZE;
        this.width = gameObject.getWidth() / MapRenderer.TILE_SIZE;
        this.height = gameObject.getHeight() / MapRenderer.TILE_SIZE;

        // Récupération de HP et propriété destructible
        Object hpProp = gameObject.getProperties().get("HP");
        this.hp = (hpProp instanceof Number) ? ((Number)hpProp).intValue() : 20;

        Object destructibleProp = gameObject.getProperties().get("destructible");
        if(destructibleProp instanceof Boolean)
            this.destructible = (Boolean) destructibleProp;
        else if(destructibleProp instanceof String)
            this.destructible = Boolean.parseBoolean((String)destructibleProp);

        // Temps de respawn
        Object respawnProp = gameObject.getProperties().get("respawn_time");
        if(respawnProp instanceof Number)
            this.respawnTime = ((Number)respawnProp).floatValue();
        else if(respawnProp instanceof String)
            this.respawnTime = Float.parseFloat((String)respawnProp);
        else
            this.respawnTime = DEFAULT_RESPAWN_DELAY;

        // Déterminer le type d'effet à partir du nom de l'objet
        String name = gameObject.getName() != null ? gameObject.getName() : "";
        if (name.toLowerCase().contains("heal"))
            effectType = "heal";
        else if (name.toLowerCase().contains("shield"))
            effectType = "shield";
        else {
            effectType = "heal"; // par défaut
            System.out.println("⚠️ ATTENTION : Type de FlyingBox non spécifié, 'heal' par défaut.");
        }

        // Récupérer la texture depuis l'atlas
        String spriteName = gameObject.getProperty("sprite", String.class);
        if (spriteName != null) {
            TextureAtlas atlas = TextureManager.getGameAtlas();
            this.texture = atlas.findRegion(spriteName);
            if (this.texture == null) {
                System.out.println("❌ ERREUR : Texture '" + spriteName + "' introuvable pour FlyingBox !");
            }
        } else {
            System.out.println("⚠️ ATTENTION : La FlyingBox n'a pas de sprite défini !");
        }

        active = true;
        respawnTimer = 0f;
    }

    /**
     * Met à jour la FlyingBox : si elle est inactive, incrémente le timer
     * et la réactive une fois le délai écoulé.
     */
    public void update(float delta) {
        if (!active) {
            respawnTimer += delta;
            if (respawnTimer >= respawnTime) {
                active = true;
                respawnTimer = 0f;
            }
        }
    }

    /**
     * Affiche la FlyingBox uniquement si elle est active.
     */
    public void render(SpriteBatch batch) {
        if (active && texture != null) {
            batch.draw(texture, x, y, width, height);
        }
    }

    /**
     * Lors de la collecte, applique l'effet sur le joueur et désactive la FlyingBox.
     */
    public void onCollected(Player player) {
        if (active) {
            player.collectFlyingBox(this);
            active = false;
            respawnTimer = 0f;
        }
    }

    public boolean isActive() {
        return active;
    }

    /**
     * Retourne les limites de la FlyingBox pour la détection de collision.
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public String getEffectType() {
        return effectType;
    }
}
