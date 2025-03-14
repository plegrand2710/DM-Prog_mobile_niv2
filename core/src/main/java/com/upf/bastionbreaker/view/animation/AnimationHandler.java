package com.upf.bastionbreaker.view.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import java.util.HashMap;
import com.upf.bastionbreaker.model.graphics.TextureManager;

public class AnimationHandler {
    private static HashMap<String, Animation<TextureRegion>> animations = new HashMap<>();

    public static void loadAnimations() {
        System.out.println("🔄 Chargement des animations...");

        TextureAtlas atlas = TextureManager.getGameAtlas();
        if (atlas == null) {
            System.out.println("❌ ERREUR : game.atlas n'est pas chargé !");
            return;
        }

        addAnimation(atlas, "robot_walk_forward", new String[]{
            "robot_1_forward", "robot_2_forward", "robot_1_forward", "robot_3_forward", "robot_1_forward"
        }, 0.2f);

        addAnimation(atlas, "robot_walk_backward", new String[]{
            "robot_1_backward", "robot_2_backward", "robot_1_backward", "robot_3_backward", "robot_1_backward"
        }, 0.2f);

        addAnimation(atlas, "robot_turn_back", new String[]{
            "robot_1_forward", "robot_facing", "robot_1_backward"
        }, 0.15f, Animation.PlayMode.NORMAL);

        addAnimation(atlas, "robot_turn_front", new String[]{
            "robot_1_backward", "robot_facing", "robot_1_forward"
        }, 0.15f, Animation.PlayMode.NORMAL);

        addAnimation(atlas, "robot_idle", new String[]{"robot_1_forward"}, 0.2f);
        addAnimation(atlas, "tank_idle", new String[]{"tank"}, 0.2f);

        System.out.println("✅ Animations chargées avec succès !");
    }

    private static void addAnimation(TextureAtlas atlas, String key, String[] frameNames, float frameDuration) {
        addAnimation(atlas, key, frameNames, frameDuration, Animation.PlayMode.LOOP);
    }

    private static void addAnimation(TextureAtlas atlas, String key, String[] frameNames, float frameDuration, Animation.PlayMode playMode) {
        Array<TextureRegion> frames = new Array<>();
        boolean missingTexture = false;

        for (String frameName : frameNames) {
            TextureRegion region = atlas.findRegion(frameName);
            if (region == null) {
                System.out.println("❌ ERREUR : Texture '" + frameName + "' introuvable !");
                missingTexture = true;
            } else {
                frames.add(region);
            }
        }

        if (!missingTexture) {
            animations.put(key, new Animation<>(frameDuration, frames, playMode));
        } else {
            System.out.println("⚠️ Animation '" + key + "' ignorée car certaines textures sont absentes !");
        }
    }

    public static Animation<TextureRegion> getAnimation(String key) {
        if (!animations.containsKey(key)) {
            System.out.println("⚠️ Attention : Animation '" + key + "' introuvable !");
            return null;
        }
        return animations.get(key);
    }
}
