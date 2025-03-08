package com.upf.bastionbreaker.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.upf.bastionbreaker.model.graphics.TextureManager;

public class GameScreen implements Screen {
    private MapRenderer mapRenderer;
    private ParallaxBackground parallaxBackground;
    private SpriteBatch batch;

    @Override
    public void show() {
        TextureManager.load();
        mapRenderer = new MapRenderer("map/bastion_breaker_map.tmx");
        parallaxBackground = new ParallaxBackground(); // Ajout des 5 couches du parallaxe
        batch = new SpriteBatch();
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        parallaxBackground.update(mapRenderer.getCamera().position.x);
        parallaxBackground.render(batch);
        batch.end();

        mapRenderer.update(delta);
        mapRenderer.render();
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
        mapRenderer.dispose();
        batch.dispose();
    }
}
