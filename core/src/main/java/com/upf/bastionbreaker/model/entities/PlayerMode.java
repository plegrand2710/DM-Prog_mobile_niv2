package com.upf.bastionbreaker.model.entities;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.upf.bastionbreaker.model.graphics.TextureManager;

public abstract class PlayerMode {
    protected TextureRegion texture;
    protected float speed;
    protected int weight;
    protected float width, height;
    protected boolean canJump;

    public PlayerMode(String spriteName, float speed, int weight, float width, float height, boolean canJump) {
        TextureAtlas atlas = TextureManager.getGameAtlas();
        this.texture = atlas.findRegion(spriteName);
        this.speed = speed;
        this.weight = weight;
        this.width = width;
        this.height = height;
        this.canJump = canJump;
    }

    public TextureRegion getTexture() { return texture; }
    public float getSpeed() { return speed; }
    public int getWeight() { return weight; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public boolean canJump() { return canJump; }
}
