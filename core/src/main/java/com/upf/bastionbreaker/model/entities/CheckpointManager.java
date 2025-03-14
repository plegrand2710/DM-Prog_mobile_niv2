package com.upf.bastionbreaker.model.entities;

import com.badlogic.gdx.math.Vector2;

public class CheckpointManager {
    private static Vector2 lastCheckpoint = new Vector2(5, 5); // 🚀 Position de départ par défaut

    public static void setLastCheckpoint(float x, float y) {
        lastCheckpoint.set(x, y);
        System.out.println("✅ Checkpoint mis à jour : (" + x + ", " + y + ")");
    }

    public static Vector2 getLastCheckpoint() {
        return lastCheckpoint;
    }
}
