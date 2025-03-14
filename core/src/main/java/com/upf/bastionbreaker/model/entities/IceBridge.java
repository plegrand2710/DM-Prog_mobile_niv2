package com.upf.bastionbreaker.model.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.upf.bastionbreaker.model.map.GameObject;
import com.upf.bastionbreaker.model.graphics.TextureManager;
import com.upf.bastionbreaker.view.screens.MapRenderer;

public class IceBridge {
    private float x, y, width, height;
    private TextureRegion texture;
    private boolean fragile;
    private int weightLimit;

    public IceBridge(GameObject gameObject) {
        // Conversion des coordonnées (pixels -> unités de tuiles)
        this.x = gameObject.getX() / MapRenderer.TILE_SIZE;
        this.y = gameObject.getY() / MapRenderer.TILE_SIZE;
        this.width = gameObject.getWidth() / MapRenderer.TILE_SIZE;
        this.height = gameObject.getHeight() / MapRenderer.TILE_SIZE;

        // Récupérer la propriété "fragile" (par défaut true)
        Object fragileProp = gameObject.getProperties().get("fragile");
        if (fragileProp instanceof Boolean)
            this.fragile = (Boolean) fragileProp;
        else if (fragileProp instanceof String)
            this.fragile = Boolean.parseBoolean((String) fragileProp);
        else
            this.fragile = true;

        // Récupérer la limite de poids (weight_limit), par défaut 100
        Object weightProp = gameObject.getProperties().get("weight_limit");
        if (weightProp instanceof Number)
            this.weightLimit = ((Number) weightProp).intValue();
        else if (weightProp instanceof String)
            this.weightLimit = Integer.parseInt((String) weightProp);
        else
            this.weightLimit = 100;

        // Charger la texture depuis l'atlas via la propriété "sprite"
        String spriteName = gameObject.getProperty("sprite", String.class);
        if (spriteName != null) {
            TextureAtlas atlas = TextureManager.getGameAtlas();
            this.texture = atlas.findRegion(spriteName);
            if (this.texture == null) {
                System.out.println("❌ ERREUR : Texture '" + spriteName + "' introuvable pour IceBridge !");
            }
        } else {
            System.out.println("⚠️ ATTENTION : IceBridge n'a pas de sprite défini !");
        }
    }

    /**
     * Affiche le pont de glace.
     */
    public void render(SpriteBatch batch) {
        if (texture != null) {
            batch.draw(texture, x, y, width, height);
        }
    }

    /**
     * Renvoie le rectangle de collision de l'Ice Bridge.
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getWeightLimit() {
        return weightLimit;
    }

    public boolean isFragile() {
        return fragile;
    }
}
