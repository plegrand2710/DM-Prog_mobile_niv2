package com.upf.bastionbreaker.model.graphics;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureManager {
    private static AssetManager assetManager;

    public static void load() {
        assetManager = new AssetManager();

        // Charger les atlas
        assetManager.load("atlas/background/background.atlas", TextureAtlas.class);
        assetManager.load("atlas/game/game.atlas", TextureAtlas.class); // Ajout de l’`atlas` du jeu

        assetManager.finishLoading(); // Charge tout avant d’aller plus loin
    }

    public static TextureRegion getTextureRegion(String name) {
        TextureAtlas gameAtlas = getGameAtlas();
        if (gameAtlas != null) {
            TextureRegion region = gameAtlas.findRegion(name);
            if (region == null) {
                System.out.println("⚠️ Texture '" + name + "' introuvable dans game.atlas !");
            }
            return region;
        }
        System.out.println("❌ ERREUR : game.atlas non chargé !");
        return null;
    }

    public static TextureAtlas getBackgroundAtlas() {
        return assetManager.get("atlas/background/background.atlas", TextureAtlas.class);
    }

    public static TextureAtlas getGameAtlas() {
        return assetManager.get("atlas/game/game.atlas", TextureAtlas.class);
    }

    public static void dispose() {
        assetManager.dispose();
    }
}
