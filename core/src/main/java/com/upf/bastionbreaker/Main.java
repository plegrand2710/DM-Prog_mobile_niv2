package com.upf.bastionbreaker;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.upf.bastionbreaker.view.screens.GameScreen;
import com.upf.bastionbreaker.model.graphics.TextureManager;
import com.upf.bastionbreaker.view.animation.AnimationHandler;
import com.upf.bastionbreaker.view.screens.SplashScreen;

public class Main extends Game {
    @Override
    public void create() {
        try {
            // 🔹 Étape 1 : Charger les textures AVANT d'afficher GameScreen
            Gdx.app.log("Main", "🖼️ Chargement des assets...");
            TextureManager.load();

            // 🔹 Étape 2 : Vérifier si `game.atlas` est bien chargé
            if (TextureManager.getGameAtlas() == null) {
                Gdx.app.error("Main", "❌ ERREUR : `game.atlas` non chargé !");
                return;
            }

            // 🔹 Étape 3 : Charger les animations APRES les textures
            AnimationHandler.loadAnimations();

            // 🔹 Étape 4 : Définir l'écran principal après avoir tout chargé
            setScreen(new SplashScreen(this));
            Gdx.app.log("Main", "✅ GameScreen défini comme écran principal");

        } catch (Exception e) {
            Gdx.app.error("Main", "❌ ERREUR : Impossible d'initialiser GameScreen", e);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        TextureManager.dispose(); // 🔹 Libérer proprement les textures
        Gdx.app.log("Main", "🚀 Nettoyage du jeu terminé");
    }
}
