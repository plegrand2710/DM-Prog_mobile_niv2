package com.upf.bastionbreaker.model.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.upf.bastionbreaker.model.map.GameObject;
import com.upf.bastionbreaker.model.graphics.TextureManager;
import com.upf.bastionbreaker.view.screens.MapRenderer;

public class FallingBlock {
    private float x, y, width, height;
    private TextureRegion texture;
    private int hp;
    private float fallSpeed;
    private boolean gravity; // false tant que le bloc est suspendu
    private boolean landed;  // devient true quand le bloc atterrit et se stabilise
    private boolean destroyed;

    // Référence au maillon auquel le bloc est attaché (issu de la propriété "linked_top")
    private ChainLink linkedChain;
    private String linkedTopName; // Nom du maillon auquel le bloc est attaché

    public FallingBlock(GameObject gameObject) {
        // Conversion des coordonnées de pixels en unités de tuiles
        this.x = gameObject.getX() / MapRenderer.TILE_SIZE;
        this.y = gameObject.getY() / MapRenderer.TILE_SIZE;
        this.width = gameObject.getWidth() / MapRenderer.TILE_SIZE;
        this.height = gameObject.getHeight() / MapRenderer.TILE_SIZE;

        // Initialisation des HP, par défaut 50
        Object hpProp = gameObject.getProperties().get("HP");
        if (hpProp instanceof Number) {
            this.hp = ((Number) hpProp).intValue();
        } else {
            this.hp = 50;
        }

        // Vitesse de chute (fall_speed), par défaut 7.0
        Object fallSpeedProp = gameObject.getProperties().get("fall_speed");
        if (fallSpeedProp instanceof Number) {
            this.fallSpeed = ((Number) fallSpeedProp).floatValue();
        } else if (fallSpeedProp instanceof String) {
            try {
                this.fallSpeed = Float.parseFloat((String) fallSpeedProp);
            } catch (NumberFormatException e) {
                this.fallSpeed = 7.0f;
            }
        } else {
            this.fallSpeed = 7.0f;
        }

        // La gravité est désactivée au départ (gravity=false)
        Object gravityProp = gameObject.getProperties().get("gravity");
        if (gravityProp instanceof Boolean) {
            this.gravity = (Boolean) gravityProp;
        } else if (gravityProp instanceof String) {
            this.gravity = Boolean.parseBoolean((String) gravityProp);
        } else {
            this.gravity = false;
        }

        this.landed = false;
        this.destroyed = false;

        // Récupération du maillon auquel le bloc est attaché (linked_top)
        this.linkedTopName = gameObject.getProperty("linked_top", String.class);

        // Récupérer le nom du sprite depuis le TMX sans utiliser de valeur par défaut
        String spriteName = gameObject.getProperty("sprite", String.class);
        if (spriteName == null) {
            System.out.println("⚠️ ATTENTION : FallingBlock n'a pas de sprite défini dans le TMX !");
            this.texture = null;
        } else {
            TextureAtlas atlas = TextureManager.getGameAtlas();
            this.texture = atlas.findRegion(spriteName);
            if (this.texture == null) {
                System.out.println("❌ ERREUR : Texture '" + spriteName + "' introuvable pour FallingBlock !");
            }
        }
    }

    /**
     * Permet de définir le maillon auquel ce bloc est attaché.
     * Cette méthode doit être appelée par le GameScreen pour lier le bloc au bon ChainLink.
     */
    public void setLinkedChain(ChainLink chain) {
        this.linkedChain = chain;
    }

    /**
     * Met à jour le FallingBlock :
     * - S'il n'est pas encore soumis à la gravité, il suit la position du maillon auquel il est attaché.
     * - Si le maillon attaché est détruit ou en chute, la gravité est activée.
     * - Lorsque gravity est true, le bloc tombe à la vitesse fallSpeed.
     * - Lorsqu'il atteint le sol (y ≤ 0), il atterrit, devient statique et indestructible.
     *
     * @param delta Temps écoulé depuis la dernière frame.
     */
    public void update(float delta) {
        if (destroyed) return;

        if (!gravity) {
            // Suivre la position du maillon attaché, s'il existe
            if (linkedChain != null) {
                // Si le maillon auquel il est attaché est détruit ou en chute, activer la gravité
                if (linkedChain.isDestroyed() || linkedChain.isFalling()) {
                    gravity = true;
                } else {
                    // Positionner le bloc juste en dessous du maillon
                    this.x = linkedChain.getX();
                    this.y = linkedChain.getY() - this.height;
                }
            }
        }

        // Si la gravité est activée et que le bloc n'a pas encore atterri, le faire tomber
        if (gravity && !landed) {
            this.y -= fallSpeed * delta;
            // Supposons que le sol est à y = 0
            if (this.y <= 0) {
                this.y = 0;
                landed = true;
                System.out.println("✅ FallingBlock a atterri et est devenu un pont solide.");
                // TODO : déclencher une animation d'atterrissage et jouer un effet sonore.
            }
        }
    }

    /**
     * Affiche le FallingBlock s'il n'est pas détruit.
     */
    public void render(SpriteBatch batch) {
        if (!destroyed && texture != null) {
            batch.draw(texture, x, y, width, height);
        }
    }

    /**
     * Retourne le rectangle de collision du FallingBlock.
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    /**
     * Applique des dégâts au FallingBlock tant qu'il n'a pas atterri.
     * Une fois atterri, le bloc devient indestructible.
     *
     * @param dmg La quantité de dégâts.
     */
    public void damage(int dmg) {
        if (!landed && !destroyed) {
            hp -= dmg;
            if (hp <= 0) {
                destroy();
            }
        }
    }

    /**
     * Détruit le FallingBlock, par exemple en cas d'attaque avant qu'il ne tombe.
     * Le bloc ne pourra plus être affiché ni interagir.
     */
    private void destroy() {
        destroyed = true;
        System.out.println("💥 FallingBlock détruit !");
        // TODO : déclencher une animation d'explosion et jouer un effet sonore.
    }

    public String getLinkedTopName() {
        return linkedTopName;
    }

    // Getters utiles
    public boolean isLanded() {
        return landed;
    }

    public boolean isDestroyed() {
        return destroyed;
    }
}
