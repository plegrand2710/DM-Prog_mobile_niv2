package com.upf.bastionbreaker.model.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.upf.bastionbreaker.view.screens.MapRenderer;
import com.upf.bastionbreaker.model.audio.SoundManager;
import com.upf.bastionbreaker.view.animation.AnimationHandler;
import com.upf.bastionbreaker.model.physics.WorldManager;
import com.badlogic.gdx.math.MathUtils;

public class Player {
    // Remplacement des anciennes coordonnées par un Body Box2D
    private Body body;
    private PlayerMode currentMode;
    private int hp, shield;
    private boolean isOnGround = true; // Géré via les contacts Box2D (ContactListener)

    private static final float JUMP_FORCE = 5f; // Valeur d'impulsion (à ajuster)

    // Gestion des animations
    private TextureRegion currentAnimation;
    private boolean facingRight = true;
    private float stateTime = 0;

    // Indicateurs pour le mouvement (pour l'animation et le son)
    private boolean movingForward = false;
    private boolean movingBackward = false;

    public Player(float startX, float startY) {
        this.hp = 100;
        this.shield = 0;
        // Démarrer en mode Tank par défaut
        this.currentMode = new Tank();
        this.currentAnimation = AnimationHandler.getAnimation("tank_idle").getKeyFrame(0, true);

        // Création du body Box2D dynamique
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        // Positionner le body au centre du sprite
        bodyDef.position.set(startX, startY);
        body = WorldManager.getWorld().createBody(bodyDef);
        body.setUserData(this);

        // Créer une fixture en forme de rectangle (taille selon le mode courant)
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(currentMode.getWidth() / 2, currentMode.getHeight() / 2);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0.1f;
        body.createFixture(fixtureDef);
        shape.dispose();
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

    public void move(float moveX) {
        Vector2 currentVel = body.getLinearVelocity();
        // Calcul de la vitesse cible en fonction du mode et de l'input (moveX entre -1 et 1)
        float targetSpeed = moveX * currentMode.getSpeed();
        // Lissage pour une transition plus fluide (facteur 0.2f)
        float smoothedSpeed = MathUtils.lerp(currentVel.x, targetSpeed, 0.2f);
        body.setLinearVelocity(new Vector2(smoothedSpeed, currentVel.y));

        // Met à jour les indicateurs de direction
        setMovingForward(moveX > 0);
        setMovingBackward(moveX < 0);
    }

    public void jump() {
        if (currentMode instanceof Robot && isOnGround) {
            // Augmentation de la force de saut pour un effet plus puissant
            float enhancedJumpForce = 15f; // Nouvelle valeur d'impulsion
            body.applyLinearImpulse(new Vector2(0, enhancedJumpForce), body.getWorldCenter(), true);
            isOnGround = false;
            SoundManager.playSound("robot_jump");
            System.out.println("🟢 [DEBUG] Jump effectué avec force améliorée (" + enhancedJumpForce + ") !");
        } else {
            System.out.println("❌ [DEBUG] Impossible de sauter !");
        }
    }


    public void update(float delta) {
        stateTime += delta;
        Vector2 velocity = body.getLinearVelocity();

        // Détermine si le joueur se déplace réellement
        boolean isMoving = Math.abs(velocity.x) > 0.1f;

        // Gestion des sons en fonction du mode
        if (currentMode instanceof Tank) {
            // Le son du tank doit toujours être actif en mode Tank
            if (!SoundManager.isPlaying("tank_engine")) {
                SoundManager.playLoopingSound("tank_engine", 0.5f); // Volume de base
            }
            // Volume plus élevé en cas de déplacement
            float volume = isMoving ? 0.8f : 0.5f;
            SoundManager.adjustVolume("tank_engine", volume);
        } else if (currentMode instanceof Robot) {
            // Pour le robot, on augmente la puissance du son quand il bouge
            if (isMoving) {
                if (!SoundManager.isPlaying("robot_walk")) {
                    // Augmente le volume pour simuler une "vitesse" supérieure
                    SoundManager.playLoopingSound("robot_walk", 0.9f);
                }
            } else {
                SoundManager.stopSound("robot_walk");
            }
        } else {
            SoundManager.stopSound("tank_engine");
            SoundManager.stopSound("robot_walk");
        }

        // Mise à jour de l'animation en fonction de la vitesse horizontale
        if (currentMode instanceof Robot) {
            if (velocity.x > 0.1f) {
                // Utilisation du nouveau nom d'animation pour avancer
                currentAnimation = AnimationHandler.getAnimation("robot_walk_forward").getKeyFrame(stateTime, true);
                facingRight = true;
            } else if (velocity.x < -0.1f) {
                // Utilisation du nouveau nom d'animation pour reculer
                currentAnimation = AnimationHandler.getAnimation("robot_walk_backward").getKeyFrame(stateTime, true);
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
            hp = Math.min(100, hp + 20);
            System.out.println("🩹 Vie restaurée : " + hp);
            SoundManager.playSound("heal_sound");
        } else if (box.getEffectType().equalsIgnoreCase("shield")) {
            shield = Math.min(100, shield + 20);
            System.out.println("🛡️ Bouclier augmenté : " + shield);
            SoundManager.playSound("shield_sound");
        }
        // La désactivation de la FlyingBox est gérée dans sa méthode onCollected()
    }

    public void render(SpriteBatch batch) {
        if (currentAnimation != null) {
            float posX = body.getPosition().x - currentMode.getWidth() / 2;
            float posY = body.getPosition().y - currentMode.getHeight() / 2;
            batch.draw(currentAnimation, posX, posY, currentMode.getWidth(), currentMode.getHeight());
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

    public void setOnGround(boolean onGround) {
        this.isOnGround = onGround;
    }

    public PlayerMode getCurrentMode() {
        return currentMode;
    }

    public float getX() {
        return body.getPosition().x - currentMode.getWidth() / 2;
    }

    public float getY() {
        return body.getPosition().y - currentMode.getHeight() / 2;
    }

    public Body getBody() {
        return body;
    }


    public void setPosition(float x, float y) {
        // On positionne le body de façon à ce que (x,y) soit le coin inférieur gauche du sprite
        body.setTransform(x + currentMode.getWidth() / 2, y + currentMode.getHeight() / 2, body.getAngle());
    }

    public com.badlogic.gdx.math.Rectangle getBoundingBox() {
        return new com.badlogic.gdx.math.Rectangle(getX(), getY(), currentMode.getWidth(), currentMode.getHeight());
    }
}
