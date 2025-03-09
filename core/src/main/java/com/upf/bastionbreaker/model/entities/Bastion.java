package com.upf.bastionbreaker.model.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.upf.bastionbreaker.model.map.GameObject;
import com.upf.bastionbreaker.model.graphics.TextureManager;
import com.upf.bastionbreaker.view.screens.MapRenderer;

public class Bastion {
    private float x, y, width, height;
    private TextureRegion texture;
    private int hp;
    private boolean breakable;
    private boolean destroyed;

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
     * Ici, vous pouvez ajouter une animation d'explosion si nécessaire.
     */
    private void destroy() {
        destroyed = true;
        System.out.println("💥 Bastion détruit !");
        // Optionnel : déclencher ici une animation d'explosion ou un effet sonore.
    }

    /**
     * Indique si le bastion a été détruit.
     */
    public boolean isDestroyed() {
        return destroyed;
    }
}
