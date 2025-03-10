package com.upf.bastionbreaker.model.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.upf.bastionbreaker.model.map.GameObject;
import com.upf.bastionbreaker.model.graphics.TextureManager;
import com.upf.bastionbreaker.view.screens.MapRenderer; // Import nécessaire pour accéder à TILE_SIZE

public class Checkpoint {
    private float x, y, width, height;
    private TextureRegion texture;

    public Checkpoint(GameObject gameObject) {
        // Conversion de pixels en unités de tuiles en divisant par MapRenderer.TILE_SIZE (32f)
        this.x = gameObject.getX() / MapRenderer.TILE_SIZE;
        this.y = gameObject.getY() / MapRenderer.TILE_SIZE;
        this.width = gameObject.getWidth() / MapRenderer.TILE_SIZE;
        this.height = gameObject.getHeight() / MapRenderer.TILE_SIZE;

        // Vérifier si l'objet a une propriété 'sprite'
        String spriteName = gameObject.getProperty("sprite", String.class);

        if (spriteName != null) {
            TextureAtlas atlas = TextureManager.getGameAtlas();
            this.texture = atlas.findRegion(spriteName); // 🔹 Récupère la bonne image

            if (this.texture == null) {
                System.out.println("❌ ERREUR : Texture '" + spriteName + "' introuvable !");
            }
        } else {
            System.out.println("⚠️ ATTENTION : L'objet '" + gameObject.getName() + "' n'a pas de sprite défini !");
        }
    }

    public void render(SpriteBatch batch) {
        if (texture != null) {
            batch.draw(texture, x, y, width, height); // 🟢 Adapte l'image à l'objet
        }
    }

    // Getters pour récupérer les coordonnées si besoin
    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
