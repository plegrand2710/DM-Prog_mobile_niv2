package com.upf.bastionbreaker.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ParallaxBackground {
    private TextureRegion[] layers;
    private float[] speeds;
    private float cameraStartX;

    public ParallaxBackground(TextureRegion[] layers, float[] speeds) {
        this.layers = layers;
        this.speeds = speeds;
        this.cameraStartX = 0;
    }

    public void render(SpriteBatch batch, OrthographicCamera camera) {
        batch.setProjectionMatrix(camera.combined);


        for (int i = 0; i < layers.length; i++) {
            if (layers[i] == null) continue; // 🔹 Vérifie que la texture existe

            float x = (camera.position.x * speeds[i]) - (camera.viewportWidth / 2);
            float y = camera.position.y - (camera.viewportHeight / 2);

            // 🔹 Dessiner la couche avec une taille ajustée à l'écran
            batch.draw(layers[i], x, y, camera.viewportWidth, camera.viewportHeight);
        }

    }

}
