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
        this.hp = getIntProperty(gameObject, "HP", 20);
        this.destructible = getBooleanProperty(gameObject, "destructible", false);
        this.respawnTime = getFloatProperty(gameObject, "respawn_time", DEFAULT_RESPAWN_DELAY);

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
        if (spriteName != null && !spriteName.isEmpty()) {
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
                setActive(true);
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
            setActive(false);
        }
    }

    /**
     * Active ou désactive la FlyingBox.
     */
    public void setActive(boolean active) {
        this.active = active;
        this.respawnTimer = 0f;
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

    /**
     * Méthodes utilitaires pour récupérer des propriétés de `GameObject`
     * et éviter les erreurs de conversion.
     */
    private int getIntProperty(GameObject gameObject, String property, int defaultValue) {
        Object prop = gameObject.getProperties().get(property);
        if (prop instanceof Number) return ((Number) prop).intValue();
        if (prop instanceof String) try { return Integer.parseInt((String) prop); } catch (NumberFormatException e) {}
        return defaultValue;
    }

    private float getFloatProperty(GameObject gameObject, String property, float defaultValue) {
        Object prop = gameObject.getProperties().get(property);
        if (prop instanceof Number) return ((Number) prop).floatValue();
        if (prop instanceof String) try { return Float.parseFloat((String) prop); } catch (NumberFormatException e) {}
        return defaultValue;
    }

    private boolean getBooleanProperty(GameObject gameObject, String property, boolean defaultValue) {
        Object prop = gameObject.getProperties().get(property);
        if (prop instanceof Boolean) return (Boolean) prop;
        if (prop instanceof String) return Boolean.parseBoolean((String) prop);
        return defaultValue;
    }
}
