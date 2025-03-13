package com.upf.bastionbreaker.model.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.*;
import com.upf.bastionbreaker.model.map.GameObject;
import com.upf.bastionbreaker.view.screens.MapRenderer;
import com.upf.bastionbreaker.model.physics.WorldManager;

public class Hill {
    private float climb;
    private float slide;
    private boolean difficulty;
    private Body body;

    public Hill(GameObject gameObject) {
        // Récupération des propriétés
        Float climbVal = gameObject.getProperty("climb", Float.class);
        Float slideVal = gameObject.getProperty("slide", Float.class);
        Boolean difficultyVal = gameObject.getProperty("difficulty", Boolean.class);
        this.climb = (climbVal != null) ? climbVal : 1f;
        this.slide = (slideVal != null) ? slideVal : 1f;
        this.difficulty = (difficultyVal != null) ? difficultyVal : false;

        // Récupération des points du polygone depuis Tiled
        float[] polygonPoints = gameObject.getPolygonPoints();
        if (polygonPoints == null || polygonPoints.length < 6) {
            System.out.println("⚠️ Aucune donnée polygonale pour Hill.");
            return;
        }

        // Conversion correcte des points :
        // On prend la position en pixels de l'objet, on additionne les points relatifs, puis on divise par TILE_SIZE
        float posX = gameObject.getX();
        float posY = gameObject.getY();
        int numPoints = polygonPoints.length / 2;
        float[] vertices = new float[polygonPoints.length];
        for (int i = 0; i < numPoints; i++) {
            vertices[2 * i] = (posX + polygonPoints[2 * i]) / MapRenderer.TILE_SIZE;
            vertices[2 * i + 1] = (posY + polygonPoints[2 * i + 1]) / MapRenderer.TILE_SIZE;
        }

        // Calculer le centre (moyenne) pour positionner le body
        float sumX = 0, sumY = 0;
        for (int i = 0; i < numPoints; i++){
            sumX += vertices[2 * i];
            sumY += vertices[2 * i + 1];
        }
        float centerX = sumX / numPoints;
        float centerY = sumY / numPoints;

        // Création du body Box2D statique pour la pente
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(centerX, centerY);
        body = WorldManager.getWorld().createBody(bodyDef);

        // Création d'un ChainShape à partir des points convertis
        // Pour une ChainShape, les points doivent être relatifs à la position du body
        float[] relativeVertices = new float[vertices.length];
        for (int i = 0; i < numPoints; i++){
            relativeVertices[2 * i] = vertices[2 * i] - centerX;
            relativeVertices[2 * i + 1] = vertices[2 * i + 1] - centerY;
        }
        ChainShape chain = new ChainShape();
        chain.createChain(relativeVertices);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = chain;
        // Friction élevée pour éviter que le joueur glisse trop sur la pente
        fixtureDef.friction = 1.0f;
        fixtureDef.restitution = 0f;
        body.createFixture(fixtureDef);
        chain.dispose();

        // Pour que CollisionHandler reconnaisse cette pente
        body.setUserData(this);
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

    public void render(SpriteBatch batch) {
        // Optionnel : ajouter un rendu de debug pour visualiser la pente
    }
}
