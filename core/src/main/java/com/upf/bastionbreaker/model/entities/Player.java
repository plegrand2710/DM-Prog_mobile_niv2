package com.upf.bastionbreaker.model.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.upf.bastionbreaker.view.screens.MapRenderer;

public class Player {
    private float x, y;
    private PlayerMode currentMode;
    private int hp, shield;

    // Gestion des mouvements
    private boolean movingForward = false;
    private boolean movingBackward = false;
    private boolean turning = false;

    public Player(float startX, float startY) {
        // Conversion de la position initiale en unités de tuiles
        this.x = startX; // Les coordonnées sont déjà en unités de tuiles
        this.y = startY;
        this.hp = 100;
        this.shield = 0;
        this.currentMode = new Tank(); // Démarrage en mode Tank par défaut
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
            // Logique de saut à ajouter ici
        }
    }

    public void render(SpriteBatch batch) {
        if (currentMode.getTexture() != null) {
            batch.draw(currentMode.getTexture(), x, y, currentMode.getWidth(), currentMode.getHeight());
        }
    }

    // Ajout d'un getter pour le rectangle de collision
    public Rectangle getBoundingBox() {
        return new Rectangle(x, y, currentMode.getWidth(), currentMode.getHeight());
    }

    public int getWeight() {
        return currentMode.getWeight();
    }

    public int getHp() {
        return hp;
    }

    public int getShield() {
        return shield;
    }

    public void collectFlyingBox(FlyingBox box) {
        if (box.getEffectType().equalsIgnoreCase("heal")) {
            hp = Math.min(100, hp + 20);
            System.out.println("Player healed: HP = " + hp);
        } else if (box.getEffectType().equalsIgnoreCase("shield")) {
            shield = Math.min(100, shield + 20);
            System.out.println("Player shield increased: Shield = " + shield);
        }
    }

    public void update(float delta) {
        if (currentMode instanceof Robot) {
            ((Robot) currentMode).update(delta, movingForward, movingBackward, turning);
        }
        if (currentMode instanceof Tank) {
            if (movingForward) {
                ((Tank) currentMode).playEngineSound();
            } else {
                ((Tank) currentMode).stopEngineSound();
            }
        }
    }

    // Ajout d'un getter pour la texture du mode courant
    public Object getTexture() {
        return currentMode.getTexture();
    }

    // Ajout d'un setter pour position
    public void setPosition(float newX, float newY) {
        this.x = newX;
        this.y = newY;
    }

    // Getters et Setters pour le mouvement
    public void setMovingForward(boolean movingForward) {
        this.movingForward = movingForward;
    }

    public void setMovingBackward(boolean movingBackward) {
        this.movingBackward = movingBackward;
    }

    public void setTurning(boolean turning) {
        this.turning = turning;
    }
}
