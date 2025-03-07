package com.upf.bastionbreaker.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.upf.bastionbreaker.model.graphics.TextureManager;

public class GameScreen implements Screen {
    private MapRenderer mapRenderer;
    private ParallaxBackground parallaxBackground;
    private SpriteBatch batch;

    @Override
    public void show() {
        TextureManager.load(); // Charger les textures et atlas
        mapRenderer = new MapRenderer("map/bastion_breaker_map.tmx"); // Charger la carte .tmx
        parallaxBackground = new ParallaxBackground("background", 0.5f); // Effet de parallaxe
        batch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Mettre à jour et dessiner le fond parallaxe
        batch.begin();
        parallaxBackground.update(mapRenderer.getCamera().position.x);
        parallaxBackground.render(batch);
        batch.end();

        // Mettre à jour et dessiner la carte
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
