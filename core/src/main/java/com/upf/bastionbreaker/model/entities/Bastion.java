package com.upf.bastionbreaker.model.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import com.upf.bastionbreaker.model.map.GameObject;
import com.upf.bastionbreaker.model.graphics.TextureManager;
import com.upf.bastionbreaker.view.screens.MapRenderer;
import com.upf.bastionbreaker.model.physics.WorldManager;

public class Bastion {
    private float x, y, width, height;
    private TextureRegion texture;
    private int hp;
    private boolean breakable;
    private boolean destroyed;

    // Ajout d'un Body Box2D pour la physique
    private Body body;

    public Bastion(GameObject gameObject) {
        // Conversion des coordonnées de pixels en unités de tuiles
        this.x = gameObject.getX() / MapRenderer.TILE_SIZE;
        this.y = gameObject.getY() / MapRenderer.TILE_SIZE;
        this.width = gameObject.getWidth() / MapRenderer.TILE_SIZE;
        this.height = gameObject.getHeight() / MapRenderer.TILE_SIZE;

        // Récupération des points de vie (HP), par défaut 200
        Object hpProp = gameObject.getProperties().get("HP");
        if (hpProp instanceof Number) {
            this.hp = ((Number) hpProp).intValue();
        } else if (hpProp instanceof String) {
            try {
                this.hp = Integer.parseInt((String) hpProp);
            } catch (NumberFormatException e) {
                this.hp = 200;
            }
        } else {
            this.hp = 200;
        }

        // Récupération de la propriété breakable, par défaut true
        Object breakableProp = gameObject.getProperties().get("breakable");
        if (breakableProp instanceof Boolean) {
            this.breakable = (Boolean) breakableProp;
        } else if (breakableProp instanceof String) {
            this.breakable = Boolean.parseBoolean((String) breakableProp);
        } else {
            this.breakable = true;
        }

        // Chargement de la texture depuis l'atlas avec la propriété "sprite"
        String spriteName = gameObject.getProperty("sprite", String.class);
        if (spriteName == null) {
            spriteName = "wall"; // Valeur par défaut
        }
        TextureAtlas atlas = TextureManager.getGameAtlas();
        this.texture = atlas.findRegion(spriteName);
        if (this.texture == null) {
            System.out.println("❌ ERREUR : Texture '" + spriteName + "' introuvable pour Bastion !");
        }

        destroyed = false;

        // Création du body Box2D pour le Bastion (StaticBody)
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        // Positionner le body au centre du Bastion
        bodyDef.position.set(x + width / 2, y + height / 2);
        body = WorldManager.getWorld().createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0f;

        body.createFixture(fixtureDef);
        shape.dispose();

        // Identifier ce body pour le ContactListener
        body.setUserData("Bastion");
    }

    /**
     * Affiche le bastion s'il n'est pas détruit.
     */
    public void render(SpriteBatch batch) {
        if (!destroyed && texture != null) {
            batch.draw(texture, x, y, width, height);
        }
    }

    /**
     * Applique des dégâts au bastion.
     * Chaque appel de cette méthode inflige une certaine quantité de dégâts.
     * Si les HP tombent à 0 ou moins, le bastion est détruit.
     *
     * @param damage La quantité de dégâts à infliger (par exemple 20).
     */
    public void damage(int damage) {
        if (!destroyed && breakable) {
            hp -= damage;
            if (hp <= 0) {
                destroy();
            }
        }
    }

    /**
     * Détruit le bastion.
     * Supprime son body du monde Box2D et marque l'objet comme détruit.
     * Optionnel : ajouter une animation d'explosion avant la destruction.
     */
    private void destroy() {
        destroyed = true;
        System.out.println("💥 Bastion détruit !");
        // Supprimer le body du World pour qu'il ne soit plus pris en compte dans la physique
        if (body != null) {
            WorldManager.getWorld().destroyBody(body);
            body = null;
        }
        // Optionnel : déclencher une animation d'explosion ou un effet sonore.
    }

    /**
     * Indique si le bastion a été détruit.
     */
    public boolean isDestroyed() {
        return destroyed;
    }
}
