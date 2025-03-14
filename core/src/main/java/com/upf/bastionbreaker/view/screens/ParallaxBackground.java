package com.upf.bastionbreaker.view.screens;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ParallaxBackground {
    private TextureRegion[] _layers;
    private float[] _speeds;
    private float[] _offsets;

    public ParallaxBackground(TextureRegion[] layers, float[] speeds) {
        _layers = layers;
        _speeds = speeds;
        _offsets = new float[layers.length];

        for (TextureRegion layer : _layers) {
            if (layer != null) {
                layer.getTexture().setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.ClampToEdge);
            }
        }
    }

    public void update(float delta, OrthographicCamera camera) {
        for (int i = 0; i < _layers.length; i++) {
            if (_layers[i] == null) continue;

            _offsets[i] -= _speeds[i] * delta; // Ajuste la vitesse de défilement
            _offsets[i] %= _layers[i].getRegionWidth(); // Assure une boucle continue
        }
    }

    public void render(SpriteBatch batch, OrthographicCamera camera) {
        batch.setProjectionMatrix(camera.combined);

        for (int i = 0; i < _layers.length; i++) {
            if (_layers[i] == null) continue;

            float textureWidth = _layers[i].getRegionWidth();
            float textureHeight = _layers[i].getRegionHeight();

            float viewportWidth = camera.viewportWidth;
            float viewportHeight = camera.viewportHeight;

            float scaleX = viewportWidth / textureWidth;
            float scaleY = viewportHeight / textureHeight;
            float scale = Math.max(scaleX, scaleY);

            float x = (camera.position.x - viewportWidth / 2) + _offsets[i];
            float y = camera.position.y - viewportHeight / 2;

            batch.draw(_layers[i], x, y, textureWidth * scale, viewportHeight);
            batch.draw(_layers[i], x + textureWidth * scale, y, textureWidth * scale, viewportHeight);
        }
    }
}
