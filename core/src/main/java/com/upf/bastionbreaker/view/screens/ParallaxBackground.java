package com.upf.bastionbreaker.view.screens;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.upf.bastionbreaker.model.graphics.TextureManager;

public class ParallaxBackground {
    private TextureAtlas.AtlasRegion background;
    private Vector2 position;
    private float parallaxSpeed;

    public ParallaxBackground(String regionName, float speed) {
        background = TextureManager.getBackgroundAtlas().findRegion(regionName);
        position = new Vector2(0, 0);
        this.parallaxSpeed = speed;
    }

    public void update(float cameraX) {
        position.x = -cameraX * parallaxSpeed; // Effet de défilement du fond
    }

    public void render(SpriteBatch batch) {
        batch.draw(background, position.x, position.y);
    }
}
