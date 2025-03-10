package com.upf.bastionbreaker.model.entities;

import com.badlogic.gdx.math.Rectangle;

public class Player {
    private float x, y, width, height;
    private int hp;
    private int shield;

    // Mode de jeu : true si Tank, false si Robot
    private boolean isTank = true; // Par défaut, le joueur commence en mode Tank

    public Player() {
        // Initialisation des propriétés du joueur
        this.x = 1f;
        this.y = 1f;
        this.width = 1f;
        this.height = 1f;
        this.hp = 100;
        this.shield = 0;
    }

    /**
     * Retourne le poids du joueur en fonction de son mode.
     * Mode Tank : 120 kg, Mode Robot : 80 kg.
     */
    public int getWeight() {
        return isTank ? 120 : 80;
    }

    public boolean isTank() {
        return isTank;
    }

    public void setTankMode(boolean isTank) {
        this.isTank = isTank;
    }

    /**
     * Méthode appelée lors du passage sur un Ice Bridge.
     * (La logique de destruction est gérée dans GameScreen)
     */
    public void update() {
        // Mettre ici la logique de mise à jour du joueur (déplacement, etc.)
    }

    /**
     * Renvoie le rectangle de collision du joueur.
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

    /**
     * Applique l'effet d'une FlyingBox sur le joueur.
     * Si la box est de type "heal", ajoute 20 HP (sans dépasser 100).
     * Si la box est de type "shield", ajoute 20 Shield (sans dépasser 100).
     */
    public void collectFlyingBox(com.upf.bastionbreaker.model.entities.FlyingBox box) {
        if (box.getEffectType().equalsIgnoreCase("heal")) {
            hp = Math.min(100, hp + 20);
            System.out.println("Player healed: HP = " + hp);
        } else if (box.getEffectType().equalsIgnoreCase("shield")) {
            shield = Math.min(100, shield + 20);
            System.out.println("Player shield increased: Shield = " + shield);
        }
    }
}
