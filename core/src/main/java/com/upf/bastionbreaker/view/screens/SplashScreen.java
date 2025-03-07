package com.upf.bastionbreaker.view.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Timer;

public class SplashScreen implements Screen {
    private Game game;
    private SpriteBatch batch;
    private TextureAtlas backgroundAtlas;
    private TextureAtlas gameAtlas;
    private TextureRegion background;
    private TextureRegion logo;
    private TextureRegion loadBullet;
    private TextureRegion magazineDummy;

    private int bulletsLoaded = 0;
    private float timer = 0;

    public SplashScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        Gdx.app.log("DEBUGsplashScreen", "dans splash");

        backgroundAtlas = new TextureAtlas(Gdx.files.internal("atlas/background/background.atlas"));
        gameAtlas = new TextureAtlas(Gdx.files.internal("atlas/game/game.atlas"));
        Gdx.app.log("DEBUGsplashScreen", "chargement atlas");

        background = backgroundAtlas.findRegion("backgroundScreenResize");
        logo = gameAtlas.findRegion("bastion_breaker_logo");
        loadBullet = gameAtlas.findRegion("Load-Bullet");
        magazineDummy = gameAtlas.findRegion("Magazine-Dummy");

        Gdx.app.log("DEBUGsplashScreen", "textures chargées");
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                Gdx.app.log("DEBUGsplashScreen", "run");

                game.setScreen(new MenuScreen(game));
                Gdx.app.log("DEBUGsplashScreen", "menuscreen");

            }
        }, 3);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();

        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


        if (logo != null) {
            float logoScale = 0.6f;
            float logoWidth = logo.getRegionWidth() * logoScale;
            float logoHeight = logo.getRegionHeight() * logoScale;
            float logoX = (Gdx.graphics.getWidth() - logoWidth) / 2;
            float logoY = ((Gdx.graphics.getHeight() - logoHeight) / 2) + 100;
            batch.draw(logo, logoX, logoY, logoWidth, logoHeight);
        }


        float magazineX = (Gdx.graphics.getWidth() - magazineDummy.getRegionWidth()) / 2;
        float magazineY = (Gdx.graphics.getHeight() / 2) - 300;
        batch.draw(magazineDummy, magazineX, magazineY);


        timer += delta;
        if (timer > 0.3f && bulletsLoaded < 8) {
            bulletsLoaded++;
            timer = 0;
        }

        for (int i = 0; i < bulletsLoaded; i++) {
            float bulletX = magazineX + 20 + i * (loadBullet.getRegionWidth() + 5);
            float bulletY = (magazineY + 10) + 6;
            batch.draw(loadBullet, bulletX, bulletY);
        }

        batch.end();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        backgroundAtlas.dispose();
        gameAtlas.dispose();
    }
}
