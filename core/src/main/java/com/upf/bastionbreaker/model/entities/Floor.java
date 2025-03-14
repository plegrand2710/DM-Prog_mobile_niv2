package com.upf.bastionbreaker.model.entities;

import com.badlogic.gdx.physics.box2d.*;
import com.upf.bastionbreaker.model.map.GameObject;
import com.upf.bastionbreaker.model.map.MapManager;
import com.upf.bastionbreaker.model.physics.WorldManager;
import com.upf.bastionbreaker.view.screens.MapRenderer;
import java.util.List;

public class Floor {
    private Body body;

    public Floor(GameObject gameObject, World world) {
        float x = gameObject.getX() / MapRenderer.TILE_SIZE;
        float y = gameObject.getY() / MapRenderer.TILE_SIZE;
        float width = gameObject.getWidth() / MapRenderer.TILE_SIZE;
        float height = gameObject.getHeight() / MapRenderer.TILE_SIZE;

        // Définir le corps statique
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x + width / 2, y + height / 2); // Centrage

        body = world.createBody(bodyDef);

        // Définir la forme et la fixture
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0f;

        body.createFixture(fixtureDef);
        body.setUserData("Floor");

        shape.dispose();
    }

    public static void createFloors(World world) {
        // 🔥 Vérifie si `MapManager` est initialisé
        MapManager mapManager = MapManager.getInstance();
        if (mapManager == null) {
            System.out.println("❌ ERREUR : `MapManager` n'est pas initialisé !");
            return; // 🚨 On arrête ici pour éviter le crash
        }

        List<GameObject> floors = mapManager.getObjects("Floor");
        if (floors == null) {
            System.out.println("⚠️ Aucun sol détecté dans la carte !");
            return;
        }

        for (GameObject obj : floors) {
            new Floor(obj, world);
        }
        System.out.println("✅ " + floors.size() + " sols ajoutés à Box2D !");
    }

}
