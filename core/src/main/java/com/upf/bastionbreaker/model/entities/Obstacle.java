package com.upf.bastionbreaker.model.entities;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.upf.bastionbreaker.model.graphics.TextureManager;
import java.util.Map;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Obstacle {
    private Sprite sprite;

    public Obstacle(String name, float x, float y, float width, float height, Map<String, Object> properties) {
        // Récupérer le nom du sprite
        String spriteName = (String) properties.get("sprite");

        if (spriteName != null) {
            TextureRegion textureRegion = TextureManager.getTextureRegion(spriteName);
            if (textureRegion != null) {
                this.sprite = new Sprite(textureRegion);
                this.sprite.setBounds(x, y, width, height);
            } else {
                System.out.println("⚠️ Sprite '" + spriteName + "' introuvable !");
            }
        } else {
            System.out.println("⚠️ Pas de sprite défini pour l'obstacle : " + name);
        }
    }

    public void render(SpriteBatch batch) {
        if (sprite != null) {
            sprite.draw(batch);
        }
    }
}
