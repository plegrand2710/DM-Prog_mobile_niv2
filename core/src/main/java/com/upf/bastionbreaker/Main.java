package com.upf.bastionbreaker;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.upf.bastionbreaker.view.screens.SplashScreen;

public class Main extends Game {
    private SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        Gdx.app.log("DEBUGsplashScreen", "main");

        setScreen(new SplashScreen(this));
        Gdx.app.log("DEBUGsplashScreen", "screen lancé");

    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
