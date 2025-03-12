package com.upf.bastionbreaker.model.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class WorldManager {
    private static World world;
    private static final Vector2 GRAVITY = new Vector2(0, -9.8f);

    public static void initialize() {
        if (world == null) {
            world = new World(GRAVITY, true);
            world.setContactListener(new CollisionHandler());
        }
    }

    public static World getWorld() {
        if (world == null) {
            initialize();
        }
        return world;
    }

    public static void update(float delta) {
        // Effectue une étape de simulation Box2D
        world.step(delta, 6, 2);
    }

    public static void dispose() {
        if (world != null) {
            world.dispose();
        }
    }
}
