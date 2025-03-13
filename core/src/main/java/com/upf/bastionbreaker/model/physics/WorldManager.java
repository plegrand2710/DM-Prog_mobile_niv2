package com.upf.bastionbreaker.model.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.upf.bastionbreaker.model.map.GameObject;
import com.upf.bastionbreaker.model.map.MapManager;
import com.upf.bastionbreaker.view.screens.MapRenderer;
import java.util.List;

public class WorldManager {
    private static World world;
    private static final Vector2 GRAVITY = new Vector2(0, -9.8f);

    public static void initialize() {
        if (world == null) {
            world = new World(GRAVITY, true);
            world.setContactListener(new CollisionHandler());
            createFloorBodies();  // Création des bodies pour le sol
        }
    }

    public static void createFloorBodies() {
        System.out.println("🛠️ Création des bodies pour le sol...");
        // Récupérer la liste des objets "Floor" via le singleton MapManager
        List<GameObject> floorObjects = MapManager.getInstance().getObjects("Floor");
        // Facteur de conversion : de pixels en unités Box2D
        float scale = 1f / MapRenderer.TILE_SIZE;

        for (GameObject obj : floorObjects) {
            // Vérifier que la propriété "collision" est activée
            String collisionProp = obj.getProperty("collision", String.class);
            if (collisionProp == null || !Boolean.parseBoolean(collisionProp)) {
                continue;
            }

            // Créer un BodyDef statique
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;

            // Vérifier et appliquer la rotation (si définie dans Tiled)
            String rotationProp = obj.getProperty("rotation", String.class);
            if (rotationProp != null) {
                try {
                    float rotation = Float.parseFloat(rotationProp);
                    bodyDef.angle = (float)Math.toRadians(rotation);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            // Calculer le centre de l'objet en appliquant le facteur de conversion
            float centerX = (obj.getX() + obj.getWidth() / 2) * scale;
            float centerY = (obj.getY() + obj.getHeight() / 2) * scale;
            bodyDef.position.set(centerX, centerY);

            Body body = world.createBody(bodyDef);
            PolygonShape shape = new PolygonShape();
            // Créer la forme en convertissant la largeur et la hauteur en unités Box2D
            shape.setAsBox((obj.getWidth() / 2) * scale, (obj.getHeight() / 2) * scale);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.friction = 0.5f;
            body.createFixture(fixtureDef);
            shape.dispose();

            // Utiliser un identifiant dans le userData pour le ContactListener
            body.setUserData("Floor");
        }
        System.out.println("✅ Sol chargé avec succès !");
    }

    public static World getWorld() {
        if (world == null) {
            initialize();
        }
        return world;
    }

    public static void update(float delta) {
        world.step(delta, 6, 2);
    }

    public static void dispose() {
        if (world != null) {
            world.dispose();
        }
    }
}
