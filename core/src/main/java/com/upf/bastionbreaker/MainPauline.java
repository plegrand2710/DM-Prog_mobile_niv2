package com.upf.bastionbreaker;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.upf.bastionbreaker.model.graphics.TextureManager;
import com.upf.bastionbreaker.view.animation.AnimationHandler;
import com.upf.bastionbreaker.view.screens.GameScreen;
import com.upf.bastionbreaker.view.screens.SplashScreen;

public class MainPauline extends Game {
    private SpriteBatch batch;

//    @Override
//    public void create() {
//        batch = new SpriteBatch();
//        Gdx.app.log("DEBUGsplashScreen", "main");
//
//        setScreen(new SplashScreen(this));
//        Gdx.app.log("DEBUGsplashScreen", "screen lancé");
//
//    }
//
//    @Override
//    public void render() {
//        super.render();
//    }
//
//    @Override
//    public void dispose() {
//        batch.dispose();
//    }

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

            //GameScreen gameScreen = new GameScreen("touchpad");
            // 🔹 Étape 3 : Charger les animations APRES les textures
            AnimationHandler.loadAnimations();
            //setScreen(gameScreen);

            setScreen(new SplashScreen(this));

            Gdx.app.log("Main", "✅ GameScreen défini comme écran principal");

        } catch (Exception e) {
            Gdx.app.error("Main", "❌ ERREUR : Impossible d'initialiser GameScreen", e);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        TextureManager.dispose();
        Gdx.app.log("Main", "🚀 Nettoyage du jeu terminé");
    }
}
