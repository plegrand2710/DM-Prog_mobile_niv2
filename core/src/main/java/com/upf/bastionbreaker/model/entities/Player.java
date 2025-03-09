package com.upf.bastionbreaker.model.entities;

import com.badlogic.gdx.math.Rectangle;

public class Player {
    private float x, y, width, height;
    private int hp;
    private int shield;

    public Player() {
        // Initialisation par défaut du joueur
        this.x = 1f;
        this.y = 1f;
        this.width = 1f;
        this.height = 1f;
        this.hp = 100;
        this.shield = 0;
    }

    /**
     * Applique l'effet d'une FlyingBox au joueur.
     * Si c'est une box de type heal, ajoute 20 HP (max 100).
     * Si c'est une box de type shield, ajoute 20 Shield (max 100).
     */
    public void collectFlyingBox(FlyingBox box) {
        if (box.getEffectType().equalsIgnoreCase("heal")) {
            hp = Math.min(100, hp + 20);
            System.out.println("Player healed: HP = " + hp);
        } else if (box.getEffectType().equalsIgnoreCase("shield")) {
            shield = Math.min(100, shield + 20);
            System.out.println("Player shield increased: Shield = " + shield);
        }
    }

    /**
     * Renvoie les limites du joueur pour la détection de collision.
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // Getters et setters pour la position et la taille
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }

    public int getHp() { return hp; }
    public int getShield() { return shield; }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }
}
