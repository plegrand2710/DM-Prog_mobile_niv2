package com.upf.bastionbreaker.model.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.upf.bastionbreaker.view.screens.MapRenderer;
import com.upf.bastionbreaker.model.audio.SoundManager;
import com.upf.bastionbreaker.view.animation.AnimationHandler;
import com.badlogic.gdx.math.MathUtils;

public class Player {
    private float x, y;
    private PlayerMode currentMode;
    private int hp, shield;
    private boolean isOnGround = true; // Pour le saut
    private Vector2 velocity = new Vector2(0, 0);
    private static final float JUMP_FORCE = 5f;
    private static final float GRAVITY = -9.8f;

    // Gestion des animations
    private TextureRegion currentAnimation;
    private boolean facingRight = true;
    private float stateTime = 0;

    // Gestion des mouvements
    private boolean movingForward = false;
    private boolean movingBackward = false;
    private boolean turning = false;

    public Player(float startX, float startY) {
        this.x = startX;
        this.y = startY;
        this.hp = 100;
        this.shield = 0;
        this.currentMode = new Tank(); // Démarrage en mode Tank par défaut
        this.currentAnimation = AnimationHandler.getAnimation("tank_idle").getKeyFrame(0, true);
    }

    public void switchMode() {
        if (currentMode instanceof Tank) {
            currentMode = new Robot();
            SoundManager.stopSound("tank_engine");
        } else {
            currentMode = new Tank();
        }
        System.out.println("🔄 Transformation en " + (currentMode instanceof Tank ? "TANK" : "ROBOT"));
    }

    public void move(float deltaX) {
        x += deltaX * currentMode.getSpeed();
    }

    public void jump() {
        if (currentMode instanceof Robot && isOnGround) { // ✅ Vérifie s'il peut sauter
            velocity.y = JUMP_FORCE;
            isOnGround = false;
            SoundManager.playSound("robot_jump");

            System.out.println("🟢 [DEBUG] Jump effectué !");
        } else {
            System.out.println("❌ [DEBUG] Impossible de sauter !");
        }
    }


    public void update(float delta) {
        stateTime += delta; // Mise à jour du temps d'animation

        // Appliquer la gravité
        if (!isOnGround) {
            velocity.y += GRAVITY * delta;
            y += velocity.y * delta;
        }

        // Vérifier si le joueur touche le sol (limite basse)
        if (y <= 0) {
            y = 0;
            isOnGround = true;
            velocity.y = 0;
        }

        // Gestion du mouvement horizontal
        float movementSpeed = currentMode.getSpeed() * delta;
        if (movingForward) {
            x += movementSpeed;
        }
        if (movingBackward) {
            x -= movementSpeed;
        }

        // Gestion du son du Tank
        if (currentMode instanceof Tank) {
            if (!SoundManager.isPlaying("tank_engine")) {
                SoundManager.playLoopingSound("tank_engine", 0.4f);
            }
            if (movingForward || movingBackward) {
                SoundManager.adjustVolume("tank_engine", 0.7f);
            } else {
                SoundManager.adjustVolume("tank_engine", 0.4f);
            }
        }

        // Gestion du son du Robot
        if (currentMode instanceof Robot) {
            if (isMoving()) {
                if (!SoundManager.isPlaying("robot_walk")) {
                    SoundManager.playLoopingSound("robot_walk", 0.7f);
                }
            } else {
                SoundManager.stopSound("robot_walk");
            }
        } else {
            SoundManager.stopSound("robot_walk");
        }

        updateAnimation(delta);
    }

    public void updateAnimation(float delta) {
        if (currentMode instanceof Robot) {
            if (velocity.x > 0) {
                currentAnimation = AnimationHandler.getAnimation("robot_walk_right").getKeyFrame(stateTime, true);
                facingRight = true;
            } else if (velocity.x < 0) {
                currentAnimation = AnimationHandler.getAnimation("robot_walk_left").getKeyFrame(stateTime, true);
                facingRight = false;
            } else {
                currentAnimation = AnimationHandler.getAnimation("robot_idle").getKeyFrame(stateTime, true);
            }
        } else {
            currentAnimation = AnimationHandler.getAnimation("tank_idle").getKeyFrame(stateTime, true);
        }
    }

    public void collectFlyingBox(FlyingBox box) {
        if (box == null) return;

        if (box.getEffectType().equalsIgnoreCase("heal")) {
            hp = Math.min(100, hp + 20); // Limite la vie à 100
            System.out.println("🩹 Vie restaurée : " + hp);
            SoundManager.playSound("heal_sound");
        } else if (box.getEffectType().equalsIgnoreCase("shield")) {
            shield = Math.min(100, shield + 20); // Limite le bouclier à 100
            System.out.println("🛡️ Bouclier augmenté : " + shield);
            SoundManager.playSound("shield_sound");
        }

        // La désactivation de la FlyingBox est déjà gérée dans sa méthode onCollected()
    }

    public void render(SpriteBatch batch) {
        if (currentAnimation != null) {
            batch.draw(currentAnimation, x, y, currentMode.getWidth(), currentMode.getHeight());
        }
    }

    public boolean isMoving() {
        return movingForward || movingBackward;
    }

    public void setMovingForward(boolean movingForward) {
        this.movingForward = movingForward;
    }

    public void setMovingBackward(boolean movingBackward) {
        this.movingBackward = movingBackward;
    }

    public PlayerMode getCurrentMode() {
        return currentMode;
    }

    // --- Ajouts pour la gestion de la position et des collisions ---

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Rectangle getBoundingBox() {
        return new Rectangle(x, y, currentMode.getWidth(), currentMode.getHeight());
    }
}
