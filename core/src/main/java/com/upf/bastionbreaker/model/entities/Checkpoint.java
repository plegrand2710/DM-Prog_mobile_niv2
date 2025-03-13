package com.upf.bastionbreaker.model.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.upf.bastionbreaker.model.map.GameObject;
import com.upf.bastionbreaker.model.graphics.TextureManager;
import com.upf.bastionbreaker.view.screens.MapRenderer;

public class Checkpoint {
    private float x, y, width, height;
    private TextureRegion texture;
    private String name; // Nom du checkpoint

    public Checkpoint(GameObject gameObject) {
        // Récupération du nom depuis le GameObject
        this.name = gameObject.getName();
        // Conversion de pixels en unités de tuiles
        this.x = gameObject.getX() / MapRenderer.TILE_SIZE;
        this.y = gameObject.getY() / MapRenderer.TILE_SIZE;
        this.width = gameObject.getWidth() / MapRenderer.TILE_SIZE;
        this.height = gameObject.getHeight() / MapRenderer.TILE_SIZE;

        // Récupération du sprite défini dans le TMX
        String spriteName = gameObject.getProperty("sprite", String.class);
        if (spriteName != null) {
            TextureAtlas atlas = TextureManager.getGameAtlas();
            this.texture = atlas.findRegion(spriteName);
            if (this.texture == null) {
                System.out.println("❌ ERREUR : Texture '" + spriteName + "' introuvable !");
            }
        } else {
            System.out.println("⚠️ ATTENTION : L'objet '" + gameObject.getName() + "' n'a pas de sprite défini !");
        }
    }

    public void render(SpriteBatch batch) {
        if (texture != null) {
            batch.draw(texture, x, y, width, height);
        }
    }

    // Getter pour le rectangle de collision
    public Rectangle getBoundingBox() {
        return new Rectangle(x, y, width, height);
    }

    // Getter pour le nom
    public String getName() {
        return name;
    }

    // Optionnel : getters pour x et y
    public float getX() { return x; }
    public float getY() { return y; }
}
