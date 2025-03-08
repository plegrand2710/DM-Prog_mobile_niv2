package com.upf.bastionbreaker.view.screens;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.upf.bastionbreaker.model.graphics.TextureManager;

import java.util.ArrayList;
import java.util.List;

public class ParallaxBackground {
    private static class Layer {
        TextureAtlas.AtlasRegion texture;
        float speed;
        Vector2 position;

        Layer(TextureAtlas.AtlasRegion texture, float speed) {
            this.texture = texture;
            this.speed = speed;
            this.position = new Vector2(0, 0);
        }
    }

    private final List<Layer> layers = new ArrayList<>();

    public ParallaxBackground() {
        TextureAtlas backgroundAtlas = TextureManager.getBackgroundAtlas();

        // Vérifier si l'atlas est bien chargé
        if (backgroundAtlas == null) {
            System.out.println("❌ ERREUR: `background.atlas` non chargé !");
            return;
        }

        // Charger les couches avec les bonnes vitesses (du plus lointain au plus proche)
        layers.add(new Layer(backgroundAtlas.findRegion("background", 1), 0.1f)); // Couche la plus lointaine
        layers.add(new Layer(backgroundAtlas.findRegion("background", 2), 0.2f));
        layers.add(new Layer(backgroundAtlas.findRegion("background", 3), 0.3f));
        layers.add(new Layer(backgroundAtlas.findRegion("background", 4), 0.4f));
        layers.add(new Layer(backgroundAtlas.findRegion("background", 5), 0.5f)); // Couche la plus proche

        // Vérifier que les textures existent
        for (Layer layer : layers) {
            if (layer.texture == null) {
                System.out.println("❌ ERREUR: Une texture de fond est introuvable !");
            } else {
                System.out.println("✅ Texture chargée : " + layer.texture.name);
            }
        }
    }

    public void update(float cameraX) {
        for (Layer layer : layers) {
            layer.position.x = -cameraX * layer.speed; // Appliquer l'effet parallaxe
        }
    }

    public void render(SpriteBatch batch) {
        for (Layer layer : layers) {
            if (layer.texture != null) { // Sécurité pour éviter un crash
                batch.draw(layer.texture, layer.position.x, layer.position.y);
            }
        }
    }
}
