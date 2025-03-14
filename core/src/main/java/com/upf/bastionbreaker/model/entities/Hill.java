package com.upf.bastionbreaker.model.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.*;
import com.upf.bastionbreaker.model.map.GameObject;
import com.upf.bastionbreaker.model.map.MapManager;
import com.upf.bastionbreaker.view.screens.MapRenderer;
import com.upf.bastionbreaker.model.physics.WorldManager;
import java.util.List;

public class Hill {
    private float climb;
    private float slide;
    private boolean difficulty;
    private Body body;
    // Stockage des vertices relatifs pour la hitbox (optionnel pour debug)
    private float[] hitboxVertices;

    /**
     * Construit une instance de Hill et crée le body statique correspondant.
     * @param gameObject l'objet Tiled représentant la pente.
     * @param world le monde Box2D dans lequel créer le body.
     */
    public Hill(GameObject gameObject, World world) {
        // Récupération des propriétés climb, slide et difficulty
        Float climbVal = gameObject.getProperty("climb", Float.class);
        Float slideVal = gameObject.getProperty("slide", Float.class);
        Boolean difficultyVal = gameObject.getProperty("difficulty", Boolean.class);
        this.climb = (climbVal != null) ? climbVal : 1f;
        this.slide = (slideVal != null) ? slideVal : 1f;
        this.difficulty = (difficultyVal != null) ? difficultyVal : false;

        // Récupération des points du polygone défini dans Tiled
        float[] polygonPoints = gameObject.getPolygonPoints();
        if (polygonPoints == null || polygonPoints.length < 6) {
            System.out.println("⚠️ Aucune donnée polygonale pour Hill.");
            return;
        }

        // Conversion des points : on ajoute la position de l'objet (en pixels), puis on convertit en unités de jeu
        float posX = gameObject.getX();
        float posY = gameObject.getY();
        int numPoints = polygonPoints.length / 2;
        float[] vertices = new float[polygonPoints.length];
        for (int i = 0; i < numPoints; i++) {
            vertices[2 * i] = (posX + polygonPoints[2 * i]) / MapRenderer.TILE_SIZE;
            vertices[2 * i + 1] = (posY + polygonPoints[2 * i + 1]) / MapRenderer.TILE_SIZE;
        }

        // Calcul du centre pour positionner le body
        float sumX = 0, sumY = 0;
        for (int i = 0; i < numPoints; i++){
            sumX += vertices[2 * i];
            sumY += vertices[2 * i + 1];
        }
        float centerX = sumX / numPoints;
        float centerY = sumY / numPoints;

        // Calcul des vertices relatifs pour le ChainShape
        hitboxVertices = new float[vertices.length];
        for (int i = 0; i < numPoints; i++){
            hitboxVertices[2 * i] = vertices[2 * i] - centerX;
            hitboxVertices[2 * i + 1] = vertices[2 * i + 1] - centerY;
        }

        // Création du body statique pour la pente
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(centerX, centerY);
        body = world.createBody(bodyDef);

        // Création d'un ChainShape à partir des vertices relatifs
        ChainShape chain = new ChainShape();
        chain.createChain(hitboxVertices);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = chain;
        // Friction élevée pour permettre au joueur de marcher sans glisser (comme un sol)
        fixtureDef.friction = 1.0f;
        fixtureDef.restitution = 0f;
        body.createFixture(fixtureDef);
        chain.dispose();

        // Identifier ce body dans le CollisionHandler (pour le traitement des collisions)
        body.setUserData(this);
    }

    /**
     * Crée des instances de Hill pour tous les objets du calque "Hill" et les ajoute au monde Box2D.
     * @param world le monde Box2D dans lequel créer les pentes.
     */
    public static void createHills(World world) {
        MapManager mapManager = MapManager.getInstance();
        if (mapManager == null) {
            System.out.println("❌ ERREUR: MapManager non initialisé.");
            return;
        }
        List<GameObject> hillObjects = mapManager.getObjects("Hill");
        if (hillObjects == null || hillObjects.isEmpty()) {
            System.out.println("⚠️ Aucun Hill trouvé dans la carte.");
            return;
        }
        for (GameObject obj : hillObjects) {
            new Hill(obj, world);
        }
        System.out.println("✅ " + hillObjects.size() + " pentes ajoutées à Box2D !");
    }

    public float getClimb() {
        return climb;
    }

    public float getSlide() {
        return slide;
    }

    public boolean isDifficulty() {
        return difficulty;
    }

    public Body getBody() {
        return body;
    }

    /**
     * Méthode de rendu de la hitbox pour le debug, similaire à celle utilisée dans Floor.
     * Nécessite un ShapeRenderer configuré avec la caméra.
     * @param shapeRenderer le ShapeRenderer utilisé pour dessiner la hitbox.
     */
    public void renderHitbox(ShapeRenderer shapeRenderer) {
        if (hitboxVertices == null || hitboxVertices.length < 4) return;
        // Pour fermer le contour, on ajoute le premier point à la fin
        float[] polygon = new float[hitboxVertices.length + 2];
        System.arraycopy(hitboxVertices, 0, polygon, 0, hitboxVertices.length);
        polygon[polygon.length - 2] = hitboxVertices[0];
        polygon[polygon.length - 1] = hitboxVertices[1];
        shapeRenderer.setColor(1, 0, 0, 1); // Rouge
        shapeRenderer.polygon(polygon);
    }
}
