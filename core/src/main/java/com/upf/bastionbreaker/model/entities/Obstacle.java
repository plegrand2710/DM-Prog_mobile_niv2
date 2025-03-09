package com.upf.bastionbreaker.model.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.upf.bastionbreaker.model.map.GameObject;
import com.upf.bastionbreaker.model.graphics.TextureManager;
import com.upf.bastionbreaker.view.screens.MapRenderer;

public class Obstacle {
    private float x, y, width, height;
    private TextureRegion texture;
    private int hp;
    private boolean destructible;
    private boolean movable;

    public Obstacle(GameObject gameObject) {
        // Conversion des coordonnées de pixels en unités de tuiles
        this.x = gameObject.getX() / MapRenderer.TILE_SIZE;
        this.y = gameObject.getY() / MapRenderer.TILE_SIZE;
        this.width = gameObject.getWidth() / MapRenderer.TILE_SIZE;
        this.height = gameObject.getHeight() / MapRenderer.TILE_SIZE;

        // Récupérer les propriétés spécifiques (HP, destructible, movable)
        Object hpProp = gameObject.getProperties().get("HP");
        if (hpProp instanceof Number) {
            this.hp = ((Number)hpProp).intValue();
        } else {
            this.hp = 20; // Valeur par défaut
        }
        Object destructibleProp = gameObject.getProperties().get("destructible");
        if (destructibleProp instanceof Boolean) {
            this.destructible = (Boolean) destructibleProp;
        } else if (destructibleProp instanceof String) {
            this.destructible = Boolean.parseBoolean((String)destructibleProp);
        }
        Object movableProp = gameObject.getProperties().get("movable");
        if (movableProp instanceof Boolean) {
            this.movable = (Boolean) movableProp;
        } else if (movableProp instanceof String) {
            this.movable = Boolean.parseBoolean((String)movableProp);
        }

        // Récupérer le nom du sprite et charger la texture correspondante depuis l'atlas
        String spriteName = gameObject.getProperty("sprite", String.class);
        if (spriteName != null) {
            TextureAtlas atlas = TextureManager.getGameAtlas();
            this.texture = atlas.findRegion(spriteName);
            if (this.texture == null) {
                System.out.println("❌ ERREUR : Texture '" + spriteName + "' introuvable pour l'obstacle !");
            }
        } else {
            System.out.println("⚠️ ATTENTION : L'obstacle '" + gameObject.getName() + "' n'a pas de sprite défini !");
        }
    }

    public void render(SpriteBatch batch) {
        if (texture != null) {
            batch.draw(texture, x, y, width, height);
        }
    }

    // Getters optionnels
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public int getHp() { return hp; }
    public boolean isDestructible() { return destructible; }
    public boolean isMovable() { return movable; }
}
