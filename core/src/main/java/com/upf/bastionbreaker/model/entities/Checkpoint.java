package com.upf.bastionbreaker.model.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.upf.bastionbreaker.model.map.GameObject;
import com.upf.bastionbreaker.model.graphics.TextureManager;
import com.upf.bastionbreaker.view.screens.MapRenderer;
import com.badlogic.gdx.math.Rectangle;

public class Checkpoint {
    private float x, y, width, height;
    private TextureRegion texture;

    public Checkpoint(GameObject gameObject) {
        // Conversion de pixels en unités de tuiles (division par TILE_SIZE)
        this.x = gameObject.getX() / MapRenderer.TILE_SIZE;
        this.y = gameObject.getY() / MapRenderer.TILE_SIZE;
        this.width = gameObject.getWidth() / MapRenderer.TILE_SIZE;
        this.height = gameObject.getHeight() / MapRenderer.TILE_SIZE;

        // Vérifier la propriété 'sprite'
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

    // Ajout d'un getter pour le rectangle de collision
    public Rectangle getBoundingBox() {
        return new Rectangle(x, y, width, height);
    }

    // Optionnel : getters pour x et y
    public float getX() { return x; }
    public float getY() { return y; }
}
