package com.upf.bastionbreaker.model.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.upf.bastionbreaker.model.map.GameObject;
import com.upf.bastionbreaker.model.graphics.TextureManager;
import com.upf.bastionbreaker.view.screens.MapRenderer;

public class UnstablePlatform {
    private float x, y, width, height;
    private TextureRegion texture;
    private float instability; // Vitesse de basculement
    private float maxAngle;    // Inclinaison maximale en degrés
    private boolean slippery;  // Surface glissante
    private float currentAngle; // Angle actuel de la plateforme
    private float targetAngle;  // Angle cible selon la position du joueur

    // Propriété pour la liaison avec un chain link (ex. chainlink15)
    private String linkedTopName;
    private ChainLink linkedChain; // Optionnel : pour suivre dynamiquement la position du chain link

    public UnstablePlatform(GameObject gameObject) {
        // Conversion des coordonnées (pixels -> unités de tuiles)
        this.x = gameObject.getX() / MapRenderer.TILE_SIZE;
        this.y = gameObject.getY() / MapRenderer.TILE_SIZE;
        this.width = gameObject.getWidth() / MapRenderer.TILE_SIZE;
        this.height = gameObject.getHeight() / MapRenderer.TILE_SIZE;

        // Récupérer la propriété "instability"
        Object instabilityProp = gameObject.getProperties().get("instability");
        if (instabilityProp instanceof Number) {
            this.instability = ((Number) instabilityProp).floatValue();
        } else if (instabilityProp instanceof String) {
            try {
                this.instability = Float.parseFloat((String) instabilityProp);
            } catch (NumberFormatException e) {
                this.instability = 2.5f;
            }
        } else {
            this.instability = 2.5f;
        }

        // Récupérer la propriété "max_angle"
        Object maxAngleProp = gameObject.getProperties().get("max_angle");
        if (maxAngleProp instanceof Number) {
            this.maxAngle = ((Number) maxAngleProp).floatValue();
        } else if (maxAngleProp instanceof String) {
            try {
                this.maxAngle = Float.parseFloat((String) maxAngleProp);
            } catch (NumberFormatException e) {
                this.maxAngle = 10.0f;
            }
        } else {
            this.maxAngle = 10.0f;
        }

        // Récupérer la propriété "slippery"
        Object slipperyProp = gameObject.getProperties().get("slippery");
        if (slipperyProp instanceof Boolean) {
            this.slippery = (Boolean) slipperyProp;
        } else if (slipperyProp instanceof String) {
            this.slippery = Boolean.parseBoolean((String) slipperyProp);
        } else {
            this.slippery = false;
        }

        // Récupération du lien via "linked_top"
        this.linkedTopName = gameObject.getProperty("linked_top", String.class);

        // Récupérer le sprite depuis le TMX (aucune valeur par défaut)
        String spriteName = gameObject.getProperty("sprite", String.class);
        if (spriteName == null) {
            System.out.println("⚠️ ATTENTION : UnstablePlatform n'a pas de sprite défini dans le TMX !");
            this.texture = null;
        } else {
            TextureAtlas atlas = TextureManager.getGameAtlas();
            this.texture = atlas.findRegion(spriteName);
            if (this.texture == null) {
                System.out.println("❌ ERREUR : Texture '" + spriteName + "' introuvable pour UnstablePlatform !");
            }
        }

        // Initialisation des angles
        this.currentAngle = 0f;
        this.targetAngle = 0f;
    }

    /**
     * Permet de lier dynamiquement la plateforme à un chain link.
     */
    public void setLinkedChain(ChainLink chain) {
        this.linkedChain = chain;
    }

    /**
     * Met à jour la plateforme instable.
     * Si le joueur se trouve sur la plateforme, ajuste le targetAngle selon la position du joueur.
     * Si le joueur n'est pas sur la plateforme, le targetAngle revient progressivement à 0.
     *
     * @param delta Temps écoulé depuis la dernière frame.
     * @param playerBounds Rectangle de collision du joueur.
     */
    public void update(float delta, Rectangle playerBounds) {
        // Optionnel : mettre à jour la position en fonction du chain link si lié
        if (linkedChain != null) {
            // Exemple : positionner la plateforme en fonction du chain link (centrée)
            this.x = linkedChain.getX() - (width / 2) + (linkedChain.getWidth() / 2);
            this.y = linkedChain.getY();
        }

        // Calculer le centre de la plateforme
        float centerX = x + width / 2;

        // Vérifier si le joueur est sur la plateforme
        Rectangle platformBounds = new Rectangle(x, y, width, height);
        if (platformBounds.overlaps(playerBounds)) {
            // Calculer le décalage horizontal entre le centre du joueur et le centre de la plateforme
            float playerCenterX = playerBounds.x + playerBounds.width / 2;
            float offset = playerCenterX - centerX;
            // Normaliser cet offset en fonction de la moitié de la largeur de la plateforme
            float normalizedOffset = offset / (width / 2);
            if (normalizedOffset > 1) normalizedOffset = 1;
            if (normalizedOffset < -1) normalizedOffset = -1;
            // Le targetAngle est proportionnel à l'offset et plafonné par maxAngle
            targetAngle = normalizedOffset * maxAngle;
        } else {
            // Sans joueur sur la plateforme, le targetAngle revient à 0
            targetAngle = 0f;
        }

        // Interpolation douce de l'angle courant vers targetAngle
        currentAngle += (targetAngle - currentAngle) * instability * delta;
    }

    /**
     * Affiche la plateforme instable avec rotation autour de son centre.
     */
    public void render(SpriteBatch batch) {
        if (texture == null) return;
        float originX = width / 2;
        float originY = height / 2;
        batch.draw(texture, x, y, originX, originY, width, height, 1, 1, currentAngle);
    }

    /**
     * Renvoie les limites de la plateforme pour la détection de collision.
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public String getLinkedTopName() {
        return linkedTopName;
    }

    public boolean isSlippery() {
        return slippery;
    }
}
