package com.upf.bastionbreaker;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.upf.bastionbreaker.view.screens.GameScreen;

public class Main extends Game {
    @Override
    public void create() {
        try {
            setScreen(new GameScreen("touchpad"));
            Gdx.app.log("Main", "✅ GameScreen défini comme écran principal");
        } catch (Exception e) {
            Gdx.app.error("Main", "❌ ERREUR : Impossible d'initialiser GameScreen", e);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        Gdx.app.log("Main", "🚀 Nettoyage du jeu terminé");
    }
}
