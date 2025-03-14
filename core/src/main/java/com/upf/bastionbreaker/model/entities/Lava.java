package com.upf.bastionbreaker.model.entities;

import com.badlogic.gdx.physics.box2d.*;
import com.upf.bastionbreaker.model.map.GameObject;
import com.upf.bastionbreaker.model.map.MapManager;
import com.upf.bastionbreaker.model.physics.WorldManager;
import com.upf.bastionbreaker.view.screens.MapRenderer;
import java.util.List;

public class Lava {
    private Body body;

    public Lava(GameObject gameObject, World world) {
        float x = gameObject.getX() / MapRenderer.TILE_SIZE;
        float y = gameObject.getY() / MapRenderer.TILE_SIZE;
        float width = gameObject.getWidth() / MapRenderer.TILE_SIZE;
        float height = gameObject.getHeight() / MapRenderer.TILE_SIZE;

        // Création du body Box2D statique pour la lave
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x + width / 2, y + height / 2);
        body = world.createBody(bodyDef);

        // Définition de la forme de la lave
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true; // 🚨 La lave doit être un capteur, pas un obstacle solide !

        body.createFixture(fixtureDef);
        body.setUserData("Lava"); // 🔥 On identifie la lave pour la collision

        shape.dispose();
    }

    public static void createLavaZones(World world) {
        MapManager mapManager = MapManager.getInstance();
        if (mapManager == null) {
            System.out.println("❌ ERREUR : `MapManager` n'est pas initialisé !");
            return;
        }

        List<GameObject> lavaObjects = mapManager.getObjects("Lava");
        if (lavaObjects == null || lavaObjects.isEmpty()) {
            System.out.println("⚠️ Aucun objet `Lava` trouvé dans la carte.");
            return;
        }

        for (GameObject obj : lavaObjects) {
            new Lava(obj, world);
        }
        System.out.println("✅ " + lavaObjects.size() + " zones de lave ajoutées !");
    }
}
