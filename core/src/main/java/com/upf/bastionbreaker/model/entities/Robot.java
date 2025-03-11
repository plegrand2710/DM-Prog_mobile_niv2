package com.upf.bastionbreaker.model.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.upf.bastionbreaker.model.graphics.TextureManager;

public class Robot extends PlayerMode {
    private Animation<TextureRegion> walkForwardAnimation;
    private Animation<TextureRegion> walkBackwardAnimation;
    private Animation<TextureRegion> turnAnimation;
    private TextureRegion currentFrame;
    private float stateTime;
    private boolean facingForward = true; // Indique si le robot regarde vers l'avant
    private Sound walkSound;
    private long walkSoundId;
    private boolean isPlayingSound = false; // Pour éviter de rejouer en boucle le son

    public Robot() {
        // 🔹 Correction : Passer "robot_1_forward" au lieu de TextureRegion
        super("robot_1_forward", 3.0f, 80, 1.0f, 2.0f, true);
        this.stateTime = 0;

        // Charger les animations
        loadAnimations();
        walkSound = Gdx.audio.newSound(Gdx.files.internal("assets/sounds/robot_walk.ogg"));
    }

    private void loadAnimations() {
        TextureAtlas atlas = TextureManager.getGameAtlas();

        // Animation pour avancer
        Array<TextureRegion> framesForward = new Array<>();
        framesForward.add(atlas.findRegion("robot_1_forward"));
        framesForward.add(atlas.findRegion("robot_2_forward"));
        framesForward.add(atlas.findRegion("robot_1_forward"));
        framesForward.add(atlas.findRegion("robot_3_forward"));
        framesForward.add(atlas.findRegion("robot_1_forward"));
        walkForwardAnimation = new Animation<>(0.1f, framesForward, Animation.PlayMode.LOOP);

        // Animation pour reculer
        Array<TextureRegion> framesBackward = new Array<>();
        framesBackward.add(atlas.findRegion("robot_1_backward"));
        framesBackward.add(atlas.findRegion("robot_2_backward"));
        framesBackward.add(atlas.findRegion("robot_1_backward"));
        framesBackward.add(atlas.findRegion("robot_3_backward"));
        framesBackward.add(atlas.findRegion("robot_1_backward"));
        walkBackwardAnimation = new Animation<>(0.1f, framesBackward, Animation.PlayMode.LOOP);

        // Animation pour le demi-tour
        Array<TextureRegion> framesTurn = new Array<>();
        framesTurn.add(atlas.findRegion("robot_1_forward"));
        framesTurn.add(atlas.findRegion("robot_facing"));
        framesTurn.add(atlas.findRegion("robot_1_backward"));
        turnAnimation = new Animation<>(0.15f, framesTurn, Animation.PlayMode.NORMAL);
    }

    public void update(float delta, boolean movingForward, boolean movingBackward, boolean turning) {
        stateTime += delta;

        if (turning) {
            currentFrame = turnAnimation.getKeyFrame(stateTime, false);
            facingForward = !facingForward;
        } else if (movingForward) {
            currentFrame = walkForwardAnimation.getKeyFrame(stateTime);
            playWalkSound();
        } else if (movingBackward) {
            currentFrame = walkBackwardAnimation.getKeyFrame(stateTime);
            playWalkSound();
        } else {
            currentFrame = facingForward ? walkForwardAnimation.getKeyFrame(0) : walkBackwardAnimation.getKeyFrame(0);
            stopWalkSound();
        }
    }

    private void playWalkSound() {
        if (!isPlayingSound) {
            walkSoundId = walkSound.loop();
            isPlayingSound = true;
        }
    }

    private void stopWalkSound() {
        if (isPlayingSound) {
            walkSound.stop(walkSoundId);
            isPlayingSound = false;
        }
    }

    @Override
    public TextureRegion getTexture() {
        return currentFrame;
    }

    public void dispose() {
        if (walkSound != null) {
            walkSound.dispose();
        }
    }
}
