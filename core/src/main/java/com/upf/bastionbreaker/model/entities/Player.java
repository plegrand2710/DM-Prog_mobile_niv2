package com.upf.bastionbreaker.model.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.upf.bastionbreaker.view.screens.MapRenderer;

public class Player {
    private float x, y;
    private PlayerMode currentMode;
    private int hp, shield;

    public Player(float startX, float startY) {
        this.x = startX / MapRenderer.TILE_SIZE;
        this.y = startY / MapRenderer.TILE_SIZE;
        this.hp = 100;
        this.shield = 0;
        this.currentMode = new Tank(); // Débute en Tank
    }

    public void transform() {
        if (currentMode instanceof Tank) {
            currentMode = new Robot();
        } else {
            currentMode = new Tank();
        }
        System.out.println("🔄 Transformation en " + (currentMode instanceof Tank ? "TANK" : "ROBOT"));
    }

    public void move(float deltaX) {
        x += deltaX * currentMode.getSpeed();
    }

    public void jump() {
        if (currentMode.canJump()) {
            System.out.println("🤖 Robot a sauté !");
            // Ajouter ici la logique pour le saut
        }
    }

    public void render(SpriteBatch batch) {
        if (currentMode.getTexture() != null) {
            batch.draw(currentMode.getTexture(), x, y, currentMode.getWidth(), currentMode.getHeight());
        }
    }

    public Rectangle getBoundingBox() {
        return new Rectangle(x, y, currentMode.getWidth(), currentMode.getHeight());
    }

    public int getWeight() {
        return currentMode.getWeight();
    }

    public int getHp() { return hp; }
    public int getShield() { return shield; }

    public void collectFlyingBox(FlyingBox box) {
        if (box.getEffectType().equalsIgnoreCase("heal")) {
            hp = Math.min(100, hp + 20);
            System.out.println("Player healed: HP = " + hp);
        } else if (box.getEffectType().equalsIgnoreCase("shield")) {
            shield = Math.min(100, shield + 20);
            System.out.println("Player shield increased: Shield = " + shield);
        }
    }
}
