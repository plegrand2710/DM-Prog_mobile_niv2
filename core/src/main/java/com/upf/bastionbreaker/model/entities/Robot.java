package com.upf.bastionbreaker.model.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.upf.bastionbreaker.model.graphics.TextureManager;
import com.upf.bastionbreaker.view.animation.AnimationHandler;

public class Robot extends PlayerMode {
    private TextureRegion currentFrame;
    private float stateTime;
    private boolean facingForward = true; // Indique si le robot regarde vers l'avant
    private Sound walkSound;
    private long walkSoundId;
    private boolean isPlayingSound = false; // Pour éviter de rejouer en boucle le son

    public Robot() {
        super("robot_1_forward", 3.0f, 80, 1.0f, 2.0f, true);
        this.stateTime = 0;
        walkSound = Gdx.audio.newSound(Gdx.files.internal("sounds/robot_walk.ogg"));
    }

    public void update(float delta, boolean movingForward, boolean movingBackward, boolean turning) {
        stateTime += delta;

        if (turning) {
            currentFrame = AnimationHandler.getAnimation(facingForward ? "robot_turn_back" : "robot_turn_front")
                .getKeyFrame(stateTime, false);
            facingForward = !facingForward;
        } else if (movingForward) {
            currentFrame = AnimationHandler.getAnimation("robot_walk_forward").getKeyFrame(stateTime);
            playWalkSound();
        } else if (movingBackward) {
            currentFrame = AnimationHandler.getAnimation("robot_walk_backward").getKeyFrame(stateTime);
            playWalkSound();
        } else {
            currentFrame = AnimationHandler.getAnimation(facingForward ? "robot_walk_forward" : "robot_walk_backward")
                .getKeyFrame(0);
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
