package com.upf.bastionbreaker.model.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import com.upf.bastionbreaker.model.map.GameObject;
import com.upf.bastionbreaker.model.graphics.TextureManager;
import com.upf.bastionbreaker.view.screens.MapRenderer;
import com.upf.bastionbreaker.model.physics.WorldManager;

public class Obstacle {
    private float width, height;
    private TextureRegion texture;
    private int hp;
    private boolean destructible;
    private boolean movable;
    private Body body; // Body Box2D associé

    public Obstacle(GameObject gameObject) {
        // Conversion des dimensions (pixels -> unités de tuiles)
        this.width = gameObject.getWidth() / MapRenderer.TILE_SIZE;
        this.height = gameObject.getHeight() / MapRenderer.TILE_SIZE;

        // Récupération des propriétés spécifiques (HP, destructible, movable)
        Object hpProp = gameObject.getProperties().get("HP");
        this.hp = (hpProp instanceof Number) ? ((Number)hpProp).intValue() : 20;

        Object destructibleProp = gameObject.getProperties().get("destructible");
        if (destructibleProp instanceof Boolean) {
            this.destructible = (Boolean)destructibleProp;
        } else if (destructibleProp != null) {
            this.destructible = Boolean.parseBoolean(destructibleProp.toString());
        }

        Object movableProp = gameObject.getProperties().get("movable");
        if (movableProp instanceof Boolean) {
            this.movable = (Boolean)movableProp;
        } else if (movableProp != null) {
            this.movable = Boolean.parseBoolean(movableProp.toString());
        }

        // Récupération du sprite depuis le TMX et chargement de la texture correspondante
        String spriteName = gameObject.getProperty("sprite", String.class);
        if (spriteName != null) {
            TextureAtlas atlas = TextureManager.getGameAtlas();
            this.texture = atlas.findRegion(spriteName);
            if (this.texture == null) {
                System.out.println("❌ ERREUR : Texture '" + spriteName + "' introuvable pour l'obstacle !");
            }
        } else {
            System.out.println("⚠️ ATTENTION : L'obstacle '" + gameObject.getName() + "' n'a pas de sprite défini !");
        }

        // Création du body Box2D correspondant
        createBody(gameObject);
    }

    private void createBody(GameObject gameObject) {
        // Récupérer le monde physique depuis WorldManager
        World world = WorldManager.getWorld();

        // Conversion de la position de l'obstacle en unités de tuiles
        float x = gameObject.getX() / MapRenderer.TILE_SIZE;
        float y = gameObject.getY() / MapRenderer.TILE_SIZE;

        // Définir le BodyDef
        BodyDef bodyDef = new BodyDef();
        // Si l'obstacle est movable, le body est dynamique, sinon statique
        bodyDef.type = movable ? BodyDef.BodyType.DynamicBody : BodyDef.BodyType.StaticBody;
        // Positionner le body au centre de l'obstacle
        bodyDef.position.set(x + width / 2, y + height / 2);

        // Créer le body dans le monde
        body = world.createBody(bodyDef);
        // Stocker l'Obstacle dans le userData pour les collisions Box2D
        body.setUserData(this);

        // Définir la forme du body comme un rectangle
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        // Définir les caractéristiques physiques
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.6f;
        fixtureDef.restitution = 0.1f;
        fixtureDef.density = movable ? 1.0f : 0.0f; // Objets immobiles n'ont pas de densité

        // Créer le fixture
        body.createFixture(fixtureDef);
        shape.dispose();
    }

    public void render(SpriteBatch batch) {
        if (texture != null && body != null) {
            // Dessiner la texture centrée sur la position du Body
            float posX = body.getPosition().x - width / 2;
            float posY = body.getPosition().y - height / 2;
            batch.draw(texture, posX, posY, width, height);
        }
    }

    public boolean isDestructible() {
        return destructible;
    }

    public boolean isMovable() {
        return movable;
    }

    public int getHp() {
        return hp;
    }

    public Body getBody() {
        return body;
    }
}
